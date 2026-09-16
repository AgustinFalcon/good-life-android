# GoodLife Android — recursos no usados

- Feature id: `20260915-goodlife-unused-resources`
- Estado: `in_review` (colores legacy ya mergeados; strings auth/login en revisión aislada).
- Issue: #38 (abierto; label `type: chore`; asignación al tablero pendiente de `read:project`).
- Rama: `chore/android-remove-auth-strings`.
- Base verificada: `origin/master` `25d068ab8758203f182158240c344adede7c0d05` (PR #42 mergeada).
- PRD: no requerido; remediación técnica acotada, sin comportamiento nuevo.
- Límite de esta PR: elimina solamente 13 declaraciones legacy auth/login de `strings.xml`; no modifica Kotlin, Compose, manifest, launcher, compatibilidad, Gradle, packaging ni UI.

## Ejecución de remediación auth/login

- Owner/accountable: Android maintainers; issue #38; clasificación `remove`.
- Manifiesto exacto: `.github/scripts/manifests/auth-strings-38.json` (13 símbolos × 2 variantes = 26 fingerprints).
- Sólo se retiran declaraciones legacy de `strings.xml`; la UI vigente obtiene ese copy de `UiTexts`/textos de dominio y no hay referencias estáticas ni lookups dinámicos para estos símbolos en `app/src`.
- Evidencia: `4-implementation/remediation/auth-strings-38.json`; delta esperado 28 → 15 símbolos `UnusedResources` y 34 → 21 warnings determinísticos por variante, sujeto a gates frescos.

## Decisiones de alcance

- El snapshot inicial contenía 35 símbolos por variante y 70 fingerprints; cada remediation PR conserva `before`, `after` y `remaining` y declara un delta exacto.
- `core_compat.xml` queda `retain_compat` en #38. #38 puede inspeccionar AAR/merged resources read-only; sólo #39 puede investigar y cambiar dependency/AGP/packaging.
- Toda la familia launcher queda `retain_launcher` hasta probar roots del manifest, qualifiers y smoke de instalación/upgrade/launcher.
- Las filas restantes quedan `needs_owner`; ninguna se elimina sin autorización, evidencia y PR enfocada.

## Ejecución de remediación de colores

- Owner/accountable: Android maintainers; issue #38; clasificación `remove`.
- Manifiesto exacto: `.github/scripts/manifests/legacy-colors-38.json` (7 símbolos × 2 variantes = 14 fingerprints).
- Reducer: `.github/scripts/remove_lint_baseline_entries.py`; falla cerrado ante fingerprint ausente, duplicado o mismatch y preserva secciones protegidas.
- Evidencia completa: `4-implementation/remediation/legacy-colors-38.json`; resultado 35 → 28 `UnusedResources` y 41 → 34 warnings determinísticos por variante tras lint.
