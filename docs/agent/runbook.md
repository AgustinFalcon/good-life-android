---
type: Runbook
version: recovery-audit-2026-09
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

## CI / release

`.github/workflows/android-ci.yml` ejecuta `./gradlew :app:logicDebugUnitTestCoverageVerification` en push y PR contra `master`. Instrumented tests requieren emulator/device y no forman parte del gate CI actual.

Release tiene minify deshabilitado y versionCode 1/versionName 1.0; no hay firma/pipeline documentado.

## Definition of done

- Gates existentes pasan; ausencias se declaran.
- Contratos y persistencia afectados tienen pruebas.
- No se agregan secretos/artefactos locales.
- Cobertura Android tiene gate JaCoCo sobre logica elegible: core/domain/data logic, navigation core y ViewModels. Excluye Compose UI, theme, DTO/request/response, Room entities/DAO/database, DI, Activity/App, modelos UI y adaptadores no ejecutables en JVM documentados en Gradle. Objetivo configurado: linea >= 80%. Estado final medido post-rebase: 80.07%, gate aprobado con `.\gradlew.bat :app:logicDebugUnitTestCoverageVerification`.
- Docs/agent y SSD quedan alineados.

