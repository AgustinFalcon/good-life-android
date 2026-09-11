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

`.github/workflows/android-ci.yml` ejecuta `./gradlew :app:logicDebugUnitTestCoverageVerification` en push y PR contra `master`. Instrumented tests requieren emulator/device y no forman parte del gate CI actual.

Release tiene minify deshabilitado y versionCode 1/versionName 1.0; no hay firma/pipeline documentado.

## Definition of done

- Gates existentes pasan; ausencias se declaran.
- Contratos y persistencia afectados tienen pruebas.
- No se agregan secretos/artefactos locales.
- Cobertura Android tiene gate JaCoCo sobre logica elegible: core/domain/data logic, navigation core y ViewModels. Excluye Compose UI, theme, DTO/request/response, Room entities/DAO/database, DI, Activity/App, modelos UI y adaptadores no ejecutables en JVM documentados en Gradle. Objetivo configurado: linea >= 80%. Estado verificado el 2026-09-10: 172 tests unitarios, 0 fallos/errores/omitidos y cobertura de líneas 82,83% (1.544/1.864); gate aprobado con `.\\gradlew.bat :app:logicDebugUnitTestCoverageVerification`.
- Docs/agent quedan alineados; la migración del SDD canónico se rastrea en #9.

