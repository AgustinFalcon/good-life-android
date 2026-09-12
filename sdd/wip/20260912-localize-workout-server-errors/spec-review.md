# Spec pre-build review — Localizar errores de servidor de Workouts

## Análisis mecánico

| Área | Resultado | Decisión |
|---|---|---|
| Structure | OK | Metadata, spec funcional/técnica y tareas trazables al issue #19. |
| Design quality | OK | Cambio de borde de presentación, sin ampliar contratos ni duplicar textos. |
| Contracts & resilience | OK | `ServerError` se sanitiza; Network/NotFound y el guard de frescura de Detail permanecen intactos. |
| Baseline alignment | OK | Cumple `sdd/PATTERNS.md`: texto desde `AppLanguage`, sin contenido remoto en UI. |
| Complexity/size | Baja | Dos mapeos, KDoc y cuatro regresiones; no requiere split. |

## Council adversarial

- **Architect:** aprobado; la sanitización se mantiene en ViewModels y no contamina dominio/datos.
- **Critic:** exige probar el mensaje técnico distintivo y preservar `NetworkError`.
- **Pragmatist:** aprobado; reusa `dataLoadError` ya localizado y no crea recursos paralelos.
- **Privacy:** aprobado con la condición de no loguear ni mostrar el detalle remoto.

## Decisión

🟢 **LISTO PARA BUILD**. La implementación debe completar LSE-01 a LSE-04 y repetir gates/review antes del PR.
## Evidencia de implementación

- LSE-01/LSE-02: `ServerError` en ambos ViewModels se mapea a `language.errorTexts.dataLoadError`.
- LSE-03: `GetActiveRoutineResult.ServerError` documenta que su mensaje es técnico; las pruebas inyectan detalles distintivos y verifican que no se renderizan. NetworkError conserva `connectionError`.
- Pruebas focalizadas: Workouts 7/7 y Workout Detail 4/4, sin fallos.
- Pendiente: gates completos y revisión de PR.