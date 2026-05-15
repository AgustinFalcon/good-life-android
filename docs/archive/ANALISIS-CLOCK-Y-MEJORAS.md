# Análisis: Clock, AppLanguage y Arquitectura Actual

**Fecha:** 2026-02-03  
**Contexto:** Implementación del MainScaffold y sistema de tabs (Daily, Workouts, Meals, Settings)

---

## 1. SITUACIÓN ACTUAL

### 1.1 Problema Principal: Clock y Zona Horaria

#### Qué está pasando:
- La app muestra fechas incorrectas (día +1 del real)
- Ejemplo: Hoy es 3 de febrero, pero la app muestra "4"
- El calendario del sistema Android confirma que es 3 de febrero
- Cuando navegás a "Ayer", muestra "3" (debería ser "2")

#### Causa raíz:
```kotlin
// Implementación actual (PROBLEMÁTICA)
fun Clock.todayHere(): LocalDate {
    return java.time.LocalDate.now().toKotlinLocalDate()
}
```

**Diagnóstico:**
1. El emulador Android probablemente está configurado en UTC
2. A las 22:38 hora local (GMT-3 Argentina), en UTC ya son las 01:38 del día siguiente
3. `java.time.LocalDate.now()` sin parámetros usa el timezone del SISTEMA (no del usuario)
4. En un emulador sin timezone configurado, defaultea a UTC

#### Stack trace del problema:
- `DailyTabViewModel` inicializa con `clock.todayHere()` → devuelve fecha UTC (4/feb)
- `DateHeaderComponent` compara con `clock.todayHere()` → devuelve fecha UTC (4/feb)
- Resultado: "4 de febrero" se identifica como "Hoy" porque ambas usan UTC
- Pero el usuario ve en el reloj del sistema "3 de febrero"

---

## 2. USO ACTUAL DE CLOCK

### 2.1 Dónde se usa:

#### **Core (Utilities):**
- `core/datetime/LocalDateExtensions.kt`
  - `Clock.todayHere()` → Obtiene fecha actual
  - `LocalDate.isToday(clock)` → Verifica si es hoy
  - `LocalDate.isYesterday(clock)` → Verifica si es ayer
  - `LocalDate.isTomorrow(clock)` → Verifica si es mañana
  - `LocalDate.toFriendlyString(clock, language)` → Formatea con texto relativo

#### **Presentation (ViewModels):**
- `MainScaffoldViewModel(clock, language)` → Para formatear fecha en modal
- `DailyTabViewModel(clock, language)` → Para inicializar y validar fechas
- (Potencialmente) `MealsTabViewModel(clock, language)` → Similar a Daily

#### **Presentation (Components):**
- `DateHeaderComponent(date, clock, language, ...)` → Para comparar hoy/ayer
- Previews de componentes → Para testear con fechas reales

### 2.2 Por qué se usa:

**Ventajas teóricas:**
1. **Testabilidad:** Inyectar un `Clock` fake para tests deterministas
2. **KMP-Ready:** `kotlin.time.Clock` es multiplataforma
3. **Inmutabilidad:** No depende de estado global como `System.currentTimeMillis()`

**Problema práctico:**
- `kotlin.time.Clock` NO tiene concepto de timezone
- Solo maneja `Instant` (timestamp UTC)
- Para obtener `LocalDate` necesitás un `TimeZone`
- `TimeZone.currentSystemDefault()` NO funciona bien en Android

---

## 3. USO ACTUAL DE APPLANGUAGE

### 3.1 Dónde se usa:

#### **Core:**
- `core/datetime/language/AppLanguage.kt` → Define idioma (Spanish/English)
- `core/datetime/language/DateFormats.kt` → Formatos de fecha por idioma
- `core/datetime/language/RelativeTexts.kt` → "Hoy", "Ayer", "Mañana"
- `core/datetime/language/DayNames.kt` → Nombres de días
- `core/datetime/language/MonthNames.kt` → Nombres de meses

#### **Presentation:**
- `MainScaffoldViewModel` → Para formatear fecha del modal
- `DailyTabViewModel` → Para formatear headers
- `MealsTabViewModel` → Similar
- Componentes que muestran fechas

### 3.2 Evaluación:

**Pros:**
✅ Centralizado y consistente  
✅ Type-safe (no strings sueltos)  
✅ KMP-Ready (no depende de Android Resources)  
✅ Fácil de extender a más idiomas

**Contras:**
⚠️ Requiere pasar `language` a muchos componentes  
⚠️ No integra con locale del sistema Android automáticamente  
⚠️ Duplica funcionalidad que Android ya tiene (DateFormat, Locale)

**Recomendación:** ✅ **MANTENER** - Es una buena abstracción para KMP

---

## 4. ARQUITECTURA ACTUAL: OWNER PATTERN

### 4.1 Implementación:

```
MainScaffoldScreenOwner (ViewModel injection)
    ↓ collectAsState
MainScaffoldScreen (Pure UI)
    ↓ TabNavHost
    ↓ navigation graphs
DailyScreenOwner (ViewModel injection)
    ↓ collectAsState
DailyScreen (Pure UI)
```

### 4.2 Evaluación:

**Pros:**
✅ Separación clara de responsabilidades  
✅ UI pura (fácil de previsualizar)  
✅ ViewModels testeables sin Compose  
✅ Consistente en todo el proyecto

**Contras:**
⚠️ Verboso (2 archivos por pantalla)  
⚠️ `getClock()` expuesto para pasar a UI (rompe encapsulación)

**Problema específico:**
```kotlin
// En DailyTabViewModel
fun getClock(): Clock = clock  // ❌ Expone dependencia interna

// En DailyScreenOwner
DailyScreen(
    uiState = uiState,
    onAction = viewModel::onAction,
    clock = viewModel.getClock(),  // ❌ UI accede a Clock del ViewModel
    language = viewModel.language   // ❌ UI accede a Language del ViewModel
)
```

**Por qué es problemático:**
- La UI no debería saber qué es un `Clock` o `AppLanguage`
- Estos son detalles de implementación del ViewModel
- Si querés formatear fechas en la UI, el ViewModel debería darle el string ya formateado

---

## 5. PROBLEMAS IDENTIFICADOS

### 5.1 Clock y Timezone (CRÍTICO ❌)

**Problema:**
`kotlinx.datetime` no maneja bien timezones del dispositivo en Android. El emulador reporta fecha UTC en lugar de local.

**Soluciones posibles:**

#### Opción A: Eliminar Clock, usar java.time directamente
```kotlin
// En ViewModels
private val today = java.time.LocalDate.now()

// En extensiones
fun LocalDate.isToday(): Boolean = 
    this == java.time.LocalDate.now().toKotlinLocalDate()
```
**Pros:** Simple, funciona siempre  
**Contras:** ❌ No testeable, ❌ No KMP

#### Opción B: Wrapper propio de Clock con timezone
```kotlin
interface DateProvider {
    fun today(): LocalDate
    fun now(): Instant
}

class AndroidDateProvider : DateProvider {
    override fun today() = java.time.LocalDate.now().toKotlinLocalDate()
    override fun now() = Clock.System.now()
}

class FakeDateProvider(private val fixedDate: LocalDate) : DateProvider {
    override fun today() = fixedDate
    override fun now() = TODO()
}
```
**Pros:** ✅ Testeable, ✅ Funciona en Android  
**Contras:** ⚠️ No KMP (necesita expect/actual), más boilerplate

#### Opción C: Configurar timezone manualmente en Clock
```kotlin
// En AppModule
single { 
    TimeZone.of("America/Argentina/Buenos_Aires") 
}

fun Clock.todayHere(timeZone: TimeZone): LocalDate = 
    now().toLocalDateTime(timeZone).date
```
**Pros:** ✅ KMP-Ready, ✅ Testeable  
**Contras:** ⚠️ Requiere pasar TimeZone a muchos lados

---

### 5.2 @OptIn(ExperimentalTime) en todos lados (MOLESTO ⚠️)

**Problema:**
`kotlin.time.Clock` requiere `@OptIn(ExperimentalTime::class)` en CADA archivo que lo usa.

**Impacto:**
- `DailyTabViewModel.kt`
- `MealsTabViewModel.kt`
- `MainScaffoldViewModel.kt`
- `LocalDateExtensions.kt`
- `DateHeaderComponent.kt`
- Todos los previews
- Todos los tests

**Solución:**
Desde Kotlin 1.6, `kotlin.time` es estable. Pero `Clock` sigue siendo experimental.

**Opciones:**
1. Aceptarlo (es solo una anotación)
2. Crear un wrapper no-experimental
3. Usar `System.currentTimeMillis()` + conversión manual

---

### 5.3 ViewModels exponiendo Clock/Language (DESIGN ISSUE ⚠️)

**Problema actual:**
```kotlin
// ViewModel expone detalles internos
class DailyTabViewModel(private val clock: Clock, val language: AppLanguage) {
    fun getClock(): Clock = clock  // ❌
}

// UI accede a ellos
DailyScreen(
    uiState = uiState,
    clock = viewModel.getClock(),   // ❌
    language = viewModel.language    // ❌
)
```

**Por qué está mal:**
- Rompe encapsulación: la UI no debería saber cómo se calculan fechas
- Dificulta testing: mock de Clock es complejo
- Acoplamiento: si cambiás Clock, rompés la UI

**Solución ideal:**
```kotlin
// ViewModel formatea TODO internamente
data class DailyUiState(
    val date: LocalDate,
    val dateFormatted: String,        // "Hoy" / "Ayer" / "Lun, 08 de feb"
    val dayNumber: Int,                // 3
    val monthYear: String,             // "Febrero 2026"
    val isToday: Boolean,
    val isYesterday: Boolean
)

// UI solo muestra
DailyScreen(uiState = uiState, onAction = onAction)
```

---

## 6. ARQUITECTURA GLOBAL: RESUMEN

### 6.1 Estructura actual:

```
Presentation Layer
├── screen/
│   ├── main/
│   │   ├── MainScaffoldScreenOwner.kt (Owner)
│   │   ├── MainScaffoldScreen.kt (Pure UI)
│   │   ├── MainScaffoldViewModel.kt
│   │   └── model/
│   │       ├── MainScaffoldUiState.kt
│   │       └── MainScaffoldUiAction.kt
│   └── tabs/
│       ├── daily/
│       │   ├── DailyScreenOwner.kt (Owner)
│       │   ├── DailyScreen.kt (Pure UI)
│       │   ├── DailyTabViewModel.kt
│       │   └── model/
│       ├── workouts/ (placeholders)
│       ├── meals/ (placeholders)
│       └── more/ (placeholders)
│
├── components/
│   ├── header/
│   │   ├── DateHeaderComponent.kt
│   │   ├── TabRowHeaderComponent.kt
│   │   └── CalendarDayIcon.kt
│   ├── bottom/
│   │   └── BottomNavigationComponent.kt
│   └── modal/
│       └── AddActionModalComponent.kt
│
└── navigation/
    ├── core/ (ComposeNavigationController)
    ├── host/ (SmartNavHost)
    └── route/
        ├── AppRoute.kt (Splash, Login, Register, Main)
        ├── AppGraph.kt
        ├── TabRoute.kt (Daily, Workouts, Meals, Settings + detalles)
        └── TabNavGraph.kt
```

### 6.2 Niveles de navegación:

```
Nivel 1 (App): Splash → Login → Register → Main
    ↓ (ComposeNavigationController - SharedFlow)
    
Nivel 2 (Tabs): Daily ⇄ Workouts ⇄ Meals ⇄ Settings
    ↓ (NavHostController dentro de MainScaffold)
    
Nivel 3 (Sub-tabs): DailyList → DailyDetail
    ↓ (NavHostController dentro de cada TabGraph)
```

**Evaluación:** ✅ Arquitectura sólida, bien separada

---

## 7. ISSUES ACTUALES Y PROPUESTAS

### Issue #1: Clock no respeta timezone del dispositivo Android

**Severidad:** 🔴 CRÍTICA  
**Impacto:** App muestra fechas incorrectas

**Opciones:**

| Opción | Testeable | KMP | Complejidad | Recomendación |
|--------|-----------|-----|-------------|---------------|
| A) Eliminar Clock, usar `java.time` directo | ❌ | ❌ | Baja | ⭐ Prototipos |
| B) Wrapper `DateProvider` con expect/actual | ✅ | ✅ | Media | ⭐⭐⭐ RECOMENDADO |
| C) Inyectar `TimeZone` junto con `Clock` | ✅ | ✅ | Alta | ⭐⭐ Alternativa |
| D) Usar `Instant` + formatear en ViewModel | ✅ | ✅ | Media | ⭐⭐ Similar a B |

---

### Issue #2: @OptIn(ExperimentalTime) en todos lados

**Severidad:** 🟡 MOLESTO  
**Impacto:** Contaminación visual del código

**Opciones:**

1. **Aceptarlo** (1 línea por archivo)
   - Es estándar en Kotlin experimental APIs
   - No afecta performance ni compilación

2. **Wrapper no-experimental**
   ```kotlin
   interface ClockProvider {
       fun now(): Instant
   }
   
   class SystemClockProvider : ClockProvider {
       @OptIn(ExperimentalTime::class)
       override fun now() = Clock.System.now()
   }
   ```
   - Encapsula el `@OptIn` en un solo lugar
   - Resto del código no necesita anotación

3. **Migrar a java.time (Android 26+)**
   - API estable, sin anotaciones
   - ❌ No KMP

**Recomendación:** Si vas con KMP → Opción 2 (wrapper). Si no → java.time.

---

### Issue #3: ViewModels exponen Clock y Language a la UI

**Severidad:** 🟡 CODE SMELL  
**Impacto:** Acoplamiento innecesario, dificulta testing

**Problema:**
```kotlin
// ❌ ACTUAL: UI conoce Clock y Language
DailyScreen(
    uiState = uiState,
    onAction = onAction,
    clock = viewModel.getClock(),    // ❌ Expone implementación
    language = viewModel.language     // ❌ Expone implementación
)
```

**Solución recomendada:**
```kotlin
// ✅ PROPUESTO: ViewModel formatea TODO
data class DailyUiState(
    val date: LocalDate,              // Para navegación interna
    val headerText: String,           // "Hoy" | "Ayer" | "Lun, 08 feb"
    val dayNumber: Int,               // 3
    val monthYear: String,            // "Febrero 2026"
    val showRelativeText: Boolean     // true si es Hoy/Ayer/Mañana
)

// UI solo renderiza
DailyScreen(uiState = uiState, onAction = onAction)
```

**Ventajas:**
- ✅ UI 100% pura (sin lógica de fechas)
- ✅ Tests del ViewModel son completos (incluyen formateo)
- ✅ Fácil cambiar Clock sin tocar UI
- ✅ Previews de UI más simples (datos mock directos)

---

## 8. PLAN DE REFACTOR PROPUESTO

### Fase 1: Solucionar Clock (URGENTE)

**Objetivo:** Que la app muestre fechas correctas YA.

**Quick fix (temporal):**
```kotlin
// En LocalDateExtensions.kt
fun Clock.todayHere(): LocalDate {
    return java.time.LocalDate.now(
        java.time.ZoneId.systemDefault()  // ← Explícito
    ).toKotlinLocalDate()
}
```

**Solución definitiva (recomendada):**
```kotlin
// 1) Crear abstracción
// core/datetime/DateProvider.kt
interface DateProvider {
    fun today(): LocalDate
    fun yesterday(): LocalDate
    fun tomorrow(): LocalDate
}

// 2) Implementación Android
// core/datetime/AndroidDateProvider.kt
class AndroidDateProvider : DateProvider {
    override fun today() = 
        java.time.LocalDate.now().toKotlinLocalDate()
    
    override fun yesterday() = 
        today().minus(1, DateTimeUnit.DAY)
    
    override fun tomorrow() = 
        today().plus(1, DateTimeUnit.DAY)
}

// 3) Registrar en Koin
single<DateProvider> { AndroidDateProvider() }

// 4) Inyectar en ViewModels
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DailyUiState(date = dateProvider.today())
    )
}
```

**Testing:**
```kotlin
class FakeDateProvider(
    private val fixedToday: LocalDate
) : DateProvider {
    override fun today() = fixedToday
    override fun yesterday() = fixedToday.minus(1, DateTimeUnit.DAY)
    override fun tomorrow() = fixedToday.plus(1, DateTimeUnit.DAY)
}
```

---

### Fase 2: Eliminar Clock/Language de la UI

**Objetivo:** ViewModels formatean todo, UI solo renderiza.

**Cambios:**

#### DailyUiState (ANTES):
```kotlin
data class DailyUiState(
    val date: LocalDate  // ← Solo la fecha cruda
)
```

#### DailyUiState (DESPUÉS):
```kotlin
data class DailyUiState(
    val date: LocalDate,              // Para lógica interna
    val dayNumber: Int,               // 3
    val headerText: String,           // "Hoy" / "Ayer" / "Lun, 08 feb"
    val monthYear: String,            // "Febrero 2026" (null si es hoy/ayer)
    val showFullDate: Boolean         // false si es hoy/ayer
)
```

#### DailyTabViewModel actualizado:
```kotlin
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildUiState(dateProvider.today()))
    val uiState: StateFlow<DailyUiState> = _uiState.asStateFlow()

    private fun buildUiState(date: LocalDate): DailyUiState {
        val today = dateProvider.today()
        return DailyUiState(
            date = date,
            dayNumber = date.dayOfMonth,
            headerText = when {
                date == today -> language.relativeTexts.today
                date == today.minus(1, DateTimeUnit.DAY) -> language.relativeTexts.yesterday
                date == today.plus(1, DateTimeUnit.DAY) -> language.relativeTexts.tomorrow
                else -> date.format(language.formats.dayNameAndDate)
            },
            monthYear = "${language.monthNames.names[date.monthNumber - 1]} ${date.year}",
            showFullDate = date != today && 
                          date != today.minus(1, DateTimeUnit.DAY) && 
                          date != today.plus(1, DateTimeUnit.DAY)
        )
    }

    private fun updateDate(days: Int) {
        val newDate = _uiState.value.date.plus(days, DateTimeUnit.DAY)
        _uiState.value = buildUiState(newDate)
    }
}
```

#### DailyScreen simplificado:
```kotlin
@Composable
fun DailyScreen(
    uiState: DailyUiState,
    onAction: (DailyUiAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SimpleDateHeader(
            dayNumber = uiState.dayNumber,
            headerText = uiState.headerText,
            monthYear = if (uiState.showFullDate) uiState.monthYear else null,
            onPreviousDay = { onAction(DailyUiAction.OnPreviousDay) },
            onNextDay = { onAction(DailyUiAction.OnNextDay) }
        )
        // ... resto
    }
}
```

**Ventajas:**
- ✅ `DailyScreen` ya NO necesita `clock` ni `language`
- ✅ Previews más simples (mock directo del UiState)
- ✅ Tests del ViewModel cubren el formateo de fechas
- ✅ Cambiar lógica de fechas NO toca la UI

---

### Fase 3: Simplificar DateHeaderComponent

**Objetivo:** Componente stateless que solo renderiza.

**ANTES:**
```kotlin
DateHeaderComponent(
    date: LocalDate,      // ← Fecha cruda
    clock: Clock,         // ← Lógica
    language: AppLanguage // ← Lógica
)
```

**DESPUÉS:**
```kotlin
DateHeaderComponent(
    dayNumber: Int,           // 3
    headerText: String,       // "Hoy"
    monthYear: String?,       // "Febrero 2026" o null
    showFullDate: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
)
```

**Ventajas:**
- ✅ 0 lógica en componente
- ✅ Fácil de previsualizar
- ✅ Reutilizable (no depende de Clock/Language)

---

## 9. COMPARACIÓN DE ENFOQUES

### Enfoque A: Eliminar Clock completamente (RÁPIDO)

**Código:**
```kotlin
// LocalDateExtensions.kt
fun todayHere(): LocalDate = 
    java.time.LocalDate.now().toKotlinLocalDate()

fun LocalDate.isToday(): Boolean = 
    this == todayHere()

// ViewModels
class DailyTabViewModel(val language: AppLanguage) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DailyUiState(date = todayHere())
    )
}
```

**Pros:**
- ✅ Funciona inmediatamente
- ✅ Simple, sin abstracciones
- ✅ No más `@OptIn(ExperimentalTime)`

**Contras:**
- ❌ No testeable (fecha siempre es la real)
- ❌ No KMP (depende de java.time)

**Cuándo usarlo:** Si NO vas a hacer KMP y NO necesitás tests con fechas fijas.

---

### Enfoque B: DateProvider (RECOMENDADO)

**Código:**
```kotlin
// Abstracción
interface DateProvider {
    fun today(): LocalDate
}

// Android
class AndroidDateProvider : DateProvider {
    override fun today() = 
        java.time.LocalDate.now().toKotlinLocalDate()
}

// Testing
class FakeDateProvider(val fixedDate: LocalDate) : DateProvider {
    override fun today() = fixedDate
}

// DI
single<DateProvider> { AndroidDateProvider() }

// ViewModels
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    val language: AppLanguage
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DailyUiState(date = dateProvider.today())
    )
}
```

**Pros:**
- ✅ Testeable (inyectar FakeDateProvider)
- ✅ Funciona en Android
- ✅ Puede ser KMP con expect/actual
- ✅ Simple de entender

**Contras:**
- ⚠️ Más código (1 interfaz + 2 implementaciones)

**Cuándo usarlo:** Si querés tests deterministas O KMP en el futuro.

---

### Enfoque C: Clock + TimeZone explícito (KMP PURO)

**Código:**
```kotlin
// Koin
single { TimeZone.currentSystemDefault() }
single { Clock.System }

// Extensions
fun Clock.todayIn(zone: TimeZone): LocalDate = 
    now().toLocalDateTime(zone).date

// ViewModels
class DailyTabViewModel(
    private val clock: Clock,
    private val timeZone: TimeZone,
    val language: AppLanguage
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DailyUiState(date = clock.todayIn(timeZone))
    )
}
```

**Pros:**
- ✅ 100% KMP
- ✅ Testeable (inyectar TimeZone fake)

**Contras:**
- ❌ Más verboso (2 parámetros siempre)
- ⚠️ `TimeZone.currentSystemDefault()` SIGUE fallando en Android
- ⚠️ `@OptIn(ExperimentalTime)` sigue presente

**Cuándo usarlo:** Solo si necesitás KMP desde día 1 y no te importa la complejidad.

---

## 10. RECOMENDACIÓN FINAL

### Quick Fix (HOY mismo):

```kotlin
// LocalDateExtensions.kt
fun Clock.todayHere(): LocalDate {
    return java.time.LocalDate.now(
        java.time.ZoneId.systemDefault()
    ).toKotlinLocalDate()
}
```

Esto **debería** funcionar porque `ZoneId.systemDefault()` es más robusto que `TimeZone.currentSystemDefault()`.

---

### Refactor recomendado (próxima sesión):

**1) Crear DateProvider** (Fase 1)
- Elimina dependencia de `Clock`
- Testeable con `FakeDateProvider`
- KMP-ready con expect/actual si hace falta

**2) Mover formateo a ViewModels** (Fase 2)
- `UiState` contiene strings ya formateados
- UI no conoce Clock ni Language
- Componentes más reutilizables

**3) Simplificar componentes** (Fase 3)
- `DateHeaderComponent` solo recibe strings
- Fácil de previsualizar
- 0 lógica de fechas

---

## 11. PREGUNTAS PARA LA OTRA IA

1. **Clock vs DateProvider:** ¿Cuál preferís para un proyecto Android que PODRÍA ser KMP en el futuro?

2. **Timezone en emuladores:** ¿Cómo garantizar que `TimeZone.currentSystemDefault()` funcione en emuladores?

3. **UI con Clock:** ¿Está bien que componentes reciban `Clock` o deberían recibir solo strings formateados?

4. **@OptIn(ExperimentalTime):** ¿Vale la pena wrappear Clock para evitar esta anotación en todo el código?

5. **java.time vs kotlinx.datetime:** Para Android-first (con KMP potencial), ¿qué recomendás?

6. **Estado del date en UiState:** ¿`LocalDate` crudo + formateo en UI, o strings ya formateados del ViewModel?

---

## 12. CÓDIGO COMPLETO DEL QUICK FIX

Si querés probar el quick fix antes de refactorizar todo:

```kotlin
// LocalDateExtensions.kt
@file:OptIn(ExperimentalTime::class)

package com.agusstkd.goodlife.core.datetime

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import kotlin.time.ExperimentalTime

/**
 * Obtiene la fecha de hoy en la zona horaria local del sistema.
 * 
 * Usa java.time.LocalDate con ZoneId explícito porque
 * kotlinx.datetime.TimeZone.currentSystemDefault() 
 * no funciona correctamente en emuladores Android.
 */
fun Clock.todayHere(): LocalDate {
    return java.time.LocalDate.now(
        java.time.ZoneId.systemDefault()
    ).toKotlinLocalDate()
}

// ... resto igual
```

---

## 13. IMPACTO DE CADA SOLUCIÓN

| Solución | Archivos a cambiar | Líneas de código | Tiempo estimado | Riesgo |
|----------|-------------------|------------------|-----------------|---------|
| Quick fix timezone | 1 archivo | 5 líneas | 5 min | Bajo |
| DateProvider | 3 archivos | ~80 líneas | 30 min | Medio |
| Mover formateo a VM | 6 archivos | ~200 líneas | 2 horas | Alto |
| Refactor completo | 15+ archivos | ~500 líneas | 1 día | Alto |

---

## 14. ESTADO ACTUAL DEL PROYECTO

### ✅ Funcionando:
- Navegación app-level (Splash → Login → Main)
- Navegación tab-level (Daily ⇄ Workouts ⇄ Meals ⇄ Settings)
- Bottom Navigation
- Modal de acciones rápidas
- Owner pattern consistente
- Koin DI configurado

### ⚠️ Parcialmente funcionando:
- Daily tab (funciona pero fechas incorrectas)

### ❌ Pendiente:
- Contenido de WorkoutsTab
- Contenido de MealsTab
- Contenido de MoreTab (Settings)
- Pantallas de detalle (TaskDetail, WorkoutDetail, MealDetail, Profile)
- Funcionalidad del modal de acciones (onClick navegación)

---

## 15. DECISIÓN REQUERIDA

Antes de seguir implementando tabs y detalles, necesitás decidir:

**Opción 1: Quick fix y seguir** (⏱️ 5 minutos)
- Aplicar el quick fix del timezone
- Implementar resto de tabs con la arquitectura actual
- Refactorizar Clock/formateo después

**Opción 2: Refactor DateProvider primero** (⏱️ 30 minutos)
- Crear `DateProvider` abstraction
- Migrar ViewModels actuales
- Continuar con resto de tabs usando el nuevo patrón

**Opción 3: Refactor completo** (⏱️ 1 día)
- DateProvider
- Mover formateo a ViewModels
- Simplificar componentes
- Luego implementar resto de tabs

---

## RESUMEN EJECUTIVO

**Problema principal:** `kotlinx.datetime.TimeZone.currentSystemDefault()` no respeta timezone del dispositivo Android, devuelve UTC.

**Impacto:** App muestra fecha del día siguiente cuando son después de las 21:00 (GMT-3).

**Solución inmediata:** Usar `java.time.LocalDate.now(ZoneId.systemDefault())` explícitamente.

**Deuda técnica actual:**
1. Clock experimental con `@OptIn` en todos lados
2. ViewModels exponiendo Clock/Language a la UI
3. Componentes con lógica de fechas (no solo rendering)

**Decisión arquitectónica:**
- Si KMP es prioridad → Refactor a DateProvider (expect/actual)
- Si no → Eliminar Clock, usar java.time directo
- En cualquier caso → Mover formateo a ViewModels (mejor separación)

**Recomendación:** Discutir con otra IA si Clock vale la pena para este proyecto específico.
