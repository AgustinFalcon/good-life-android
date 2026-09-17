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

- PR #55, GitHub Actions run `35286706734`: **PASS**.
- `Logic tests and 80% coverage`: PASS.
- `Lint baseline and unsigned release build`: PASS; generó reportes, verificó baseline, subió `lint-evidence` con el pin Node 24 y ensambló release no firmado.
- GitHub emitió una advertencia futura de migración `ubuntu-latest` a Ubuntu 26; está trazada separadamente en #56 y no se silenció ni se mezcló con #53.
- El monitor programado sigue sin dispararse: comparte runner/contrato y #53 no cambia su schedule.