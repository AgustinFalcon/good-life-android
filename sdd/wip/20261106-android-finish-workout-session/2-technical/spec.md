# Especificación técnica — Sesión real al terminar un workout

## Arquitectura (sin cambiar capas)

```
Owner (tab NavController::navigateUp)
  → Screen pura (onAction)
  → WorkoutDetailViewModel
  → FinishWorkoutUseCase → FinishWorkoutResult
  → WorkoutLogRepository → WorkoutLogRemoteDataSource
  → WorkoutLogApiService POST api/v1/workouts/log/finish
```

Igual que `architecture.md` y el wiring actual en `routineModule`: `GetActiveRoutineUseCase` (GET rutina) y `FinishWorkoutUseCase` (POST log) son puertos distintos. DateProvider ya está inyectado en el VM vía Koin (`dateProvider = get()`). No `NavController` ni `Context` en el VM. No segundo repositorio. No meter finish en `RoutineRepository`.

`GetActiveRoutineUseCase` / `RoutineRepositoryImpl.getActiveRoutine()` son **remote-only**. La única caché es de presentación: `loadedRoutineId` y `loadedWorkout` en el ViewModel, más el instante de inicio de sesión. Cada `load()` / `OnRetry` vuelve a llamar al UseCase.

## Contrato HTTP (no se inventa)

Body alineado a `FinishWorkoutRequest` del backend y al DTO Android ya existente:

- `routineId`, `workoutId`, `name`, `durationMinutes`, `startTime`/`endTime` (ISO string vía `LocalDateTime.toString()`), `notes` (string, nunca omitir), `exercises[].exerciseMasterId` (= `WorkoutExercise.exerciseId`), `sets[]`: `setNumber`, `weight`, `reps`, `rpe` (nullable).

Mapper: `FinishWorkoutDraft.toRequest()` permanece el único traductor draft→DTO.

## Dominio

- `FinishWorkoutDraft` / `FinishSetDraft` no cambian de forma: `weight: Double`, `reps: Int`, `rpe: Int?`. El VM deja de mandar `durationMinutes = 0` y `startTime == endTime` salvo intervalo real < 1 min, en cuyo caso duración = 1 y end ≥ start.
- RPE: tipo cerrado de dominio `PerceivedExertion` (no un rango suelto en UI ni en el UseCase):
  - `Absent`
  - `Score` (entero 1–10, constructor no público)
  - `Unknown` (wire/texto que no entra en el conjunto; la pantalla no lo trata como RPE válido)
  - único `fromWire(raw: String?): PerceivedExertion` en el borde presentación→dominio.
  - Al armar `FinishSetDraft.rpe`: `Score` → `value`; `Absent` y `Unknown` → `null`. Tests del tipo + `fromWire` (1, 10, vacío, `"11"`, `"abc"`).
- Validación `EmptyExercises` se queda en el UseCase (antes del repo).
- El UseCase **no** calcula duración ni parsea inputs; recibe el draft ya cerrado. El reloj y el parseo son de presentación + `DateProvider`.

## Reloj de sesión

- Campo privado del VM (no va en UiState): `Instant` de inicio.
- Inicio: `dateProvider.now()` cuando el load publica `Content` (éxito con workout encontrado).
- Fin: `dateProvider.now()` al confirmar Finish (después del guard `isFinishing`).
- Conversión a `LocalDateTime` local: mismo patrón ya usado (`toLocalDateTime(TimeZone.currentSystemDefault())`).
- `durationMinutes = (end - start).inWholeMinutes.toInt().coerceAtLeast(1)`.
- Load `NotFound` / `Error` (GET): borrar `loadedWorkout`, `loadedRoutineId` y el instante de inicio (sesión inválida). Hoy el GET `NotFound` del UseCase no limpia esos campos; este feature lo exige.
- Retry de load: `Loading` (R-03); si vuelve Content, nuevo inicio (nueva sesión de pantalla). Los edits anteriores no se conservan: R-03 Retry relee.

### FakeDateProvider (tests)

`DateProvider` de producción no cambia. `FakeDateProvider.today()` sigue siendo la `LocalDate` del constructor (Daily y el resto de tests no se mutan).

`now()` hoy es `fixedDate.atStartOfDayIn(TimeZone.UTC)` y el KDoc ya dice extender la clase si hace falta otro timestamp. El VM usa **la misma** instancia entre Content y Finish, así que un `nowInstant` de solo constructor no alcanza.

Contrato de test (default = comportamiento actual; `today()` no se muta):

- `FakeDateProvider` (o subclase en test source) expone un `nowInstant` inicializado a inicio del día UTC de `fixedDate`.
- `now()` lee ese Instant.
- `advance(duration)` solo mueve `nowInstant`. No cambia `today()`.
- Tests de Daily siguen con `FakeDateProvider(LocalDate)` y no llaman `advance`.
- Tests de duración del detalle: `advance` entre Content y Finish sobre esa instancia.

## UiState / UiAction (delta sobre R-03)

Conservar: `Loading`, `Content`, `NotFound`, `Error` (GET), `OnRetry`, `OnNotesChange`, `OnFinishWorkout`, `finished`, `notes`.

Agregar en **Content** (no un estado paralelo):

- `isFinishing: Boolean` — POST en vuelo; botón y campos deshabilitados.
- `finishError: String?` — copy ya localizado; no usar `UiState.Error` ni `NotFound` de GET.

Sets en UiModel:

- `WorkoutExerciseUiModel.id` = `WorkoutExercise.id` (identidad de fila).
- `exerciseMasterId` = `WorkoutExercise.exerciseId` (POST).
- `WorkoutSetUiModel`: `id` (`WorkoutSet.id`), `setNumber`, `weightInput` / `repsInput` / `rpeInput` (String de edición). `label` puede seguir con `setFormat`. `summary` deja de ser la fuente del POST; los tests de mapping que afirman `summary` se actualizan a los inputs.

UiAction cerrada para edición de set: `OnSetFieldChange(exerciseId, setId, field, value)` con `WorkoutSetField` sellado = `Weight` | `Reps` | `Rpe`. No strings mágicos de campo. `exerciseId` aquí es `WorkoutExerciseUiModel.id`, no el master.

Semilla de inputs (misma regla que la spec funcional): peso formateado como el mapping R-03 vigente (entero sin `.0`); vacío si `targetWeight == null`; reps = `targetReps.toString()`; RPE vacío.

Label de cada input: `WorkoutSetField.label(WorkoutTexts)` (`setWeightLabel` / `setRepsLabel` / `setRpeLabel`). La Screen no copia literales.

Parseo al armar el draft (borde VM, no UseCase):

- peso: `weightInput.toDoubleOrNull() ?: 0.0`
- reps: `repsInput.toIntOrNull() ?: 0`
- rpe: `PerceivedExertion.fromWire(rpeInput)` → `Int?` según arriba

## Finish — secuencia en el VM

1. Si no hay `loadedWorkout` o no hay `Content` → return.
2. Si `finished || isFinishing` → return (doble tap).
3. Publicar `isFinishing = true`, `finishError = null` **antes** del `launch`.
4. Armar draft: ids de dominio (`loadedRoutineId`, `workoutId` del constructor, `exerciseMasterId`), notes de sesión de Content, `FinishExerciseDraft.notes` desde `WorkoutExercise.notes` (no editables en esta pantalla; mismo mapeo vigente), sets parseados de UiModel, times del reloj, duration ≥ 1.
5. `when (FinishWorkoutResult)` — el UseCase no emite copy; el VM traduce a `AppLanguage` (mismo patrón que `CreateRoutineValidation` → `CreateRoutineTexts`):

| Resultado | UiState | Copy |
|-----------|---------|------|
| `Success` | Content `finished = true`, `isFinishing = false` | — (Lottie) |
| `EmptyExercises` | Content + `finishError`, `isFinishing = false` | `WorkoutTexts.noExercises` (ya existe) |
| `NetworkError` | Content + `finishError` | `ErrorTexts.connectionError` (ya existe; es red, no carga) |
| `ServerError` | Content + `finishError` | `WorkoutTexts.finishFailed` (**clave nueva**). No reutilizar `errorTexts.dataLoadError` (“cargar datos”). Copy = el `errorServer` ya usado en create-flows, no un texto nuevo: ES `Error del servidor, intentá de nuevo` / EN `Server error, please try again` / PT `Erro do servidor, tente novamente`. |
| `NotFound` de **POST** | Content + `finishError` (no `UiState.NotFound`) | la misma `finishFailed`. `workoutNotFound` / `UiState.NotFound` siguen siendo del GET. |

6. Screen: Lottie solo si `finished`; banner/texto de `finishError` si no null. Sin literales.

GET sigue igual que R-03: `ServerError` → `UiState.Error(dataLoadError)`; `NetworkError` → `UiState.Error(connectionError)`; workout ausente o UseCase `NotFound` → `UiState.NotFound`.

## Persistencia / Koin

Sin endpoints nuevos. `routineModule` ya inyecta `FinishWorkoutUseCase`, `GetActiveRoutineUseCase` y `DateProvider` en `WorkoutDetailViewModel`. No segundo repositorio. Clase Mapper aparte no: el repo usa extensiones `toRequest`.

## Tests (proporcionales)

- Existentes de mapping, notes→draft, Success `finished` se **conservan** y se actualizan: el draft de finish sale de inputs (semilla), `durationMinutes >= 1`, y no se afirma `summary` si deja de existir. El happy path actual con `FakeDateProvider(LocalDate)` (start==end de `now()`) sigue siendo válido: duración coerce a 1.
- Nuevos: duración > 1 con fake cuya `now()` avanza entre Content y Finish; `PerceivedExertion.fromWire`; RPE vacío vs 8 en `lastDraft`; peso/reps editados en el `lastDraft` del fake; `targetWeight == null` + input vacío → `weight == 0.0`; segundo `OnFinishWorkout` no incrementa `finishWorkoutCallCount`; `ServerException` en finish deja `Content` con notes y `finishError == texts.finishFailed`, no `UiState.Error`.
- UseCase tests actuales se mantienen (no calculan duración).

## Docs ya apuntados en el paso de spec (no repetir)

- `docs/agent/architecture.md`: párrafo vigente describe el código R-03; párrafo “next contract” ya apunta a este feature. El párrafo vigente se reescribe cuando el código mergea.
- `CHANGELOG.md` Unreleased ya tiene la viñeta de spec. Viñetas de código al implementar.
- R-03: ver `R-03-delta.md` en esta carpeta.

Al implementar (no en este paso): `docs/agent/contracts.md` si se formaliza duration/RPE; copy `finishFailed` en `Spanish`/`English`/`Portuguese` + `WorkoutTexts`.

## No hacer

- `stateIn` / quitar `init { load() }` (fuera de este feature; deuda previa).
- Meter finish en `RoutineRepository`.
- Clase Mapper aparte (el repo usa extensiones `toRequest`).
- Mutar `FakeDateProvider.today()` para tests de duración.
- Reusar `dataLoadError` / `UiState.Error` / `UiState.NotFound` para fallos del POST.
