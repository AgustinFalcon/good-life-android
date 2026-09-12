# Especificación funcional

## Objetivo

Ninguna pantalla de Workouts debe renderizar detalles crudos recibidos desde el backend.

## Criterios de aceptación

- Un `ServerError` del tab Workouts muestra `language.errorTexts.dataLoadError` en ES, EN y PT.
- Un `ServerError` de Workout Detail muestra el mismo copy localizado genérico.
- `NetworkError` conserva el copy localizado de conexión existente.
- `NotFound`, Loading y Content no cambian de significado ni navegación.
- Los detalles técnicos continúan encapsulados en el resultado de dominio y no llegan a `WorkoutsUiState.Error` ni `WorkoutDetailUiState.Error`.
- Cada ViewModel tiene una prueba que inyecta un detalle distintivo de backend y verifica que la UI no lo expone.

## Fuera de alcance

- Modificar contratos HTTP/backend, logging, credenciales, datos, diseño visual o las pantallas ajenas a las cuatro indicadas en #19.