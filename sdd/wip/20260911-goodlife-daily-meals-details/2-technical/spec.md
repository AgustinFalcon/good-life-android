# Especificación técnica — Daily y Meal Plan details

## Arquitectura

Se conserva el flujo `ScreenOwner → ViewModel → UseCase → Repository → Retrofit/Room`. Los composables son puros y los Owners son los únicos que obtienen ViewModels con Koin y coordinan efectos de plataforma. La navegación de detalle es local al `TabNavGraph`: los ViewModels emiten efectos tipados de detalle y el Owner invoca un callback recibido desde el grafo interno.

No se agrega API, DTO, repositorio ni migración. Los ViewModels de detalle reutilizan:

- `GetDailyItemsUseCase(LocalDate)` para el daily log.
- `GetMealPlansUseCase(LocalDate)` para los planes diarios.

## Migración de rutas

Reemplazar las rutas y extensiones engañosas en un mismo cambio atómico:

```kotlin
@Serializable data class DailyItemDetail(val dailyItemId: Long, val dateIso: String) : TabRoute
@Serializable data class MealPlanDetail(val mealPlanId: Long, val dateIso: String) : TabRoute
```

Los ViewModels de tab capturan `currentDate.toString()` al crear un efecto tipado `NavigateToDailyItemDetail` o `NavigateToMealPlanDetail`; no reciben `NavController` ni emiten `TabRoute` al controlador global de `AppRoute`. `DailyScreenOwner` y `MealsScreenOwner` reciben callbacks locales desde `TabNavGraph`, colectan esos efectos y navegan con el `NavHostController` interno. `TabNavGraph` decodifica con `backStackEntry.toRoute`, pasa ID y fecha primitivos al Owner de detalle, y entrega `onNavigateUp = navController::navigateUp`. El botón volver conserva el stack y el tab de origen.

No se conservan aliases ambiguos `DailyDetail(taskId)` o `MealDetail(mealId)`: no son API pública y eliminarlos evita llamadas futuras con la identidad incorrecta.

## Estados y frescura

Cada detalle expone una sealed UI state estable: `Loading`, `Content`, `NotFound` y `Error(localizedMessage)`. Debe validar `dailyItemId`/`mealPlanId` estrictamente mayores que cero y parsear `dateIso` exactamente como `YYYY-MM-DD` antes de invocar un use case. Cualquier parámetro inválido produce error localizado y no consulta red. Sólo una colección vacía, un not-found de fecha o un ID ausente producen `NotFound`; expiración de sesión sigue el flujo autenticado existente y nunca se degrada a ausencia de contenido.

La carga mantiene un `Job` cancelable y un contador monotónico de request. Al reintentar, cancela la carga anterior y sólo publica el resultado de la última solicitud. Esto evita que una respuesta tardía reemplace la pantalla actual.

Las respuestas `NotFound`, colecciones vacías o IDs ausentes producen `NotFound`, no un contenido parcial. Los errores de servidor/red se traducen a copy de `AppLanguage`; no se propaga el mensaje remoto.

## Mapeo

Daily detail mapea del domain `DailyItem`: `type`, `title`, `description`, `scheduledTime`, `status`. Meal plan detail mapea de `DailyMealPlanSummary`: `name`, `mealName`, `mealType`, `scheduledTime`, `imageUrl`, calorías y macros. `AppLanguage` define y fuerza en ES/EN/PT todos los textos nuevos: título, volver, cargar, not-found, retry, ruta inválida, labels de tipo y estado Daily, labels de macros y error genérico localizado. Los formatos numéricos y labels se resuelven en ViewModel; ninguna pantalla nueva usa literales de producto ni enums crudos.

Como parte de esta vertical, los textos hardcodeados actualmente expuestos en `MealsScreen` se migran al mismo contrato de idioma. No se amplía el modelo nutricional para fingir ingredientes o recetas.

La imagen usa únicamente el cargador aprobado por la app. Si falta o falla, el detalle muestra el fallback visual local sin registrar ni presentar URL, error remoto o contenido de receta.

## DI, validación y rollout

Los dos ViewModels de detalle se registran con parámetros Koin para ID y `dateIso`. Los Owners reciben los callbacks locales de navegación; el controlador global queda reservado para `AppRoute`. Los tests usan repositorios fake existentes y dispatchers de prueba. Se agregan pruebas unitarias de:

- acciones de los tabs que emiten efecto tipado local con ID y fecha histórica capturada;
- decodificación del grafo interno, callback local y vuelta al mismo tab;
- selección correcta por ID y fecha;
- `NotFound`, error localizado, ID/fecha inválidos sin llamada, sesión separada y reintento obsoleto;
- mapeo de campos visibles y formatos.

El gate obligatorio es tests unitarios, cobertura lógica elegible >= 80%, lint sin errores y verificación de gobierno. No hay rollout de backend ni secreto nuevo. Tras merge, el smoke autenticado pendiente valida navegación real contra datos recuperados sin registrar información personal.

## Riesgos y decisiones futuras

El resumen de Meal Plan no equivale a una receta. Si producto necesita ingredientes, instrucciones, edición, consulta cross-date por ID o deep links, se abrirá una feature con endpoint canónico por ID y autorización backend. No se adelanta esa expansión en R-04.
