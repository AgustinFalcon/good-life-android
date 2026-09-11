# Especificación funcional — Daily y Meal Plan details

## Objetivo

Eliminar las acciones expuestas sin comportamiento de los tabs Daily y Meals mediante detalles internos, read-only y reconstruibles después de reiniciar la app.

## Decisión propuesta

Los detalles se construyen con las lecturas existentes por fecha. Cada ruta transporta un identificador semántico y la fecha ISO del tab de origen; el Owner vuelve a leer esa fecha y selecciona el elemento por ID. No transporta objetos de UI ni usa la fecha actual como sustituto.

| Superficie | Ruta propuesta | Origen del ID | Lectura |
|---|---|---|---|
| Daily | `DailyItemDetail(dailyItemId, dateIso)` | `DailyItem.id` | daily log de `dateIso` |
| Meals | `MealPlanDetail(mealPlanId, dateIso)` | `DailyMealPlanSummary.id` | meal plans de `dateIso` |

`dateIso` usa `YYYY-MM-DD`. Es un primitive estable para Navigation; no hay serialización de objetos ni dependencia de memoria de la pantalla anterior.

## Comportamiento

### Daily item

Al tocar una tarjeta Daily, se abre su detalle para la fecha que el usuario está visualizando. El detalle muestra tipo, título, descripción si existe, hora programada si existe y estado. Es una vista de contexto del item diario; no promete editar la entidad subyacente ni cambiar su estado desde esta pantalla.

### Meal plan

Al tocar una tarjeta Meals, se abre el resumen del plan de esa fecha: nombre del plan, nombre de la comida si existe, tipo, hora si existe, imagen si existe y macros agregados. No promete ingredientes, instrucciones ni edición porque no están presentes en el contrato de resumen vigente.

### Estados observables

Ambos detalles deben renderizar:

1. `Loading` mientras se consulta la fecha de la ruta.
2. `Content` cuando la respuesta contiene el ID pedido.
3. `NotFound` si no existe log/plan para la fecha o si el ID no pertenece a la respuesta.
4. `Error` con mensaje localizado y reintento para red, servidor o una fecha de ruta inválida.

El botón volver regresa al tab de origen. Reintentar vuelve a usar exactamente el mismo `dateIso` e ID.

## Criterios de aceptación

- Ninguna tarjeta Daily o Meals visible termina en no-op o placeholder.
- Una fecha histórica conserva su contexto: abrir y reabrir el detalle consulta esa misma fecha, no `today()`.
- Daily selecciona por `DailyItem.id`; Meals por `DailyMealPlanSummary.id`.
- Los nombres de ruta y parámetros reflejan esas identidades; no quedan `taskId` ni `mealId` engañosos.
- La app puede recrear los detalles desde la ruta sin depender de un objeto pasado entre composables.
- Todo copy nuevo y el copy de Meals tocado por esta vertical usa `AppLanguage` en español, inglés y portugués.
- No se muestra texto crudo del backend en estados de error nuevos o corregidos.
- La cobertura incluye navegación, fecha de origen, content, not-found, error, ruta inválida y reintento sin respuesta obsoleta.

## Fuera de alcance

- Endpoints nuevos por ID.
- Ingredientes, instrucciones, historial, edición o borrado de meal plans.
- Edición de task/habit/workout desde Daily detail.
- Deep links públicos, notificaciones y sincronización offline de mutaciones.
