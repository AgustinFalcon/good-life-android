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
- `POST /api/v1/workouts/log/finish` envía `notes` como string (vacío si no hay feedback); no se omite el campo como null.
- El mismo POST lleva `startTime`/`endTime` del reloj de pantalla (`DateProvider.now()` al publicar Content y al confirmar Finish) y `durationMinutes` ≥ 1. Peso/reps salen de los inputs de Content (semilla = targets; blank → `0.0`/`0`). `rpe` es 1–10 o null (`PerceivedExertion.fromWire`). Los inputs se identifican con `WorkoutTexts.setWeightLabel` / `setRepsLabel` / `setRpeLabel` (via `WorkoutSetField.label`); no hay literales en la Screen.
- Un finish exitoso no navega desde el ViewModel: `Content.finished` dispara `SuccessDialog` y el Owner resuelve el pop con `onNavigateUp`. Un fallo de POST no usa `UiState.Error` ni `dataLoadError`; queda en Content con `WorkoutTexts.finishFailed` (servidor/404) o `connectionError` (red).
- `POST /api/v1/meal-plans` envía un plan por comida: `mealId`, `mealType`, `scheduledTime`, `daysOfWeek`, `startDate`, `endDate`. `MealType` de wire: `BREAKFAST`, `LUNCH`, `DINNER`, `AFTERNOON_SNACK`, `SNACK`.
- `GET /api/v1/meal-plans` devuelve todos los planes activos (`mealName`, `daysOfWeek`, `startDate`, `endDate`) y no filtra por query `date`. El cliente mapea `mealName` (fallback `name`) y recorta al día pedido.
- En Diario, la card de workout usa `exerciseCount` si es > 0; si no, `Día {dayOrder}`. Un hábito `COMPLETED` con log menor a la meta muestra la meta.
- Owners manejan DI/side effects; screens son puros.
- Textos via AppLanguage y fecha via DateProvider.
- No exponer JWT/credenciales en logs; refresh 401 está implementado y requiere validación en dispositivo.

## Rutina activa opcional

GET /api/v1/routines/active puede responder exitosamente sin data cuando el usuario no tiene una rutina activa. Sólo esa frontera propaga Routine?; el caso de uso lo traduce a NoRoutine. No extender esta semántica a endpoints con payload obligatorio.
