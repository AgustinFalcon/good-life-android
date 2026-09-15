# Spec pre-build review — Daily y Meal Plan details

- Mode: local, revisión adversarial independiente.
- Target: `20260911-goodlife-daily-meals-details` / issue #7.
- Baseline único revalidado: `origin/master` `341930e` (2026-09-14).
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
| P1-09 | P1 | Se mantienen pruebas `connectedDebugAndroidTest` para semántica/navegación/fallback como validación local/dispositivo; el CI actual no las declara gate. |

## Decisión actual

**PENDIENTE DE CONFIRMACIÓN FINAL.** No se habilita Kotlin hasta que ambas revisiones independientes confirmen estos dos cambios.

## Decisión final

**LISTO PARA BUILD.** La re-revisión posterior al rebase aprobó la alineación CI/instrumentación, baseline e i18n; producción y fuentes de test compilan sin hallazgos accionables.

## Re-revisión SDD de cierre

| ID | Severidad | Resolución y evidencia |
|---|---|---|
| P1-10 | P1 | Meals ahora exige antes del parseo el patrón exacto `YYYY-MM-DD`; la prueba `extended ISO year is invalid and does not query` verifica que `+12026-09-13` produce `InvalidRoute` sin consulta. `MealDetailViewModelTest`: 6/6 OK localmente. |

## Decisión de cierre

**APROBADO PARA ARCHIVO.** P1-10 quedó corregido; CI #34920854561 aprobó cobertura/gobernanza y lint/ensamblado release, y dos revisiones independientes aprobaron el HEAD.