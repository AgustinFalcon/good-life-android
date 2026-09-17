# GoodLife Android — recursos no usados

- Feature id: `20260915-goodlife-unused-resources`
- Estado: `in_review` (PRs #42, #43 y #44 mergeadas; esta rama reconcilia el cierre).
- Issue: #38 (abierta hasta que esta PR tenga review y CI verde; label `type: chore`; sin asignación de tablero requerida).
- Rama: `chore/android-close-unused-resources`.
- Base verificada: `origin/master` `283d2cbb98b976968f606f305b2f1880357910ec` (PR #44 mergeada).
- PRD: no requerido; remediación técnica acotada, sin comportamiento nuevo.
- Límite de cierre: consolida la evidencia de las tres remediaciones mergeadas; no elimina recursos adicionales, no modifica Kotlin, Compose, manifest, launcher, compatibilidad, Gradle, packaging ni UI.

## Remediaciones mergeadas

- Owner/accountable: Android maintainers; issue #38; clasificación `remove`.
- Manifiesto exacto: `.github/scripts/manifests/auth-strings-38.json` (13 símbolos × 2 variantes = 26 fingerprints).
- Sólo se retiran declaraciones legacy de `strings.xml`; la UI vigente obtiene ese copy de `UiTexts`/textos de dominio y no hay referencias estáticas ni lookups dinámicos para estos símbolos en `app/src`.
- Evidencia: `4-implementation/remediation/auth-strings-38.json`; delta 28 → 15 símbolos `UnusedResources` y 34 → 21 warnings determinísticos por variante.
- La PR #44 retira los 9 strings Daily/tabs/registro autorizados; `4-implementation/remediation/remaining-strings-38.json` registra 15 → 6 símbolos y 18 fingerprints retirados.

## Decisiones de alcance

- El snapshot inicial contenía 35 símbolos por variante y 70 fingerprints; cada remediation PR conserva `before`, `after` y `remaining` y declara un delta exacto.
- `core_compat.xml` queda `retain_compat` en #38. #38 puede inspeccionar AAR/merged resources read-only; sólo #39 puede investigar y cambiar dependency/AGP/packaging.
- Toda la familia launcher queda `retain_launcher`: sus raíces del manifest y qualifiers son parte del contrato de instalación. No se elimina ni se anuncia como validada para upgrade sin un smoke futuro específico.
- No quedan filas `needs_owner`: los seis símbolos restantes tienen accountable, rationale y trigger. Ninguna se elimina sin autorización, evidencia y PR enfocada.

## Ejecución de remediación de colores

- Owner/accountable: Android maintainers; issue #38; clasificación `remove`.
- Manifiesto exacto: `.github/scripts/manifests/legacy-colors-38.json` (7 símbolos × 2 variantes = 14 fingerprints).
- Reducer: `.github/scripts/remove_lint_baseline_entries.py`; falla cerrado ante fingerprint ausente, duplicado o mismatch y preserva secciones protegidas.
- Evidencia completa: `4-implementation/remediation/legacy-colors-38.json`; resultado 35 → 28 `UnusedResources` y 41 → 34 warnings determinísticos por variante tras lint.

## Cierre

- #38 queda satisfecha por clasificación completa y tres remediaciones acotadas: colores legacy (#42), strings auth/login (#43) y strings Daily/tabs/registro (#44).
- Los seis UnusedResources restantes se retienen explícitamente: cuatro son compatibilidad AndroidX y dos pertenecen a familias launcher protegidas por manifest/qualifiers.
- #39 conserva exclusivamente cualquier cambio de toolchain/dependencias/packaging. El smoke autenticado #5 es independiente y no se usa para declarar la remediación de recursos como una release.
