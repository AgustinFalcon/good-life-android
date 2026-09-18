# Evidencia — evaluación Ubuntu 26

## Fuente oficial

GitHub anunció el 2026-09-17 que `ubuntu-latest` migrará gradualmente de Ubuntu 24.04 a Ubuntu 26.04 entre 2026-10-19 y 2026-11-19, y recomienda probar `ubuntu-26.04` antes de la ventana. Fuente: https://github.blog/changelog/2026-09-17-ubuntu-26-generally-available-and-latest-migration/

## Contrato estático

- Commit evaluado: `4d2cf07302c5297c043dc35262296efe7714c4da`.
- Tres jobs usan `ubuntu-26.04`: `logic-quality`, `release-static` y `monitor`.
- Cero referencias a `ubuntu-latest` permanecen en `.github/workflows/`.
- Triggers, `contents: read`, SHAs de Actions, Java 17, comandos Gradle, rutas/nombres de artifact y condiciones de upload no cambiaron.
- `verify-governance.sh`, `git diff --check` y el gate local `:app:logicDebugUnitTestCoverageVerification` pasaron.

## Ejecuciones remotas

| Workflow | Run | Resultado | Evidencia |
|---|---:|---|---|
| Android CI PR #58 | `35309485642` | PASS | Cobertura/gobernanza: 2m30s. Lint, inventario, parser, upload y assemble release unsigned: PASS. |
| Android lint advisory monitor (dispatch sobre la rama) | `35310137341` | PASS | Lint y detector estricto de drift externo terminaron PASS; artifact de evidencia se subió según contrato. |

## Decisión y rollback

Se adopta `ubuntu-26.04` explícito: los tres jobs funcionaron con el mismo contrato existente antes de la migración de `ubuntu-latest`. No se cambian dependencias, producto, firma, distribución ni el smoke autenticado #5.

Si una regresión posterior se atribuye al runner, revertir el commit de #58 para restaurar las tres etiquetas `ubuntu-latest`, abrir PR de rollback y exigir Android CI más monitor dispatch verdes antes de considerarlo restaurado. Un pin temporal a Ubuntu 24.04 sólo puede decidirse en issue separado con evidencia del fallo.