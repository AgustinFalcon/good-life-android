---
type: Runbook
version: recovery-audit-2026-09.1
validated: 2026-09-10
update_when: "Cambien build, tests, ejecución, CI o release."
scope:
  - README.md
  - docs
---

# Runbook

## Comandos

- `.\gradlew.bat :app:testDebugUnitTest`
- `.\gradlew.bat :app:logicDebugUnitTestCoverageVerification` ? gate JaCoCo de clases logicas elegibles
- `.\gradlew.bat assembleDebug`
- `.\gradlew.bat connectedAndroidTest`

## Local

Abrir en Android Studio o instalar `app/build/outputs/apk/debug`; requiere JDK compatible con AGP, Android SDK 35 y backend configurado; URL canónica: https://good-life.ddns.net/.

## Regresiones de concurrencia

Las pruebas de Login cubren el rechazo de intentos manual y biométrico superpuestos. Daily y Meals simulan una fuente que entrega una respuesta antigua aun después de cambiar de fecha y verifican que no publique estado obsoleto. No sustituir esos escenarios por `CompletableDeferred.await()` cancelable: no ejercita el guard de publicación.
## CI / release

`.github/workflows/android-ci.yml` fija `actions/checkout` a `${{ github.sha }}` en ambos jobs: en PR valida el SHA de integración que GitHub Actions evalúa contra `master`, y en push valida el SHA publicado. Sobre ese mismo ref ejecuta cobertura lógica debug (`:app:logicDebugUnitTestCoverageVerification`), genera `:app:lintDebug` y `:app:lintRelease`, y compara las reglas deterministas de ambos XML con `config/lint-baseline.json` mediante un multiset exacto antes de ensamblar la variante release no firmada. `GradleDependency` y `AndroidGradlePluginVersion` quedan como advisories externos: cada PR publica delta JSON/XML y, si existe drift, summary sin falsear el gate por un feed remoto, mientras `lint-advisory-monitor.yml` sobre `master` falla programada o manualmente si cambian. El segundo job no firma ni sube/distribuye APK o AAB; sí conserva XML y delta JSON de lint como evidencia no ejecutable. Sus Actions oficiales se fijan por SHA inmutable y se actualizan por PR cuando el proveedor las depreca. Instrumented tests requieren emulator/device y no forman parte del gate CI actual.

### Internal release readiness (contract only)

El contrato operativo está en [release-readiness.md](release-readiness.md). Esta etapa no genera firma ni distribuye: si falta una decisión aprobada, mecanismo externo o evidencia, el resultado correcto es `BLOCKED`.

Un candidato exacto se identifica fuera de Git por `commit_sha`, `versionCode`, `versionName`, tipo de artefacto, `artifact_sha256` y una referencia opaca al registro externo. Antes de distribución se exige: PR revisada, tests unitarios, cobertura elegible, `lintRelease` o tarea release equivalente, gobierno/diff sin secretos, R-01 debug explícito y smoke UI redacted del artefacto release firmado exacto. Cada gate usa `PASS`, `BLOCKED` o `FAIL`; `BLOCKED`/`FAIL` detiene la distribución.

El rollback sólo puede terminar como `withdrawn`, `restored_verified` o `blocked_incompatible`. `restored_verified` requiere verificar el canal y un arranque/login redacted del candidato restaurado. La firma, proveedor, canal, audiencia y rutas externas siguen bloqueados hasta aprobación explícita. El baseline actual mantiene `versionCode` 1, `versionName` 1.0 y minify deshabilitado; esos valores no constituyen una release distribuible.
## Definition of done

- Gates existentes pasan; ausencias se declaran.
- Contratos y persistencia afectados tienen pruebas.
- No se agregan secretos/artefactos locales.
- Cobertura Android tiene gate JaCoCo sobre logica elegible: core/domain/data logic, navigation core y ViewModels. Excluye Compose UI, theme, DTO/request/response, Room entities/DAO/database, DI, Activity/App, modelos UI y adaptadores no ejecutables en JVM documentados en Gradle. Objetivo configurado: linea >= 80%. Estado de master medido el 2026-09-11: 80.38% (1.483/1.845), gate aprobado con `.\gradlew.bat :app:logicDebugUnitTestCoverageVerification`.
- Docs/agent y SSD quedan alineados.
