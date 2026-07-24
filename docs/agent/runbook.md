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

- `.\gradlew.bat test`
- `.\gradlew.bat assembleDebug`
- `.\gradlew.bat connectedAndroidTest`

## Local

Abrir en Android Studio o instalar `app/build/outputs/apk/debug`; requiere JDK compatible con AGP, Android SDK 35 y backend configurado.

## CI / release

No se detectó `.github/workflows`; instrumented tests requieren emulator/device.

Release tiene minify deshabilitado y versionCode 1/versionName 1.0; no hay firma/pipeline documentado.

## Definition of done

- Gates existentes pasan; ausencias se declaran.
- Contratos y persistencia afectados tienen pruebas.
- No se agregan secretos/artefactos locales.
- Docs/agent y SSD quedan alineados.

