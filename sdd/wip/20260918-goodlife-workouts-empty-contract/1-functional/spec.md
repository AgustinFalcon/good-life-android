# Especificación funcional — Workouts sin rutina activa

## Objetivo

Cuando el usuario autenticado no tiene una rutina activa, Workouts debe mostrar su estado vacío existente con CTA para crear una rutina; nunca debe presentarlo como un fallo de conexión.

## Criterios de aceptación

1. La respuesta exitosa de rutina activa sin `data` muestra `WorkoutsUiState.NoRoutine`.
2. Un fallo real de red mantiene el copy localizado de conexión y Retry.
3. Una respuesta de rutina poblada mantiene la pantalla de contenido y navegación actual.
4. No se crean, activan ni modifican rutinas para comprobar el estado vacío.

## Fuera de alcance

Cambiar el backend, crear rutinas, modificar el contrato global de API o alterar otros endpoints opcionales.
