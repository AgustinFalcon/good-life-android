# GoodLife Android — gates de CI para ramas release

- Feature id: `20260917-goodlife-release-ci-branches`.
- Estado: `spec_review`.
- Issue: #49.
- Rama: `chore/android-ci-release-branches`.

## Contexto

Android CI solo atendía `master`. Este cambio amplía el mismo workflow a `release/**`; no duplica jobs, no modifica comandos ni relaja ningún gate.