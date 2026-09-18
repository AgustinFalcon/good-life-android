# Especificación técnica — contrato nullable de rutina activa

`GET /api/v1/routines/active` es semánticamente opcional: una respuesta 2xx sin `data` significa que no existe rutina activa para el usuario. Ese endpoint declara y propaga `RoutineResponse?` exclusivamente por su datasource y repositorio. El use case convierte `Success(null)` en `GetActiveRoutineResult.NotFound`; el ViewModel ya traduce `NotFound` a `NoRoutine`.

`executeApiCall` y los demás contratos permanecen sin cambios. Los errores HTTP, servidor y transporte conservan su semántica actual. Las pruebas cubren payload nulo, contenido poblado, ausencia y red. Rollback: revertir el commit del fix; no hay migraciones ni datos que restaurar.
