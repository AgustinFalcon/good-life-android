# Evidencia de migración upload-artifact — #53

## Fuente oficial

- Tag oficial: `actions/upload-artifact@v6.0.0`.
- Commit inmutable: `b7c566a772e6b6bfb58ed0dc250532a479d7789f`.
- Release oficial: declara runtime Node 24 y runner mínimo `2.327.1`.
- Entorno de los dos workflows: GitHub-hosted `ubuntu-latest`; no se administran runners self-hosted.

## Verificación estática

- Referencias al SHA nuevo en `.github/workflows`: 2.
- Referencias al SHA anterior: 0.
- Cada diff de workflow contiene una sola sustitución `uses`; se preservan `if: always()`, nombres, paths, `if-no-files-found`, permisos, jobs y triggers.
- `git diff --check`: PASS.

## CI remoto

Pendiente de ejecución del PR. Android CI es el gate operativo que confirmará el runtime de la Action; no se dispara el monitor programado, porque comparte runner/contrato y no cambia su schedule.