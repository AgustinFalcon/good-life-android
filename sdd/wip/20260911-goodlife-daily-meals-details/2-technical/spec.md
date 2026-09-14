# Diseño técnico

## Arquitectura y rutas

Se reemplazan las rutas incompletas por:

```kotlin
data class DailyDetail(val itemId: Long, val dateIso: String) : TabRoute
data class MealDetail(val planId: Long, val dateIso: String) : TabRoute
```

Los tabs no reciben un `NavController` interno. Cada ViewModel emite un efecto local mediante `Channel.RENDEZVOUS`; el Owner lo colecta y llama al callback puro del graph. Cada origen valida el ID positivo, guarda un booleano de navegación en curso, ignora el doble tap y lo reinicia al volver al listado o si `trySend` falla. `TabNavGraph` navega con `NavHostController`, usa `backStackEntry.toRoute`, y pasa primitives más `onNavigateUp = navController::navigateUp` al Owner. El Owner suministra la acción de volver a **todos** los estados y la Screen permanece pura.

## Inyección y carga

`DailyModule` y `NutritionModule` registran ViewModels parametrizados de detalle con `(id: Long, dateIso: String)`. Sus Owners usan `koinViewModel(parameters = { parametersOf(id, dateIso) })`.

El detalle valida primero `id > 0` y parseo ISO exacto. Si falla, publica `InvalidRoute` sin llamada. Para ruta válida usa sólo el caso existente por fecha:

- Daily: `GetDailyItemsUseCase(date)` y busca `DailyItem.id`.
- Meals: `GetMealPlansUseCase(date)` y busca `DailyMealPlanSummary.id`.

`Success` sin ID, `NotFound` de fecha o lista vacía se convierten en `NotFound`. `ServerError` y `NetworkError` se mapean a `language.errorTexts.dataLoadError` y `connectionError`; nunca filtran `message`. Reintentar conserva parámetros. No se añade un resultado de sesión: `GoodLifeAuthenticator`/`SessionEventBus` conservan la recuperación global, que #7 no intercepta.

## Estado, copy y accesibilidad

Los UiState/UiModel son inmutables y resuelven display en ViewModel. `AppLanguage` declara título, volver, carga, no encontrado, ruta inválida, reintento, tipo/estado Daily, tipo Meals, activo/inactivo, macros, unidades y formato numérico. Spanish, English y Portuguese implementan exhaustivamente. Los strings hardcodeados de `MealsScreenOwner` que este cambio toca migran al contrato.

La imagen se representa mediante un componente con estado observable/injectable (URL, carga, error/fallback) o un `ImageLoader` falso en UI test; un `AsyncImage` no testeable por sí solo no satisface el criterio. La imagen decorativa no recibe descripción redundante; el fallback sí mantiene semántica útil. Cards accionables, Volver y Retry tienen semántica accesible comprobable.

## Verificación

Pruebas unitarias cubren navegación, doble tap, ID inválido, argumentos Koin/ruta, cada estado y mapping. UI/previews cubren contenido, no encontrado, error, ruta inválida, retry, back y fallback de imagen. Se ejecutan `testDebugUnitTest`, `logicDebugUnitTestCoverageVerification` y `lintDebug`. El smoke autenticado real es #5 y no se simula con credenciales.
## Preservación de listado

Los Owners inician carga al entrar por el flujo existente de `uiState`, pero no hacen `refresh()` en `LifecycleResumeEffect` al regresar de detalle. Daily mantiene `activeFilter` como estado del ViewModel al reconstruir una respuesta; `currentDate` ya es estado del ViewModel. Ambos Owners crean y pasan un `rememberLazyListState()` salvable a la Screen/lista, por lo que Navigation restaura su entrada al volver. Las pruebas de ViewModel cubren fecha/filtro; la instrumentada cubre posición visible tras navegar y volver.

## Gate instrumentado

Se agregan pruebas Compose instrumentadas para semántica de card, Volver, Retry y fallback, y para navegación/retorno. La entrega ejecuta `:app:connectedDebugAndroidTest` sobre un emulador API compatible: localmente antes de abrir PR y en el workflow de PR contra `master`. Si la infraestructura de emulador no está disponible, el gate falla o se marca bloqueado; nunca se sustituye por una afirmación manual. Unit, cobertura y lint siguen siendo gates complementarios.
