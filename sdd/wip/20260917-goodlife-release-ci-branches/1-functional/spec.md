# Especificación funcional — CI GitFlow

## Objetivo

Un PR o push de una rama `release/X.Y.Z` debe recibir los mismos gates Android que `master`, para que la integración de release no dependa de checks ausentes.

## Criterios de aceptación

1. El workflow existente se dispara en `master` y `release/**` para `push` y `pull_request`.
2. Conserva exactamente los jobs de cobertura, lint/inventarios y ensamblado release no firmado.
3. Se demuestra una ejecución sobre una rama release antes de fusionar otros PRs a ella.

## Fuera de alcance

Nuevo workflow, modificación de gates, publicación, firma, distribución o integración del repositorio CI compartido.