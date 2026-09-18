# GoodLife Android — gates de CI para ramas release

- Feature id: `20260917-goodlife-release-ci-branches`.
- Estado: `done`.
- Issue: #49.
- Rama: `chore/android-ci-release-branches`.

## Contexto

Android CI solo atendía `master`. Este cambio amplía el mismo workflow a `release/**`; no duplica jobs, no modifica comandos ni relaja ningún gate.

## Cierre

RCI-02 se completó mediante workflow_dispatch directo sobre `release/1.0.0`, con ambos jobs Android CI PASS. RCI-03 se completó con #48: GitHub disparó Android CI automáticamente hacia release y ambos jobs terminaron PASS. #49 puede cerrarse; el smoke #5 sigue separado y bloqueado.
