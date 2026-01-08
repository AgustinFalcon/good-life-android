# Sistema de Navegación

## Introducción

GoodLife implementa un sistema de navegación **reactivo** y **type-safe** que desacopla los ViewModels del NavController de Compose.

### Beneficios

| Beneficio | Descripción |
|-----------|-------------|
| **Desacoplamiento** | ViewModels no conocen NavController |
| **Testabilidad** | Navegación mockeable en tests |
| **Type-safety** | Rutas con @Serializable, sin strings |
| **Lifecycle-aware** | No navega cuando Activity está en background |
| **KMP-ready** | Interface abstracta para multiplataforma |

## Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                         ViewModel                                │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  navigationController.navigateTo(AppRoute.Main)             ││
│  └─────────────────────────────────────────────────────────────┘│
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               │ emit(NavigationAction.NavigateTo)
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│              ComposeNavigationControllerImpl                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  MutableSharedFlow<NavigationAction>                        ││
│  └─────────────────────────────────────────────────────────────┘│
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               │ collect() con repeatOnLifecycle
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        SmartNavHost                              │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  navController.navigate(route, navOptions)                  ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

## Componentes

### NavigationAction

Define los tipos de acciones de navegación disponibles.

```kotlin
sealed class NavigationAction {
    // Navega a una nueva pantalla
    data class NavigateTo<T : Any>(
        val route: T,
        val navOptions: NavOptions
    ) : NavigationAction()
    
    // Vuelve atrás
    data object NavigateUp : NavigationAction()
    
    // Pop hasta una ruta específica
    data class PopBackTo<T : Any>(
        val route: T,
        val inclusive: Boolean
    ) : NavigationAction()
}
```

### ComposeNavigationController

Interface que inyectan los ViewModels.

```kotlin
interface ComposeNavigationController {
    val navigationAction: SharedFlow<NavigationAction>
    
    fun <T : Any> navigateTo(route: T, navOptions: NavOptions)
    fun navigateUp()
    fun <T : Any> popBackTo(route: T, inclusive: Boolean)
}
```

### SmartNavHost

NavHost wrapper que observa el SharedFlow.

```kotlin
@Composable
fun SmartNavHost(
    navController: NavHostController,
    navigationController: ComposeNavigationController,
    startDestination: Any,
    graphBuilder: NavGraphBuilder.() -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    
    LaunchedEffect(Unit) {
        // Solo procesa cuando Activity está STARTED o superior
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigationController.navigationAction.collect { action ->
                // Ejecuta la navegación
            }
        }
    }
    
    NavHost(...)
}
```

## Rutas Type-Safe

### AppRoute (Nivel Aplicación)

```kotlin
@Serializable
sealed interface AppRoute {
    @Serializable
    data object Splash : AppRoute
    
    @Serializable
    data object Login : AppRoute
    
    @Serializable
    data object Register : AppRoute
    
    @Serializable
    data object Main : AppRoute
}
```

### TabRoute (Dentro de Tabs)

```kotlin
@Serializable
sealed interface TabRoute {
    @Serializable
    data object Home : TabRoute
    
    @Serializable
    data object Workouts : TabRoute
    
    // Con parámetros
    @Serializable
    data class WorkoutDetail(val workoutId: Long) : TabRoute
}
```

## Uso

### En ViewModel

```kotlin
class LoginViewModel(
    private val navigationController: ComposeNavigationController
) : ViewModel() {

    fun onLoginClicked() {
        viewModelScope.launch {
            // ... lógica de login ...
            
            // Navegar a Main
            navigationController.navigateToMain()
        }
    }
    
    fun onRegisterClicked() {
        navigationController.navigateToRegister()
    }
    
    fun onBackClicked() {
        navigationController.navigateUp()
    }
}
```

### Extension Functions

Para casos comunes, usa las extension functions:

```kotlin
// Navegación de nivel app
navigationController.navigateToLogin()
navigationController.navigateToMain()
navigationController.navigateToRegister()

// Navegación de nivel tab
navigationController.navigateToWorkoutDetail(workoutId = 5)
navigationController.navigateToMealDetail(mealId = 10)
navigationController.navigateToProfile()
```

### Navegación Personalizada

Para casos específicos:

```kotlin
navigationController.navigateTo(
    route = TabRoute.WorkoutDetail(workoutId = 123),
    navOptions = navOptions {
        popUpTo<TabRoute.Workouts> { inclusive = false }
        launchSingleTop = true
    }
)
```

## Grafo de Navegación

### AppGraph

```kotlin
fun NavGraphBuilder.addAppGraph() {
    composable<AppRoute.Splash> { SplashScreenOwner() }
    composable<AppRoute.Login> { LoginScreenOwner() }
    composable<AppRoute.Register> { RegisterScreenOwner() }
    composable<AppRoute.Main> { MainScaffoldScreen() }
}
```

### Estructura de Navegación

```
AppRoute.Splash
    │
    ▼
AppRoute.Login ◄────────┐
    │                   │
    ├──► AppRoute.Register
    │
    ▼
AppRoute.Main (con tabs)
    │
    ├── TabRoute.Home
    │       └── TabRoute.TaskDetail
    │
    ├── TabRoute.Workouts
    │       └── TabRoute.WorkoutDetail
    │
    ├── TabRoute.Meals
    │       └── TabRoute.MealDetail
    │
    └── TabRoute.Settings
            └── TabRoute.Profile
```

## Testing

### Mock del NavigationController

```kotlin
class FakeNavigationController : ComposeNavigationController {
    private val _navigationAction = MutableSharedFlow<NavigationAction>()
    override val navigationAction = _navigationAction.asSharedFlow()
    
    val navigatedRoutes = mutableListOf<Any>()
    
    override fun <T : Any> navigateTo(route: T, navOptions: NavOptions) {
        navigatedRoutes.add(route)
    }
    
    override fun navigateUp() {
        navigatedRoutes.add("UP")
    }
    
    override fun <T : Any> popBackTo(route: T, inclusive: Boolean) {
        navigatedRoutes.add("POP:$route")
    }
}

// En test
@Test
fun `login success navigates to main`() = runTest {
    val fakeNav = FakeNavigationController()
    val viewModel = LoginViewModel(fakeNav)
    
    viewModel.onLoginSuccess()
    
    assertEquals(AppRoute.Main, fakeNav.navigatedRoutes.last())
}
```

## FAQ

### ¿Por qué SharedFlow y no StateFlow?

**StateFlow** retiene el último valor y lo re-emite al rotar la pantalla.  
**SharedFlow** no retiene valores, evitando navegación duplicada.

Navegación = evento one-shot → SharedFlow.

### ¿Por qué repeatOnLifecycle?

Previene que se procesen eventos de navegación cuando la Activity está en `STOPPED` o `DESTROYED`, evitando crashes por navegar a una pantalla cuando la UI no existe.

### ¿Puedo navegar desde fuera de un ViewModel?

Sí, cualquier clase que tenga inyectado `ComposeNavigationController` puede navegar. Pero es recomendable centralizar la lógica de navegación en ViewModels.

