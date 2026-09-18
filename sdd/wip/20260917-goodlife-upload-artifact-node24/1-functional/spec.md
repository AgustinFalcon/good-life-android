# Especificación funcional — evidencia CI sin Node 20

## Objetivo

Los workflows Android deben seguir conservando evidencia lint aun si falla un gate, sin usar el runtime Node 20 deprecado.

## Criterios de aceptación

1. Los dos usos de `actions/upload-artifact` usan el mismo SHA oficial de una versión con Node 24.
2. Se conservan nombres de artifacts, rutas, `if: always()`, `if-no-files-found: error` y permisos.
3. Android CI sigue validando cobertura, lint e assembly release; el monitor conserva su comportamiento fail-on-drift.
4. El PR conserva exactamente dos pins nuevos y cero referencias al SHA anterior; Android CI pasa sus gates remotos.
5. Changelog, evidencia y revisión SDD quedan versionados.

## Fuera de alcance

Modificar reglas lint, permisos, secretos, uploads de producto, distribución, Gradle o código Android.