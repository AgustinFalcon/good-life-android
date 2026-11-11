# Delta sobre R-03 — Workout detail finish payload

R-03 (`sdd/wip/20260911-goodlife-android-revival/2-technical/R-03-workout-detail.md`) sigue siendo el contrato de **ruta, estados de carga, notes, Lottie y navegación**.

Este delta **reemplaza solo** la frase:

> Domain ids and planned sets come from the loaded `Workout`

por:

> Domain ids come from the loaded `Workout`. Target sets se muestran como semilla editable. El POST envía peso, reps y RPE **de la sesión** (CU-19 reconfirmado), más `startTime`/`endTime`/`durationMinutes` del reloj de pantalla.

Hasta que `/sdd.plan` + implementación mergeen, el código vigente todavía cumple el R-03 original (planned sets en el POST).
