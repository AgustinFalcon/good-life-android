# Validación de UR-01

Fecha: 2026-09-15
Base: `origin/master` `f7733cc`

- Conteo del snapshot: 35 filas semánticas y 35 fingerprints `UnusedResources` en cada variante; no hay fila clasificada como `remove`.
- `python .github/tests/test_lint_baseline.py`: OK, 16 tests.\n- `python .github/tests/test_resource_inventory.py`: OK, 5 tests.\n- `python .github/scripts/resource_inventory.py --check`: OK, 35 symbols and matching semantic multisets.
- `python .github/scripts/verify_lint_baseline.py ...`: OK, `Deterministic lint baseline verification passed for debug and release.`
- `./gradlew :app:lintDebug :app:lintRelease --no-daemon`: OK, `BUILD SUCCESSFUL` (55 tareas).
- `git diff --check`: OK.
- `verify-governance.sh` contra `origin/master`: OK; al estar los artefactos sin commit todavía, el gate reportó estructura base válida. Se repetirá contra el commit del PR.

Esta validación no autoriza eliminación, no prueba ownership, no modifica dependencias y no resuelve la asignación de #38 al tablero.
