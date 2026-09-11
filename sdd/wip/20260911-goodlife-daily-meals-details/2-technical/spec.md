# Especificación técnica — Daily y Meal Plan details

## Arquitectura

Se conserva el flujo `ScreenOwner → ViewModel → UseCase → Repository → Retrofit/Room`. Los composables son puros y los Owners son los únicos que obtienen ViewModels con Koin y coordinan la navegación de regreso.

No se agrega API, DTO, repositorio ni migración. Los ViewModels de detalle reutilizan:

- `GetDailyItemsUseCase(LocalDate)` para el daily log.
- `GetMealPlansUseCase(LocalDate)` para los planes diarios.

## Migración de rutas

Reemplazar las rutas y extensiones engañosas en un mismo cambio atómico:

```kotlin
@Serializable data class DailyItemDetail(val dailyItemId: Long, val dateIso: String) : TabRoute
@Serializable data class MealPlanDetail(val mealPlanId: Long, val dateIso: String) : TabRoute
```

Los ViewModels de tab emiten la ruta usando `currentDate.toString()`. Daily recibe `ComposeNavigationController` por DI, igual que Meals. `TabNavGraph` decodifica las rutas tipadas y pasa los parámetros al Owner correspondiente.

No se conservan aliases ambiguos `DailyDetail(taskId)` o `MealDetail(mealId)`: no son API pública y eliminarlos evita llamadas futuras con la identidad incorrecta.

## Estados y frescura

Cada detalle expone una sealed UI state estable: `Loading`, `Content`, `NotFound` y `Error(localizedMessage)`. Debe validar `dateIso` antes de invocar un use case. Una fecha inválida produce error localizado y no consulta red.

La carga mantiene un `Job` cancelable y un contador monotónico de request. Al reintentar, cancela la carga anterior y sólo publica el resultado de la última solicitud. Esto evita que una respuesta tardía reemplace la pantalla actual.

Las respuestas `NotFound`, colecciones vacías o IDs ausentes producen `NotFound`, no un contenido parcial. Los errores de servidor/red se traducen a copy de `AppLanguage`; no se propaga el mensaje remoto.

## Mapeo

Daily detail mapea del domain `DailyItem`: `type`, `title`, `description`, `scheduledTime`, `status`. Meal plan detail mapea de `DailyMealPlanSummary`: `name`, `mealName`, `mealType`, `scheduledTime`, `imageUrl`, calorías y macros. Los formatos numéricos y labels se resuelven en ViewModel mediante nuevos modelos de texto de `AppLanguage`.

Como parte de esta vertical, los textos hardcodeados actualmente expuestos en `MealsScreen` se migran al mismo contrato de idioma. No se amplía el modelo nutricional para fingir ingredientes o recetas.

## DI, validación y rollout

Los dos ViewModels se registran con parámetros Koin para ID y `dateIso`. Los tests usan repositorios fake existentes y dispatchers de prueba. Se agregan pruebas unitarias de:

- acciones de los tabs que emiten ruta tipada con la fecha seleccionada;
- selección correcta por ID y fecha;
- `NotFound`, error localizado, fecha inválida y reintento obsoleto;
- mapeo de campos visibles y formatos.

El gate obligatorio es tests unitarios, cobertura lógica elegible >= 80%, lint sin errores y verificación de gobierno. No hay rollout de backend ni secreto nuevo. Tras merge, el smoke autenticado pendiente valida navegación real contra datos recuperados sin registrar información personal.

## Riesgos y decisiones futuras

El resumen de Meal Plan no equivale a una receta. Si producto necesita ingredientes, instrucciones, edición, consulta cross-date por ID o deep links, se abrirá una feature con endpoint canónico por ID y autorización backend. No se adelanta esa expansión en R-04.
