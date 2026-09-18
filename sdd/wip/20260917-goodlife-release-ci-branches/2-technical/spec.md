# Especificación técnica — alcance de triggers

## Cambio mínimo

En `.github/workflows/android-ci.yml`, ambos triggers de ramas pasan de solo `master` a `[master, "release/**"]`.

Los jobs, versiones Java, comandos Gradle, verificación de inventarios y permisos quedan sin cambios. El workflow sigue ejecutando el SHA del evento, por lo que un PR a release valida el commit propuesto y no la rama base.

## Validación y rollback

- Validar sintaxis YAML y conservar la ejecución manual `workflow_dispatch`.
- Disparar manualmente Android CI sobre la rama del PR y exigir sus dos jobs verdes.
- Tras merge, actualizar/rebasear PRs abiertos hacia release para que reciban los checks.
- Rollback: revertir el único cambio de patrón de ramas si el provider no admite el trigger; no hay cambios de producto.