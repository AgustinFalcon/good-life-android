---
type: Contracts
version: recovery-audit-2026-09
validated: 2026-09-10
update_when: "Cambien endpoints, DTOs, auth, estados o integraciones."
scope:
  - README.md
  - app/build.gradle.kts
  - app/src/main/java/com/agusstkd/goodlife
  - docs
---

# Contracts

## Expuestos

- Android applicationId `com.agusstkd.goodlife`, minSdk 26, target/compile 35.
- Los iconos instalables `ic_logo_install` e `ic_logo_install_round` son adaptive icons GoodLife con capa monochrome; el manifest los usa como icon y roundIcon. La familia genérica `ic_launcher` fue retirada tras búsqueda de referencias y validación de empaquetado. Todo cambio futuro de launcher exige smoke de instalación/upgrade/launcher.
- Android mantiene `android:allowBackup=false`; no hay contrato de backup o device transfer para datos locales.
- Routes AppRoute Splash/Login/Register/Main/Create* y TabRoute Daily/DailyDetail/Workouts/WorkoutDetail/Meals/MealDetail/Settings/Profile. DailyDetail carries itemId + ISO date; MealDetail carries planId + ISO date.

## Consumidos

- GoodLife REST `/api/v1` por Retrofit
- Room
- Koin 4, Compose, Navigation, OkHttp
- Biometric y EncryptedSharedPreferences APIs

## Compatibilidad

- Mantener DTOs Retrofit alineados al backend.
- Owners manejan DI/side effects; screens son puros.
- Textos via AppLanguage y fecha via DateProvider.
- No exponer JWT/credenciales en logs; refresh 401 está implementado y requiere validación en dispositivo.

## Rutina activa opcional

GET /api/v1/routines/active puede responder exitosamente sin data cuando el usuario no tiene una rutina activa. Sólo esa frontera propaga Routine?; el caso de uso lo traduce a NoRoutine. No extender esta semántica a endpoints con payload obligatorio.
