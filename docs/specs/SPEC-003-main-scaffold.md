# SPEC-003: Main Scaffold con Bottom Navigation

## 📋 Información General

| Campo | Valor |
|-------|-------|
| **ID** | SPEC-003 |
| **Tipo** | Feature |
| **Prioridad** | Alta |
| **Estado** | 🚧 En Progreso (60% completado) |
| **Fecha Creación** | 2026-01-22 |
| **Actualizado** | 2026-01-29 |
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

## ⏳ Pendiente de Implementación

### Fase 7: MainScaffold ❌ PENDIENTE
```
📁 presentation/screen/main/
├── model/
│   ├── MainScaffoldUiState.kt   ❌ TODO
│   └── MainScaffoldUiAction.kt  ❌ TODO
├── MainScaffoldViewModel.kt     ❌ TODO
└── MainScaffoldScreen.kt        ❌ TODO (Owner + Content)
```

### Fase 8: Tab Screens ❌ PENDIENTE
```
📁 presentation/screen/tabs/
├── diary/
│   ├── DiaryTabScreen.kt        ❌ TODO (con DateHeaderComponent)
│   ├── DiaryTabViewModel.kt     ❌ TODO (maneja fecha)
│   └── model/DiaryUiState.kt    ❌ TODO
│
├── workouts/
│   ├── WorkoutsTabScreen.kt     ❌ TODO (con TabRowHeaderComponent)
│   ├── WorkoutsTabViewModel.kt  ❌ TODO
│   └── model/WorkoutsUiState.kt ❌ TODO
│
├── meals/
│   ├── MealsTabScreen.kt        ❌ TODO (con DateHeaderComponent)
│   ├── MealsTabViewModel.kt     ❌ TODO
│   └── model/MealsUiState.kt    ❌ TODO
│
└── more/
    ├── MoreTabScreen.kt         ❌ TODO
    ├── MoreTabViewModel.kt      ❌ TODO
    └── model/MoreUiState.kt     ❌ TODO
```

### Fase 9: Navegación ❌ PENDIENTE
```
📁 presentation/navigation/route/
├── TabNavGraph.kt               ❌ TODO
├── TabRoute.kt                  ⚠️ ACTUALIZAR (agregar grafos)
└── AppGraph.kt                  ⚠️ ACTUALIZAR (agregar MainScaffold)
```

### Fase 10: DI ❌ PENDIENTE
```
📁 di/
└── AppModule.kt                 ⚠️ ACTUALIZAR
    - viewModel { MainScaffoldViewModel(...) }
    - viewModel { DiaryTabViewModel(...) }
    - viewModel { WorkoutsTabViewModel(...) }
    - viewModel { MealsTabViewModel(...) }
    - viewModel { MoreTabViewModel(...) }
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
│  │   │  TAB DIARIO (DiaryTabScreen)                             │ │ │
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

## 🎬 Estados UI (Pendientes)

### MainScaffoldUiState
```kotlin
@Stable
data class MainScaffoldUiState(
    val bottomNavItems: List<BottomNavItemModel> = emptyList(),
    val selectedTab: BottomMenuOption = BottomMenuOption.HOME,
    val isModalOpen: Boolean = false,
    val currentDateFormatted: String = "",
    val quickActions: List<QuickActionItem> = emptyList(),
    val mealOptions: List<MealOptionItem> = emptyList()
)
```

### MainScaffoldUiAction
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

### Date Handling
6. **KMP-Ready**: Usa `kotlinx-datetime` en lugar de `java.time`
7. **Testable**: `Clock` inyectado, no `Clock.System` directo
8. **Multi-idioma**: `AppLanguage` sealed interface con Spanish, English, Portuguese

---

**Creado por:** Android Team  
**Última actualización:** 2026-01-29
