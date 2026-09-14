# Spec pre-build review — Daily y Meal Plan details

- Mode: local, revisión adversarial independiente.
- Target: `20260911-goodlife-daily-meals-details` / issue #7.
- Baseline único: `origin/master` `7c67804` (2026-09-12).
- Fuentes: `sdd/PROJECT.md`, `sdd/PATTERNS.md`, los tres artefactos de este feature, rutas, Owners, módulos Koin, use cases y tests actuales.
- Redacción: sin datos runtime, credenciales, hosts privados ni contenido personal.

## Hallazgos y resolución aplicada

| ID | Severidad | Riesgo | Resolución versionada |
|---|---|---|---|
| P1-01 | P1 | Baselines/veredictos contradictorios | Meta, tareas y este review declaran una única baseline y decisión. |
| P1-02 | P1 | Contrato visual ambiguo | Functional spec enumera cada campo y usa sólo `NotFound` para ausencia puntual. |
| P1-03 | P1 | Usuario sin retorno visible | Back localizado y accesible requerido en cada estado. |
| P1-04 | P1 | Koin/ruta incompletos | Technical spec define ambos parámetros y el registro/Owner. |
| P1-05 | P1 | Doble navegación/no collector | Efecto rendezvous, guardia, reset y pruebas obligatorias. |
| P1-06 | P1 | Sesión no testeable desde use cases | Se preserva SessionEventBus; evidencia de sesión va a #5. |
| P1-07 | P1 | Fallback no determinista/copy incompleto | Contrato de loader testeable, semántica y localización exhaustiva. |

## Decisión

**REVISAR.** Los hallazgos fueron aplicados a los specs y requieren re-revisión independiente antes de habilitar build. No hay hallazgos P0 ni cambio de API/backend propuesto.
## Re-revisión final: hallazgos adicionales resueltos

| ID | Severidad | Resolución |
|---|---|---|
| P1-08 | P1 | Se define preservación: sin refresh al volver, fecha/filtro en ViewModel y `LazyListState` salvable; pruebas de retorno obligatorias. |
| P1-09 | P1 | Se añade `connectedDebugAndroidTest` local y en PR con emulador como gate de semántica/navegación/fallback. |

## Decisión actual

**PENDIENTE DE CONFIRMACIÓN FINAL.** No se habilita Kotlin hasta que ambas revisiones independientes confirmen estos dos cambios.

## Decisión final

**LISTO PARA BUILD.** Dos revisiones independientes aprobaron los artefactos luego de aplicar P1-01…P1-09; no quedan hallazgos P0/P1/P2.
