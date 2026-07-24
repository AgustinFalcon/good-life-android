---
type: Runbook
version: a841b13
validated: 2026-07-13
update_when: "Cambien build, tests, ejecución, CI o release."
scope:
  - README.md
  - docs
---

# Runbook

## Comandos

- `.\gradlew.bat :app:testDebugUnitTest`
- `.\gradlew.bat assembleDebug`
- `.\gradlew.bat connectedAndroidTest`

## Local

Abrir en Android Studio o instalar `app/build/outputs/apk/debug`; requiere JDK compatible con AGP, Android SDK 35 y backend configurado.

## CI / release

`.github/workflows/android-ci.yml` ejecuta `./gradlew :app:testDebugUnitTest` en push y PR contra `master`. Instrumented tests requieren emulator/device y no forman parte del gate CI actual.

Release tiene minify deshabilitado y versionCode 1/versionName 1.0; no hay firma/pipeline documentado.

## Definition of done

- Gates existentes pasan; ausencias se declaran.
- Contratos y persistencia afectados tienen pruebas.
- No se agregan secretos/artefactos locales.
- Cobertura Android no tiene gate configurado; declarar ausencia hasta definir clases logicas elegibles y herramienta de reporte.
- Docs/agent y SSD quedan alineados.

