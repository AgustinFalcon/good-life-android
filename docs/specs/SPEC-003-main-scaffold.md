# SPEC-003: Main Scaffold con Bottom Navigation

## 📋 Información General

| Campo | Valor |
|-------|-------|
| **ID** | SPEC-003 |
| **Tipo** | Feature |
| **Prioridad** | Alta |
| **Estado** | 🚧 En Progreso (85% completado) |
| **Fecha Creación** | 2026-01-22 |
| **Actualizado** | 2026-02-03 |
| **Dependencia** | Login/Register completados ✅ |

---

## 🎯 Objetivo

Crear el scaffold principal de la aplicación con:
- Bottom Navigation con 4 tabs + FAB central flotante
- **Header dinámico por tab** (cada tab puede tener su propio header)
- Sistema de navegación anidada (grafos por tab)
- Modal de acciones rápidas (fullscreen con grid de opciones)

---

## ✅ Progreso de Implementación

### Fase 1: Modelos del Dominio ✅ COMPLETADO
```
📁 domain/model/
├── nutrition/
│   └── MealType.kt              ✅ Enum: BREAKFAST, LUNCH, DINNER, SNACK, PRE_WORKOUT, POST_WORKOUT
├── daily/
│   ├── DailyItemType.kt         ✅ Enum: TASK, HABIT, WORKOUT, MEAL
│   └── ItemStatus.kt            ✅ Enum: PENDING, IN_PROGRESS, COMPLETED, SKIPPED
```

### Fase 2: Sistema de Fechas (Core) ✅ COMPLETADO
```
📁 core/datetime/
├── LocalDateExtensions.kt       ✅ isToday(), isYesterday(), isTomorrow(), toFriendlyString()
└── language/
    ├── AppLanguage.kt           ✅ sealed interface: Spanish, English, Portuguese
    └── DateFormats.kt           ✅ DateFormats + RelativeDateTexts
```
- Usa `kotlinx-datetime` (KMP-ready)
- `Clock` inyectado para testabilidad
- Multi-idioma con formatos localizados

### Fase 3: Componentes de Header ✅ COMPLETADO
```
📁 presentation/components/header/
├── DateHeaderComponent.kt       ✅ Header con navegación de fechas
├── TabRowHeaderComponent.kt     ✅ Header con tabs para Workouts
└── CalendarDayIcon.kt           ✅ Icono de calendario personalizado
```

### Fase 4: Modal de Acciones ✅ COMPLETADO
```
📁 presentation/components/modal/
├── AddActionModalComponent.kt   ✅ Modal fullscreen con animaciones
└── model/
    ├── QuickActionType.kt       ✅ Enum: ROUTINE, NUTRITION, WEIGHT, etc.
    ├── QuickActionItem.kt       ✅ data class para grid items
    └── MealOptionItem.kt        ✅ data class para meal options
```

### Fase 5: Bottom Navigation ✅ COMPLETADO
```
📁 presentation/components/bottom/
├── BottomNavigationComponent.kt ✅ Bottom nav con FAB central
└── model/
    ├── BottomMenuOption.kt      ✅ Enum: HOME, EXERCISES, FOOD, SETTINGS
    └── BottomNavItemModel.kt    ✅ data class para nav items
```

### Fase 6: Design System ✅ COMPLETADO
```
📁 presentation/theme/
├── Shape.kt                     ✅ GoodLifeShapes (MaterialTheme.shapes)
├── Color.kt                     ✅ Paleta completa light/dark
├── Type.kt                      ✅ GoodLifeTypography (MaterialTheme.typography)
└── Theme.kt                     ✅ GoodLifeTheme (integra todo)
```
- Patrón simple: Todo sale de `MaterialTheme.xxx`
- Dimensiones: valores directos `.dp`

---

## ✅ Completado (post SPEC-004 + SPEC-007)

### Fase 7: MainScaffold ✅ COMPLETADO
```
📁 presentation/screen/main/
├── model/
│   ├── MainScaffoldUiState.kt   ✅ @Stable + fabContentDescription: String
│   └── MainScaffoldUiAction.kt  ✅ @Stable sealed interface
├── MainScaffoldViewModel.kt     ✅ Usa DateProvider + AppLanguage
├── MainScaffoldScreen.kt        ✅ UI pura, textos via UiState/Params
└── MainScaffoldScreenOwner.kt   ✅ Único koinViewModel()
```

### Fase 8a: Daily Tab ✅ COMPLETADO
```
📁 presentation/screen/tabs/daily/
├── DailyScreen.kt               ✅ dailyTexts + dateHeaderParams (sin koinInject)
├── DailyScreenOwner.kt          ✅ Construye DateHeaderParams desde ViewModel
├── DailyTabViewModel.kt         ✅ DateProvider + AppLanguage + expone dailyTexts
└── model/
    ├── DailyUiState.kt          ✅ @Stable + strings formateados
    └── DailyUiAction.kt         ✅ @Stable sealed interface
```

### Localización ✅ COMPLETADO (SPEC-007)
```
Todos los textos de BottomNav, Headers, Components, Screens y Modal
localizados via AppLanguage → ViewModel → Owner → Screen → Params
```

### Componentes actualizados ✅
```
📁 presentation/components/
├── header/
│   ├── DateHeaderComponent.kt   ✅ DateHeaderParams (accessibility dentro)
│   └── TabRowHeaderComponent.kt ✅ WorkoutTabHeaderParams (filter dentro)
├── bottom/
│   └── BottomNavigationComponent ✅ BottomNavigationParams (fabDesc dentro)
├── common/
│   ├── TextFieldComponent.kt    ✅ TextFieldParams (toggle texts dentro)
│   └── TitleComponent.kt        ✅ text: String (sin stringResource)
```

---

## ⏳ Pendiente de Implementación

### Fase 8b: Workouts Tab ❌ PENDIENTE
```
📁 presentation/screen/tabs/workouts/
├── WorkoutsTabScreen.kt         ❌ TODO (con WorkoutTabHeaderParams)
├── WorkoutsTabViewModel.kt      ❌ TODO
└── model/WorkoutsUiState.kt     ❌ TODO
```

### Fase 8c: Meals Tab ❌ PENDIENTE
```
📁 presentation/screen/tabs/meals/
├── MealsTabScreen.kt            ❌ TODO (con DateHeaderParams)
├── MealsTabViewModel.kt         ❌ TODO
└── model/MealsUiState.kt        ❌ TODO
```

### Fase 8d: More Tab ❌ PENDIENTE
```
📁 presentation/screen/tabs/more/
├── MoreTabScreen.kt             ❌ TODO
├── MoreTabViewModel.kt          ❌ TODO
└── model/MoreUiState.kt         ❌ TODO
```

### Fase 9: Backend Integration ❌ PENDIENTE
```
Endpoints reales para Daily Items, Habits, Workouts, Meals
```

---

## 🏗️ Arquitectura de Navegación

### Concepto Clave: Header por Tab

```
┌─────────────────────────────────────────────────────────────────────┐
│  IMPORTANTE: El Header NO es parte del MainScaffold                 │
│  Cada Tab tiene su PROPIO header/toolbar                            │
│                                                                      │
│  - Tab Diario:     DateHeader (navegación de fechas)                │
│  - Tab Ejercicios: TabRowHeader (Todos | Míos | Estadísticas)       │
│  - Tab Comidas:    DateHeader (similar a Diario)                    │
│  - Tab Más:        SimpleHeader (solo título)                       │
└─────────────────────────────────────────────────────────────────────┘
```

### Dos Niveles de Navegación

```kotlin
// NIVEL 1: App Navigation (Login → Main)
// Usa: ComposeNavigationController + SharedFlow
// Controlado por: ViewModels de Login, Register, Splash

// NIVEL 2: Tab Navigation (Diario ↔ Ejercicios ↔ Comida ↔ Más)
// Usa: NavHostController interno (NO SharedFlow)
// Controlado por: MainScaffoldViewModel
```

---

## 📐 Diseño Visual

### MainScaffold Structure
```
┌─────────────────────────────────────────────────────────────────────┐
│                     MainScaffoldScreen                               │
│                                                                      │
│  ┌────────────────────────────────────────────────────────────────┐ │
│  │                    TabNavHost (contenido)                       │ │
│  │                                                                 │ │
│  │   ┌──────────────────────────────────────────────────────────┐ │ │
│  │   │  TAB DIARIO (DailyTabScreen)                             │ │ │
│  │   │  ┌────────────────────────────────────────────────────┐  │ │ │
│  │   │  │ [📅] [<] 🗓️ Jueves 23 Oct [>] [🔔]  ← DateHeader  │  │ │ │
│  │   │  ├────────────────────────────────────────────────────┤  │ │ │
│  │   │  │           Contenido del diario                     │  │ │ │
│  │   │  └────────────────────────────────────────────────────┘  │ │ │
│  │   └──────────────────────────────────────────────────────────┘ │ │
│  │                                                                 │ │
│  └────────────────────────────────────────────────────────────────┘ │
│                                                                      │
│  ┌────────────────────────────────────────────────────────────────┐ │
│  │  [🏠]      [🏃]       [  +  ]       [🍽️]      [•••]           │ │
│  │  Diario  Ejercicios    FAB       Comida      Más               │ │
│  └────────────────────────────────────────────────────────────────┘ │
│                                                                      │
│  ┌────────────────────────────────────────────────────────────────┐ │
│  │              AddActionModal (cuando FAB está abierto)          │ │
│  └────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 🎬 Estados UI (Implementados)

### MainScaffoldUiState ✅
```kotlin
@Stable
data class MainScaffoldUiState(
    val bottomNavItems: List<BottomNavItemModel> = emptyList(),
    val selectedTab: BottomMenuOption = BottomMenuOption.HOME,
    val isModalOpen: Boolean = false,
    val currentDateFormatted: String = "",
    val quickActions: List<QuickActionItem> = emptyList(),
    val mealOptions: List<MealOptionItem> = emptyList(),
    val fabContentDescription: String = ""  // ← SPEC-007: texto localizado
)
```

### MainScaffoldUiAction ✅
```kotlin
@Stable
sealed interface MainScaffoldUiAction {
    data class OnTabSelected(val tab: BottomMenuOption) : MainScaffoldUiAction
    data object OnFabClick : MainScaffoldUiAction
    data object OnModalDismiss : MainScaffoldUiAction
    data class OnQuickActionClick(val action: QuickActionType) : MainScaffoldUiAction
    data class OnMealOptionClick(val mealType: MealType?) : MainScaffoldUiAction
}
```

---

## 📝 Notas Importantes

### Arquitectura
1. **Dos NavControllers**: App-level usa SharedFlow, Tab-level usa NavController directo
2. **Header por Tab**: Cada tab define su propio header internamente
3. **Patrón Owner**: Todos los Screens separan inyección de UI pura
4. **@Stable**: Todos los UI States y Actions llevan @Stable

### Theme (Actualizado)
5. **Patrón Simple (InstaDev)**:
   - Shapes: `MaterialTheme.shapes.medium`, `MaterialTheme.shapes.extraLarge`
   - Colors: `MaterialTheme.colorScheme.primary`, `MaterialTheme.colorScheme.onSurface`
   - Typography: `MaterialTheme.typography.bodyLarge`, `MaterialTheme.typography.titleMedium`
   - Dimensiones: valores directos `16.dp`, `24.dp`

### Date Handling (SPEC-004)
6. **KMP-Ready**: Usa `kotlinx-datetime` en lugar de `java.time`
7. **Testable**: `DateProvider` inyectado, no `Clock.System` directo
8. **Formateo en ViewModel**: UI recibe strings ya formateados

### Localización (SPEC-007)
9. **AppLanguage singleton**: Detecta locale automáticamente
10. **Textos via ViewModel**: `koinInject()` prohibido en Screens/Components
11. **Params encapsulan textos**: `DateHeaderParams`, `TextFieldParams`, etc.
12. **3 idiomas**: Spanish, English, Portuguese (107+ strings cada uno)

---

**Creado por:** Android Team  
**Última actualización:** 2026-02-03
