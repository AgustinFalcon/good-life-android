# Spec pre-build and final review — Ubuntu 26 runner

## Revisión previa

Revisor independiente Sol: APPROVE después de verificar tres sustituciones exactas de `runs-on`, invariancia de permisos/triggers/pins/artefactos, SDD en progreso y rollback explícito. Se corrigieron afirmaciones prematuras en runbook/changelog, el formato del changelog, el rollback y una edición SDD fuera de alcance antes de abrir PR.

## Revisión final esperada

Confirmar que las ejecuciones remotas registradas coinciden con la evidencia, que no se declara release o smoke autenticado y que la promoción conserva el diff de workflows limitado a los tres runners.
## Revisión final

Revisor independiente Sol: APPROVE. Verificó que el run Android CI `35309485642` y el monitor `35310137341` corresponden al SHA evaluado, concluyeron `success` sobre Ubuntu 26.04.1 y no modifican producto, Gradle, dependencias, permisos, triggers, firma, distribución ni el smoke autenticado. La evidencia de cierre y rollback satisfacen el contrato de #56.