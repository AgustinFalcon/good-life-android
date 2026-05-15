# 🚀 INSTRUCCIONES DE VALIDACIÓN - DateProvider Implementation

**Fecha:** 2026-02-03  
**Estado:** ✅ CÓDIGO IMPLEMENTADO - LISTO PARA VALIDAR

---

## ¿QUÉ SE IMPLEMENTÓ?

Se migró de `Clock` directo a **DateProvider Pattern**:

### ✅ Archivos creados (3):
1. `core/datetime/DateProvider.kt` - Interface
2. `core/datetime/RealDateProvider.kt` - Implementación Android
3. `app/src/test/.../FakeDateProvider.kt` - Para tests

### ✅ Archivos modificados (11):
1. `di/AppModule.kt` - Koin DI
2. `MainScaffoldViewModel.kt`
3. `DailyTabViewModel.kt`
4. `DailyUiState.kt` - Expandido con campos formateados
5. `DailyScreenOwner.kt`
6. `DailyScreen.kt`
7. `DateHeaderComponent.kt` - Simplificado
8. `LocalDateExtensions.kt` - Vaciado

---

## PASO 1: CLEAN BUILD (OBLIGATORIO)

```bash
# En Android Studio:
1. Build → Clean Project
2. Esperar a que termine
3. Build → Rebuild Project
4. Esperar a que compile
```

**⏱️ Tiempo estimado:** 1-2 minutos

**⚠️ Importante:** NO saltes este paso. El cache viejo de APK puede causar errores fantasma.

---

## PASO 2: VERIFICAR COMPILACIÓN

### ✅ Checklist de compilación:

- [ ] El proyecto compila sin errores
- [ ] NO hay errores de "No definition found for type 'Clock'"
- [ ] NO hay errores de "Unresolved reference: todayHere"
- [ ] NO hay errores de "Unresolved reference: isToday"

**Si hay errores:**
1. File → Invalidate Caches / Restart
2. Reiniciar Android Studio
3. Rebuild Project

---

## PASO 3: EJECUTAR LA APP

```bash
# En Android Studio:
1. Desinstalar la app del emulador (importante)
2. Run 'app'
3. Iniciar sesión (si es necesario)
4. Navegar al Daily tab
```

---

## PASO 4: VALIDAR FECHA CORRECTA

### ✅ Verificaciones visuales:

#### 1. Fecha inicial correcta
**Qué revisar:**
- Al abrir el Daily tab, debe mostrar la fecha de HOY del sistema
- Si hoy es **3 de febrero**, el icono debe mostrar **"3"**
- El texto debe decir **"Hoy"**

**❌ Si muestra "4" cuando hoy es "3":**
- El bug NO se resolvió
- Revisar logs (ver Paso 5)

#### 2. Navegación entre días
**Qué revisar:**
- Tocar "día anterior" (flecha izquierda)
  - Debe mostrar "Ayer"
  - Si hoy es 3, debe mostrar "2" en el icono
- Tocar "día siguiente" (flecha derecha)
  - Debe mostrar "Mañana"
  - Si hoy es 3, debe mostrar "4" en el icono

#### 3. Textos relativos
**Qué revisar:**
- **Hoy:** Muestra "Hoy" sin mes/año
- **Ayer:** Muestra "Ayer" sin mes/año
- **Mañana:** Muestra "Mañana" sin mes/año
- **Otros días:** Muestra "Lun, 08 feb" + "Febrero 2026"

---

## PASO 5: REVISAR LOGS (OPCIONAL)

Si algo sale mal, revisar logs:

```bash
# En Logcat (Android Studio):
1. Filtrar por "DailyTabViewModel"
2. Buscar mensajes de error
3. Copiar y pegar el error completo
```

**Logs esperados:**
```
# NO debería haber logs de DailyTabViewModel
# (Se eliminó el log temporal de "FECHA INICIAL")
```

---

## PASO 6: VALIDAR ARQUITECTURA

### ✅ Checklist de código limpio:

#### 1. ViewModels NO exponen Clock ni Language
```kotlin
// ❌ ANTES
class DailyTabViewModel(...) {
    fun getClock(): Clock = clock  // ❌ NO DEBE EXISTIR
    val language: AppLanguage      // ❌ NO DEBE SER PÚBLICO
}

// ✅ AHORA
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage  // Privado
) : ViewModel()
```

**Verificar:**
- [ ] NO hay método `getClock()` en DailyTabViewModel
- [ ] `language` es privado (no `val`, solo `private val`)

#### 2. Screens NO reciben Clock ni Language
```kotlin
// ❌ ANTES
fun DailyScreen(
    uiState: DailyUiState,
    onAction: (DailyUiAction) -> Unit,
    clock: Clock,          // ❌ NO DEBE EXISTIR
    language: AppLanguage  // ❌ NO DEBE EXISTIR
)

// ✅ AHORA
fun DailyScreen(
    uiState: DailyUiState,
    onAction: (DailyUiAction) -> Unit
)
```

**Verificar:**
- [ ] `DailyScreen` solo recibe `uiState` y `onAction`
- [ ] `DateHeaderComponent` solo recibe strings (no Clock/Language)

#### 3. @OptIn solo en RealDateProvider
```kotlin
// ✅ ÚNICO lugar con @OptIn(ExperimentalTime)
@file:OptIn(ExperimentalTime::class)
package ...
class RealDateProvider : DateProvider { ... }
```

**Verificar:**
- [ ] `RealDateProvider.kt` tiene `@OptIn(ExperimentalTime)`
- [ ] `AppModule.kt` NO tiene `@OptIn`
- [ ] `DailyTabViewModel.kt` NO tiene `@OptIn`
- [ ] `DailyScreen.kt` NO tiene `@OptIn`
- [ ] `DateHeaderComponent.kt` NO tiene `@OptIn`

---

## PASO 7: TESTS (OPCIONAL)

Si querés validar tests:

```kotlin
// En DailyTabViewModelTest.kt (crear si no existe)
@Test
fun `al inicializar debe mostrar fecha de hoy`() {
    // Given
    val fixedDate = LocalDate(2025, 12, 25)
    val fakeProvider = FakeDateProvider(fixedDate)
    
    // When
    val viewModel = DailyTabViewModel(fakeProvider, AppLanguage.Spanish)
    
    // Then
    assertEquals(fixedDate, viewModel.uiState.value.date)
    assertEquals(25, viewModel.uiState.value.dayNumber)
    assertEquals("Hoy", viewModel.uiState.value.headerText)
}
```

---

## RESULTADOS ESPERADOS ✅

### Si todo está bien:

✅ **Fecha correcta:**
- Hoy es 3 → muestra "3" (no "4")
- Navegación funciona correctamente

✅ **Sin errores de compilación:**
- No hay "No definition found for Clock"
- No hay "Unresolved reference: todayHere"

✅ **Arquitectura limpia:**
- ViewModels NO exponen Clock/Language
- Screens NO reciben Clock/Language
- @OptIn solo en RealDateProvider

✅ **Testeable:**
- Puedes escribir tests con FakeDateProvider
- Tests con fechas fijas funcionan

---

## SI ALGO FALLA

### Error 1: "No definition found for type 'DateProvider'"

**Solución:**
```bash
1. Verificar que DateProvider.kt existe en core/datetime/
2. Verificar que RealDateProvider.kt existe
3. Verificar que AppModule.kt tiene: single<DateProvider> { RealDateProvider() }
4. Build → Clean Project
5. Build → Rebuild Project
```

---

### Error 2: Fecha sigue incorrecta (+1 día)

**Solución:**
```bash
1. Desinstalar app del emulador
2. File → Invalidate Caches / Restart
3. Rebuild Project
4. Run 'app'
```

Si persiste:
```bash
# Verificar que RealDateProvider usa timeZone correcto:
override fun today() = clock.now().toLocalDateTime(timeZone).date
```

---

### Error 3: "Unresolved reference: todayHere"

**Causa:** Algún archivo sigue usando la función eliminada.

**Solución:**
```bash
# Buscar referencias:
cd app/src/main/java
grep -r "todayHere" .

# Si encuentra referencias, reemplazar con:
clock.todayHere() → dateProvider.today()
```

---

### Error 4: "Unresolved reference: isToday"

**Causa:** Algún archivo sigue usando la función eliminada.

**Solución:**
```bash
# Reemplazar:
date.isToday(clock) → date == dateProvider.today()
date.isYesterday(clock) → date == dateProvider.yesterday()
date.isTomorrow(clock) → date == dateProvider.tomorrow()
```

---

## CHECKLIST FINAL ✅

Antes de continuar con otras features, verifica:

- [ ] App compila sin errores
- [ ] App muestra la fecha correcta (no +1 día)
- [ ] Navegación entre días funciona
- [ ] Textos relativos funcionan ("Hoy", "Ayer", "Mañana")
- [ ] NO hay @OptIn fuera de RealDateProvider.kt
- [ ] ViewModels NO exponen Clock/Language
- [ ] Screens NO reciben Clock/Language
- [ ] UiState contiene strings formateados

---

## PRÓXIMOS PASOS (DESPUÉS DE VALIDAR)

Una vez que todo esté funcionando:

### 1. Implementar otros tabs
- WorkoutsTab (usar DateProvider)
- MealsTab (usar DateProvider)
- SettingsTab

### 2. Escribir tests
- DailyTabViewModelTest con FakeDateProvider
- MainScaffoldViewModelTest con FakeDateProvider

### 3. Features pendientes
- Pantallas de detalle (TaskDetail, WorkoutDetail, MealDetail)
- Funcionalidad del modal de acciones rápidas
- Navegación entre pantallas de detalle

---

## DOCUMENTACIÓN COMPLETA

Consultar:
- `ANALISIS-CLOCK-Y-MEJORAS.md` - Análisis detallado del problema
- `DATEPROVIDER-IMPLEMENTACION-COMPLETA.md` - Resumen de cambios

---

## CONTACTO CON LA IA

Si algo falla después de seguir estos pasos:

**Información a proporcionar:**
1. Mensaje de error completo (copiar/pegar)
2. Logs de Logcat (si hay)
3. Qué paso de validación falló
4. Screenshot del error (si es visual)

**Prompt sugerido:**
```
"Seguí las instrucciones de validación de DateProvider pero tengo este error:
[PEGAR ERROR AQUÍ]

Ya hice:
- Clean Project
- Rebuild Project
- Desinstalar app
- Invalidate Caches

¿Qué más puedo revisar?"
```

---

## RESUMEN EJECUTIVO

**✅ Implementación completa:**
- DateProvider Pattern implementado
- 3 archivos creados
- 11 archivos modificados
- Sin errores de linter

**⏳ Tiempo de validación:** 5-10 minutos

**🎯 Objetivo:** Verificar que la fecha se muestre correcta

**📋 Checklist:** 8 puntos de validación

---

**¡Éxito con la validación!** 🚀

Si todo funciona correctamente, ya tenés una base sólida para:
- Tests deterministas
- Arquitectura limpia
- KMP-ready

**FIN DE LAS INSTRUCCIONES**
