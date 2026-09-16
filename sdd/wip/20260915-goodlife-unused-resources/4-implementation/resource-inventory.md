# Inventario de recursos — contrato UR-01

Estado: inventario vigente después de las remediaciones de colores legacy y strings de la issue #38. Las filas numeradas son sólo el conjunto restante y no sustituyen la evidencia inmutable de before/after/remaining.

## Fuente y contrato de derivación

El snapshot se deriva de `config/lint-baseline.json` schema v2. El snapshot autorizado contenía 35 símbolos `UnusedResources` por variante. La PR de colores retiró exactamente 7 familias propias (14 fingerprints) y esta remediación auth/login retira 13 familias (26 fingerprints), dejando 6 símbolos por variante. La baseline es evolutiva: cada ejecución conserva evidencia before/after/remaining y un manifiesto exacto versionado en `remediation/` y `.github/scripts/manifests/`.

La herramienta acepta únicamente mensajes canonicalizados con el formato The resource R.type.name appears to be unused. Debe extraer type/name y fallar cerrado si cambia el formato, si el símbolo no puede parsearse o si hay una discrepancia de multiplicidad del multiset. La proyección semántica para comparar variantes es ruleId + type/name + normalizedMessage + occurrences; no incluye variant ni location. Los fingerprints debug/release se conservan por separado porque contienen la variante.

Cada fila representa una familia type/name, consolidando definiciones, source sets y qualifiers. D×1/R×1 significa una aparición en el multiset de cada variante. Las filas actuales `retain_*` y `needs_owner` no son removibles sin una PR enfocada con autorización, evidencia y review; la clasificación no autoriza un borrado genérico.

## Inventario restante

El inventario vigente se deriva de config/lint-baseline.json schema v2 después de retirar colores legacy (7 familias) y strings de autenticación/login (13 familias). Quedan 15 símbolos (30 fingerprints): 4 recursos de compatibilidad AndroidX, 2 familias launcher, 4 etiquetas Daily, 1 etiqueta de registro y 4 etiquetas de tabs. Cada remediación conserva su ledger before/after/remaining y su manifiesto inmutable.

| # | Symbol | Definitions / qualifiers | Debug fingerprint(s) | Release fingerprint(s) | Semantic key / occurrences | Accountable follow-up | References / evidence searched | Linked issue / PR | Classification | Rationale | Re-evaluate when |
|---:|---|---|---|---|---|---|---|---|---|---|---|
| 1 | color/notification_template_icon_bg_compat | values/core_compat.xml; default | 0be0e1cc35388828913e424cd3a49c154c00d62d9b6f3c85cd052c95da26ead5 | bdde063b1e0b956c2eadbb19658ce33e3529f0f82a53c769a5014af622c61bab | UnusedResources; color/notification_template_icon_bg_compat; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |
| 2 | color/notification_template_icon_low_bg_compat | values/core_compat.xml; default | 326f70e581f7d955a23bf4f6ffdcfeef9c0e02589b7a83bb03d01923b81ef914 | d9ca418fc2e281ee883a96545bd2d81472c4f5baed20d0cf8250c871b128dc5a | UnusedResources; color/notification_template_icon_low_bg_compat; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |
| 3 | drawable/notification_template_icon_bg | values/core_compat.xml; default | 1109016d9687a4d9e126511950c15c2888a8b75b370bff56e9f4263ac206caff | b2e64206327550d610f05ce7ccb0e562302179e792e9cf45c93ad979daf3f8e6 | UnusedResources; drawable/notification_template_icon_bg; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |
| 4 | drawable/notification_template_icon_low_bg | values/core_compat.xml; default | 5ed10548b8af259c6faef99595b5d1f3c20f6db6d329f5c6680bd522e55e9925 | b5e0ebf5f95478989d46fc775c6fcbad58add6cd8bfeeface93ce9a62d8757d9 | UnusedResources; drawable/notification_template_icon_low_bg; canonical unused; D×1/R×1 | Android maintainers; dependency owner via #39 | merged resources/AAR and notification paths | #38 / #39 | retain_compat | AndroidX notification compatibility family; ownership belongs to dependency/toolchain review. | #39 dependency/packaging validation. |
| 5 | mipmap/ic_launcher | mipmap-anydpi/ic_launcher.xml; mipmap-mdpi/ic_launcher.webp; mipmap-hdpi/ic_launcher.webp; mipmap-xhdpi/ic_launcher.webp; mipmap-xxhdpi/ic_launcher.webp; mipmap-xxxhdpi/ic_launcher.webp; manifest/qualifier family | 20471ffb451b7e99c978de1e2674a9f8f75abb15e873d2e0a4f74f221cdf7be0 | a5c5db167da99bb914491a8b3d62a5eb4fb29777f80402e7f0d951a1100ce5e6 | UnusedResources; mipmap/ic_launcher; canonical unused; D×1/R×1 | Android maintainers; launcher owner | manifest merged, all mipmap qualifiers, install/upgrade/launcher smoke | #38 / future focused PR | retain_launcher | Lint location is one member of a manifest-owned launcher family. | After install, upgrade and launcher smoke is executed and recorded with manifest/qualifier evidence. |
| 6 | mipmap/ic_logo_install_round | mipmap-anydpi-v26/ic_logo_install_round.xml; mipmap-mdpi/ic_logo_install_round.webp; mipmap-hdpi/ic_logo_install_round.webp; mipmap-xhdpi/ic_logo_install_round.webp; mipmap-xxhdpi/ic_logo_install_round.webp; mipmap-xxxhdpi/ic_logo_install_round.webp; manifest/qualifier family | e4c8d64253171748d04dc73dcf70d36bcdbd4100476df430f1f15a2e8b5f8e66 | 551f262fe8266f902b0062d9be4400201c4bfb6fba1d06456372aee077abbaeb | UnusedResources; mipmap/ic_logo_install_round; canonical unused; D×1/R×1 | Android maintainers; launcher owner | manifest merged, all mipmap qualifiers, install/upgrade/launcher smoke | #38 / future focused PR | retain_launcher | Lint location is one member of a manifest-owned launcher family. | After install, upgrade and launcher smoke is executed and recorded with manifest/qualifier evidence. |

## Strings closure

Los 13 strings auth/login y los 9 strings Daily/tabs/registro fueron inspeccionados con búsqueda estática en `app/src`: no existen referencias `R.string`, `stringResource`, `getIdentifier` ni reflexión. La UI vigente usa `UiTexts`/contratos de dominio; por eso se retiraron únicamente declaraciones legacy autorizadas mediante manifiestos inmutables y ledgers before/after/remaining.


## Launcher closure

The launcher families are protected roots from the merged manifest: `android:icon=@mipmap/ic_logo_install` and `android:roundIcon=@mipmap/ic_launcher_round`. The closure includes the adaptive `ic_launcher.xml`/`ic_launcher_round.xml` roots, their shared `ic_launcher_foreground`, `ic_launcher_background` and `monochrome` references, and every density-qualified definition listed in the rows. `ic_logo_install_round` is also retained as its anydpi-v26 plus mdpi..xxxhdpi family. A future removal must inspect merged manifest output, all qualifiers and install/upgrade/launcher smoke before changing any member.

## Guardrails

- Las filas numeradas son las 6 familias restantes. Los ledgers de colores y auth/login registran los únicos borrados autorizados; las filas actuales no pueden eliminarse sin evidencia, autorización y una PR enfocada. `needs_owner` permanece abierto hasta registrar owner, evidencia y trigger; `retain_compat` sólo admite inspección read-only en #38 y #39 es el único ámbito para dependencies/AGP/packaging; `retain_launcher` requiere roots del manifest, qualifiers y smoke de instalación/upgrade/launcher.
- The reducer, when needed for a focused remediation PR, may remove only exact fingerprints explicitly listed in that PR’s `before` snapshot. It must prove that `externalAdvisories`, `exceptions`, `generatedFrom`, canonicalizers and policy are byte-for-byte unchanged.
- Every remediation PR records `before`, `after` and `remaining` snapshots and runs both variants, verifier, assemble and the logical gates. No task is considered complete merely because lint count decreased.

## Evidencia de remediación

- Colores: 35 → 28 símbolos por variante; 14 fingerprints retirados, exactamente los siete `color/*` del manifiesto legacy.
- Auth/login: 28 → 15 símbolos por variante; 26 fingerprints retirados, exactamente los 13 `string/*` del manifiesto auth. El alcance sólo toca `app/src/main/res/values/strings.xml`; no modifica Kotlin, Compose, compatibilidad, launcher, Gradle, manifest ni UI.
- El reducer fail-closed valida los fingerprints exactos autorizados para cada scope antes de retirar entradas y preserva `generatedFrom`, `externalAdvisories` y `exceptions`.
- Los ledgers `remediation/legacy-colors-38.json` y `remediation/auth-strings-38.json` conservan owner, issue, búsqueda estática, evidencia lint y estado before/after/remaining.
