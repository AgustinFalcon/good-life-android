# GoodLife Android — recursos no usados

- Feature id: `20260915-goodlife-unused-resources`
- Estado: `in_review` (remediación de colores implementada; pendiente review/merge)
- Issue: #38 (abierto; label `type: chore`; asignación al tablero bloqueada porque el token no tiene `read:project`; no se inventa estado).
- Rama: `chore/android-remove-legacy-colors`.
- Base verificada: `origin/master` `0e56c57`.
- PRD: no requerido; remediación técnica acotada, sin comportamiento nuevo.
- Límite: esta PR elimina únicamente siete colores legacy propios; no modifica dependencias/AGP, compatibilidad, launcher, manifest, packaging ni UI.

## Decisiones de alcance

- El snapshot inicial es 35 símbolos por variante y 70 fingerprints, pero la baseline posterior es evolutiva: cada remediation PR conserva `before`, `after` y `remaining` y declara un delta exacto.
- `core_compat.xml` queda `retain_compat` en #38. #38 puede inspeccionar AAR/merged resources read-only; sólo #39 puede investigar y cambiar dependency/AGP/packaging.
- Toda la familia launcher queda `retain_launcher` hasta probar roots del manifest, qualifiers y smoke de instalación/upgrade/launcher.
- Las otras filas quedan `needs_owner`; no se marca una tarea de borrado como terminada.


## Ejecución de remediación de colores

- Owner/accountable: Android maintainers; issue #38; clasificación `remove`.
- Manifiesto exacto: .github/scripts/manifests/legacy-colors-38.json (7 símbolos × 2 variantes = 14 fingerprints).
- Reducer: .github/scripts/remove_lint_baseline_entries.py; falla cerrado ante fingerprint ausente, duplicado o mismatch y preserva secciones protegidas.
- Evidencia completa: 4-implementation/remediation/legacy-colors-38.json; resultado 35 → 28 UnusedResources y 41 → 34 warnings determinísticos por variante tras lint actual.
