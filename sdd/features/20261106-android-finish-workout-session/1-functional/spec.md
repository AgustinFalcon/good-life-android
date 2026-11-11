# Especificación funcional — Sesión real al terminar un workout

## Objetivo

El usuario que abre el detalle de un workout de su rutina activa registra **lo que hizo**, no solo la plantilla: hora de inicio, hora de fin, duración en minutos, peso y repeticiones por serie, RPE opcional, y notas de sesión. El POST existente `POST /api/v1/workouts/log/finish` deja de enviar `durationMinutes = 0` ni copiar targets a ciegas desde el dominio sin pasar por los campos editables de la pantalla.

Esto cierra el hueco entre el contrato Android R-03 (detalle + finish con plantilla + notes + Lottie) y el caso de uso de producto CU-19 (series, pesos, reps y RPE reales).

## Por qué existe cada pieza

| Pieza | Por qué | Fuente |
|-------|---------|--------|
| Reloj inicio/fin | El log y el DTO guardan `startTime` y `endTime`. Sin reloj, el cliente inventa el mismo instante dos veces. | DTO backend; `API_ENDPOINTS.md`; tabla `workout_log` en SDD técnico histórico |
| Duración en minutos | El backend espera `durationMinutes`. Debe salir del intervalo inicio→fin, no de un literal 0. | Mismo DTO; CU-19 / `workout_log.duration_minutes` |
| Sets editables (peso/reps) | CU-19 y US-TRN-004: registrar lo **hecho**. R-03 solo **mostraba** target sets. Los targets siguen siendo la semilla de los campos. | CU-19; training US-TRN-004; R-03 (display) |
| RPE | Campo opcional del set en el DTO (`rpe`). Producto: esfuerzo percibido 1–10 o vacío. | DTO `LogSetRequest.rpe`; CU-19 |
| Notes de sesión | Ya en R-03/changelog: feedback del usuario, string no null. | R-03; `contracts.md` |
| Lottie + `onNavigateUp` | Celebración y pop del tab **sin** `NavController` en el ViewModel. | R-03; `architecture.md`; `contracts.md` |
| DateProvider | Fechas/horas de dispositivo, testeable. | `contracts.md`; SPEC-004 |
| Copy AppLanguage | Sin strings sueltos. | R-03 localize; `contracts.md` |

## Qué se construye (alcance)

1. **Sesión con reloj.** Al quedar el detalle en Content (GET de rutina activa OK y workout encontrado), se captura el inicio con `DateProvider.now()`. Al confirmar “Terminar workout”, se toma el fin. La duración es minutos enteros de ese intervalo (mínimo 1).
2. **Series reales.** Cada set muestra y deja editar peso (kg) y repeticiones, con label AppLanguage que identifica el campo. Valores iniciales = targets de la rutina activa (`WorkoutSet.targetWeight` / `targetReps`). El POST usa los números parseados de esos campos, no un segundo recorrido de la plantilla de dominio.
3. **RPE.** Campo por set, vacío o 1–10, con label AppLanguage. Vacío o valor fuera de rango se envía ausente (`null` en el DTO).
4. **Robustez del POST.** Un segundo tap no dispara otro POST. Un fallo de finish **no** reemplaza Content por `UiState.Error` (eso sigue siendo de la **carga** GET). Las notas y los sets editados se conservan; se muestra error localizado en el mismo Content. Retry del GET sigue releyendo la rutina (R-03) y descarta la sesión de pantalla (Loading → nuevo Content o Error/NotFound).

## Qué no se construye

- Botón “Start workout” aparte (R-03 solo define Finish como CTA de sesión).
- Cronómetro en vivo segundo a segundo en pantalla (no está en ningún spec; el reloj es de captura inicio/fin para el POST).
- Editar el catálogo / la rutina plantilla / las notas por ejercicio (siguen viajando al POST desde el dominio cargado, como hoy).
- XP, PRs o Daily COMPLETED en Android (los hace el backend al persistir el log; CU-19 resultado esperado del servidor).
- Freestyle sin `routineId`/`workoutId`.
- iOS/KMP, push, deep links.

## Quién y cuándo (flujo)

```
1. Usuario autenticado abre Workouts (rutina activa existente).
2. Toca una card → TabRoute.WorkoutDetail(workoutId).   [R-03]
3. El ViewModel llama GetActiveRoutineUseCase() (GET remoto
   /api/v1/routines/active; el UseCase no cachea). Loading.  [R-03]
4. Si el workout está en la rutina: el VM guarda loadedRoutineId /
   loadedWorkout (caché de pantalla, no de repositorio) y publica
   Content: nombre, ejercicios, campos de set sembrados con targets,
   notes editables, inicio de sesión = DateProvider.now().
5. Usuario entrena: puede cambiar peso, reps, RPE y notes en Content.
6. Toca “Terminar workout” (habilitado si hay ejercicios y no está finishing/finished).
7. Se congela fin = DateProvider.now(); durationMinutes = minutos(inicio, fin) ≥ 1.
8. POST finish con ids de dominio, times, duration, notes, y peso/reps/RPE
   parseados de los campos de Content (no un re-mapeo ciego de loadedWorkout.sets).
9a. Success → Content.finished = true → SuccessDialog Lottie → onNavigateUp. [R-03]
9b. Fallo de red/servidor/404 de finish → se queda en Content + mensaje; no Lottie.
9c. Sin ejercicios → no POST (regla de dominio EmptyExercises); copy localizado en Content.
```

## Semilla y parseo (producto, alineado al DTO)

El DTO `FinishSetRequest` exige `weight: Double` y `reps: Int` no null. El dominio de plantilla tiene `WorkoutSet.targetWeight: Double?` y `targetReps: Int` (el mapper de rutina ya convierte reps null de red a 0).

| Campo UI | Semilla al publicar Content | Al confirmar Finish (número que viaja en el POST) |
|----------|-----------------------------|-----------------------------------------------------|
| Peso | Texto del target (mismo formateo que R-03: entero sin decimal si es `.0`); vacío si `targetWeight == null` | `toDoubleOrNull()`; si blank o no numérico → `0.0` (mismo fallback que el POST vigente `targetWeight ?: 0.0`) |
| Reps | `targetReps.toString()` | `toIntOrNull()`; si blank o no numérico → `0` (mismo patrón que CreateRoutine al tipear reps) |
| RPE | Vacío | Vacío / no 1–10 → ausente (`null`). 1–10 → ese entero |

Si el usuario no toca los sets, el POST lleva esos números de semilla parseados (targets, o `0.0`/`0` cuando no había target de peso). No hay un camino paralelo que lea otra vez `loadedWorkout.sets` ignorando la UI.

## Criterios de aceptación

1. Tras load exitoso, `startTime` del draft es `DateProvider.now()` al publicar Content y `endTime` es `DateProvider.now()` al confirmar Finish; `endTime` ≥ `startTime`. No se usa un Instant hardcodeado de producción.
2. `durationMinutes` es el entero de minutos entre esos instantes, nunca 0 si el usuario confirmó Finish (mínimo 1).
3. Si el usuario no edita los sets, el POST lleva peso/reps iguales a la semilla parseada (targets, o `0.0` si el target de peso era null). Esos números salen de los campos de Content, no de un segundo mapeo ciego de plantilla.
4. Si el usuario cambia peso, reps o RPE, el POST lleva esos valores parseados, no los targets originales.
5. RPE vacío o fuera de 1–10 no se fabrica como número (`null`). RPE informado está en 1–10.
6. `notes` sigue siendo string, vacío si no escribió (`contracts.md`).
7. Success: `finished` true, Lottie existente `R.raw.success_animation`, pop con `onNavigateUp`. El ViewModel no navega.
8. Un segundo Finish mientras `isFinishing` no llama otra vez al UseCase.
9. Fallo de POST no dispara `WorkoutDetailUiState.Error` ni `NotFound` de GET, ni pierde notes/sets.
10. Fallo de GET (load) sigue siendo `Error`/`NotFound` con Retry que relee (R-03). Retry de GET pasa por Loading y no conserva la sesión anterior (nuevo reloj si vuelve Content).
11. Copy ES/EN/PT vía `AppLanguage`. Ningún literal de error de servidor en UI (spec localize). Fallo de persistencia no reutiliza el copy de **carga** (`errorTexts.dataLoadError`).
12. Tests unitarios: duración ≥ 1 con `now()` avanzado en el fake de test; RPE null vs 1–10; finish error conserva Content; doble tap una sola llamada; Success sigue el happy path actual; peso null de plantilla → `0.0` si el campo quedó vacío.

## Decisiones de producto registradas aquí (no estaban en R-03)

| Tema | Decisión | Alternativa descartada |
|------|----------|------------------------|
| Inicio del reloj | Al publicar Content tras load (y se resetea si Retry de load tiene éxito de nuevo) | Botón Start (no existe en R-03) |
| UI de timer | Sin ticker visible obligatorio | Cronómetro animado (no especificado) |
| Duración 0 | Prohibida en el request de un finish confirmado; mínimo 1 minuto | Enviar 0 (estado actual, insuficiente) |
| Sets | Editables; semilla = target; POST = parseo de Content | POST ciego de plantilla (R-03 viejo) vs formulario vacío (rompe R-03 display) |
| Peso/reps vacíos al confirmar | Fallback numérico del DTO vigente (`0.0` / `0`); no se bloquea el POST ni se inventa un toast | Rechazar Finish (no está en CU-19 ni R-03) |
| RPE inválido | Se envía ausente, no un entero inventado | Clamp a 10 o bloquear Finish (no especificado) |

## Fuera de alcance adicional

Cambiar el backend, el path del POST, el Lottie, o el grafo de tabs.
