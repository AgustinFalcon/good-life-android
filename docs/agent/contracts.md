---
type: Contracts
version: foundation-2026-11-11c
validated: 2026-11-11
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
- `POST /api/v1/workouts/log/finish` envía `notes` como string (vacío si no hay feedback); no se omite el campo como null. `Content.notes` arranca vacío: no se siembra con las notas de plantilla del workout.
- Un POST finish 400/`UnknownApi` se traduce a `FinishWorkoutResult.ServerError` y la UI muestra `WorkoutTexts.finishFailed`. Series vacías son `EmptyExercises`. El botón Finish exige al menos una serie.
- El mismo POST lleva `startTime`/`endTime` del reloj de pantalla (`DateProvider.now()` al publicar Content y al confirmar Finish) y `durationMinutes` ≥ 1. Peso/reps salen de los inputs de Content (semilla = targets; blank → `0.0`/`0`). `rpe` es 1–10 o null (`PerceivedExertion.fromWire`). Los inputs se identifican con `WorkoutTexts.setWeightLabel` / `setRepsLabel` / `setRpeLabel` (via `WorkoutSetField.label`); no hay literales en la Screen.
- Un finish exitoso no navega desde el ViewModel: `Content.finished` dispara `SuccessDialog` y el Owner resuelve el pop con `onNavigateUp`. Un fallo de POST no usa `UiState.Error` ni `dataLoadError`; queda en Content con `WorkoutTexts.finishFailed` (servidor/404) o `connectionError` (red).
- `POST /api/v1/meal-plans` envía un plan por comida: `mealId`, `mealType`, `scheduledTime`, `daysOfWeek`, `startDate`, `endDate`. `MealType` de wire: `BREAKFAST`, `LUNCH`, `DINNER`, `AFTERNOON_SNACK`, `SNACK`.
- `GET /api/v1/meal-plans` devuelve todos los planes activos (`mealName`, `daysOfWeek`, `startDate`, `endDate`) y no filtra por query `date`. El cliente mapea `mealName` (fallback `name`) y recorta al día pedido.
- En Diario, la card de workout usa `AppLanguage` (`WorkoutTexts.dailyRoutineFormat` + `dailyExercisesFormat` o `dayFormat`). El DTO no arma copy en español. Un hábito `COMPLETED` con log menor a la meta muestra la meta. Type/status de daily desconocidos se descartan; categoría/dificultad/objetivo/unidad desconocidos son `UNKNOWN` (copy “Otro”), nunca CUSTOM/BEGINNER/STRENGTH/GRAM.
- `POST /api/v1/meals` y `POST /api/v1/ingredients` se arman en data desde drafts de dominio. Profile sube con `LocalImageRef`; el ViewModel no toca `UserDao`.
- Owners manejan DI/side effects; screens son puros.
- Errores 4xx/5xx de login, register, create habit/task, catálogo de rutina y activate-routine se muestran con `AppLanguage`; el texto del backend no entra a `UiState`. Daily/Meals `ServerError` es un caso sin payload.
- Ingredientes: request/response `baseQuantity`/`baseUnit`. `Ingredient.servingUnit` y `MealSummary.servingUnit` son `PortionUnit` (`fromWire`, nunca default a GRAM ni “porción”). `MealIngredientDraft.unit` y `CustomMealIngredientDraft.unit` son `PortionUnit`. HTTP 5xx (no solo 500) es `ServerException`. `Workout.dayOfWeek` es `DayOfWeek` (ISO 1–7; fuera de rango se omite).
- 401 sin refresh / refresh nulo / retry fallido corre `SessionCleanup` (tokens, biométrica, Room user+daily) y emite `SessionExpired`. Un 401 de `/token` con `grantType=password` no limpia sesión (aunque quede refresh residual). Splash auto-login exige tokens y usuario Room. Si el login no puede persistir el perfil, hace rollback de tokens. Un GET Daily anterior a un PATCH no pisa el toggle (`dataGeneration`); la navegación de fecha no espera el PATCH.
- Textos via AppLanguage y fecha via DateProvider.
- No exponer JWT/credenciales en logs. El logger HTTP de debug es `BASIC` (sin headers); `HEADERS`/`BODY` se degradan y `Authorization`/`refreshToken` se redactan. Refresh 401 corre `SessionCleanup` (pasos aislados) y el login descarta usuario/cache Daily previos.

## Rutina activa opcional

GET /api/v1/routines/active puede responder exitosamente sin data cuando el usuario no tiene una rutina activa. Sólo esa frontera propaga Routine?; el caso de uso lo traduce a NoRoutine. No extender esta semántica a endpoints con payload obligatorio.
