# Inventario de recursos — contrato UR-01

Estado: inventario vigente después de las remediaciones de colores legacy y strings de la issue #38. Las filas numeradas son sólo el conjunto restante y no sustituyen la evidencia inmutable de before/after/remaining.

## Fuente y contrato de derivación

El snapshot se deriva de `config/lint-baseline.json` schema v2. El snapshot autorizado contenía 35 símbolos `UnusedResources` por variante. La PR de colores retiró exactamente 7 familias propias (14 fingerprints), la PR auth/login retiró 13 (26 fingerprints) y la PR Daily/tabs/registro retiró 9 (18 fingerprints), dejando 4 símbolos por variante. La baseline es evolutiva: cada ejecución conserva evidencia before/after/remaining y un manifiesto exacto versionado en `remediation/` y `.github/scripts/manifests/`.

La herramienta acepta únicamente mensajes canonicalizados con el formato The resource R.type.name appears to be unused. Debe extraer type/name y fallar cerrado si cambia el formato, si el símbolo no puede parsearse o si hay una discrepancia de multiplicidad del multiset. La proyección semántica para comparar variantes es ruleId + type/name + normalizedMessage + occurrences; no incluye variant ni location. Los fingerprints debug/release se conservan por separado porque contienen la variante.

Cada fila representa una familia type/name, consolidando definiciones, source sets y qualifiers. D×1/R×1 significa una aparición en el multiset de cada variante. Las cuatro filas actuales `retain_*` no son removibles sin una PR enfocada con autorización, evidencia y review; la clasificación no autoriza un borrado genérico.

## Inventario restante

El inventario vigente se deriva de config/lint-baseline.json schema v2 después de retirar colores legacy (7 familias), strings de autenticación/login (13 familias) y strings Daily/tabs/registro (9 familias). Quedan 4 símbolos (8 fingerprints), todos recursos de compatibilidad AndroidX. Cada remediación conserva su ledger before/after/remaining y su manifiesto inmutable.

| # | Symbol | Definitions / qualifiers | Debug fingerprint(s) | Release fingerprint(s) | Semantic key / occurrences | Accountable follow-up | References / evidence searched | Linked issue / PR | Classification | Rationale | Re-evaluate when |
|---:|---|---|---|---|---|---|---|---|---|---|---|
| 1 | color/notification_template_icon_bg_compat | values/core_compat.xml; default | 0be0e1cc35388828913e424cd3a49c154c00d62d9b6f3c85cd052c95da26ead5 | bdde063b1e0b956c2eadbb19658ce33e3529f0f82a53c769a5014af622c61bab | UnusedResources; color/notification_template_icon_bg_compat; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |
| 2 | color/notification_template_icon_low_bg_compat | values/core_compat.xml; default | 326f70e581f7d955a23bf4f6ffdcfeef9c0e02589b7a83bb03d01923b81ef914 | d9ca418fc2e281ee883a96545bd2d81472c4f5baed20d0cf8250c871b128dc5a | UnusedResources; color/notification_template_icon_low_bg_compat; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |
| 3 | drawable/notification_template_icon_bg | values/core_compat.xml; default | 1109016d9687a4d9e126511950c15c2888a8b75b370bff56e9f4263ac206caff | b2e64206327550d610f05ce7ccb0e562302179e792e9cf45c93ad979daf3f8e6 | UnusedResources; drawable/notification_template_icon_bg; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |
| 4 | drawable/notification_template_icon_low_bg | values/core_compat.xml; default | 5ed10548b8af259c6faef99595b5d1f3c20f6db6d329f5c6680bd522e55e9925 | b5e0ebf5f95478989d46fc775c6fcbad58add6cd8bfeeface93ce9a62d8757d9 | UnusedResources; drawable/notification_template_icon_low_bg; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |

## Strings closure

Los 13 strings auth/login y los 9 strings Daily/tabs/registro fueron inspeccionados con búsqueda estática en `app/src`: no existen referencias `R.string`, `stringResource`, `getIdentifier` ni reflexión. La UI vigente usa `UiTexts`/contratos de dominio; por eso se retiraron únicamente declaraciones legacy autorizadas mediante manifiestos inmutables y ledgers before/after/remaining.


## Launcher closure

The merged manifest now roots both `android:icon` and `android:roundIcon` in the GoodLife family. The obsolete generic `ic_launcher` family was removed after reference search, clean installation evidence and package inspection; the round GoodLife adaptive icon includes its monochrome layer. The app still requires install/upgrade/launcher smoke before any future launcher-asset removal.

## Guardrails

- Las filas numeradas son las 4 familias restantes. Los ledgers de colores, auth/login y Daily/tabs/registro registran todos los borrados autorizados; las filas actuales no pueden eliminarse sin evidencia, autorización y una PR enfocada. No queda `needs_owner`; `retain_compat` sólo admite inspección read-only y #39 es el único ámbito para dependencies/AGP/packaging.
- The reducer, when needed for a focused remediation PR, may remove only exact fingerprints explicitly listed in that PR’s `before` snapshot. It must prove that `externalAdvisories`, `exceptions`, `generatedFrom`, canonicalizers and policy are byte-for-byte unchanged.
- Every remediation PR records `before`, `after` and `remaining` snapshots and runs both variants, verifier, assemble and the logical gates. No task is considered complete merely because lint count decreased.

## Evidencia de remediación

- Colores: 35 → 28 símbolos por variante; 14 fingerprints retirados, exactamente los siete `color/*` del manifiesto legacy.
- Auth/login: 28 → 15 símbolos por variante; 26 fingerprints retirados, exactamente los 13 `string/*` del manifiesto auth.
- Daily/tabs/registro: 15 → 6 símbolos por variante; 18 fingerprints retirados, exactamente los 9 `string/*` del manifiesto remaining-strings.
- Launcher contract swap (#60): 6 → 4 símbolos por variante; cuatro fingerprints retirados mediante un manifiesto inmutable que autoriza sólo `mipmap/ic_launcher` e `mipmap/ic_logo_install_round`.
- El reducer fail-closed valida los fingerprints exactos autorizados para cada scope antes de retirar entradas y preserva `generatedFrom`, `externalAdvisories` y `exceptions`.
- Los ledgers `remediation/legacy-colors-38.json`, `remediation/auth-strings-38.json`, `remediation/remaining-strings-38.json` y `remediation/launcher-contract-swap-60.json` conservan owner, issue, búsqueda estática, evidencia lint y estado before/after/remaining.
