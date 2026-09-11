# GoodLife Android — internal release readiness

- Feature id: `20260911-goodlife-internal-release-readiness`
- Estado: `spec_review`.
- Issue: #8.
- Rama: `chore/android-release-readiness-spec`.
- Idioma: español (es-AR).

## Evidencia observada

`origin/master` `3dcedfc` declara `versionCode = 1`, `versionName = "1.0"`, build type `release` y runner instrumentado. No contiene una configuración de firma, distribución, rollback, instrumented smoke ni telemetría de release versionados. Esa ausencia es un requisito de preparación, no evidencia de una entrega ya lista.

Los secretos de firma, cuentas de distribución, identificadores de testers, certificados y rutas locales se excluyen de estos artefactos.
