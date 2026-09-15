# Daily y Meals: detalles internos

## Objetivo

Completar las dos CTAs de detalle actualmente visibles: un ítem de Daily y un plan de Meals deben abrir un detalle interno útil y poder volver al mismo tab. No se agregan deep links, endpoints, edición ni eliminación.

## Alcance funcional

1. Un click con identificador positivo abre el detalle interno para la fecha seleccionada. Un identificador no positivo en el listado no emite efecto ni navega.
2. El detalle Daily muestra título, tipo, estado, horario opcional y descripción opcional. El detalle Meals muestra nombre del plan, tipo, horario opcional, nombre de comida opcional, calorías, proteínas, carbohidratos, grasas y estado activo/inactivo.
3. Cada detalle lee sólo con las consultas autenticadas existentes por fecha; filtra por el identificador de la ruta y no infiere receta, ingredientes ni acciones no disponibles.
4. Cada detalle tiene `Loading`, `Content`, `NotFound`, `Error` e `InvalidRoute`. Para una ruta puntual no existe un estado `Empty` separado: ausencia de fecha o de ID es `NotFound`.
5. En todos los estados hay una acción Volver, con etiqueta y descripción accesible localizadas; vuelve al stack local del tab y conserva la fecha, filtro y scroll que gestione el listado.
6. Una ruta restaurada con ID no positivo o fecha ISO `YYYY-MM-DD` inválida muestra `InvalidRoute` y no invoca datos. Reintentar mantiene parámetros válidos originales.
7. La imagen de comida es opcional; `null` o fallo controlado muestra fallback local sin registrar ni mostrar URL/error remoto.
8. La sesión es responsabilidad del flujo autenticado global existente. #7 no intercepta, remapea ni prueba una expiración de sesión como `NotFound`; esa integración se evidencia en el smoke #5.

## Fuera de alcance

- Navegación pública, App Links o deep links.
- Crear, editar o eliminar items/planes desde detalle.
- Nuevos contratos HTTP, modelos de receta/ingredientes o persistencia.
- Cambiar la semántica global de expiración de sesión.

## Criterios de aceptación

- Ninguna CTA de detalle de Daily/Meals queda en placeholder o no-op.
- Las rutas son serializables y contienen sólo IDs positivos más `dateIso` (`YYYY-MM-DD`).
- Hay pruebas de guardia de origen, restauración inválida, un único efecto por entrada, retorno, encontrado/no encontrado, red/servidor/retry, contenido localizado y fallback determinista de imagen.
- ES/EN/PT proveen todo copy nuevo. Los strings hardcodeados de `MealsScreenOwner` tocados por este vertical se migran a ese contrato; copy no tocado queda fuera.
- Las verificaciones unitarias, cobertura, lint, documentación y revisión de PR pasan.
## Preservación al volver

El detalle es sólo lectura: al volver no dispara una recarga automática. Daily conserva `currentDate` y el filtro activo; Meals conserva `currentDate`. Ambos listados conservan su `LazyListState` salvable del entry de navegación. La prueba de navegación verifica fecha/filtro; la prueba Compose instrumentada verifica que volver mantiene la posición visible. No se promete conservar datos ante muerte de proceso fuera de los mecanismos normales de Navigation saved state.
