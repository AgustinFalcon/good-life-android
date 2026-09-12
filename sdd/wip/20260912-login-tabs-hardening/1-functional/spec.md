# Especificación funcional

## Objetivo
Evitar navegación/estado obsoleto y nunca mostrar contenido remoto crudo.

## Criterios
- Cambiar fecha Daily no queda bloqueado por una lectura anterior.
- Errores de servidor Daily y Meals usan copy localizado genérico.
- Durante login no se puede iniciar navegación ni autenticación competidora.
- CTAs sin flujo implementado no se muestran.