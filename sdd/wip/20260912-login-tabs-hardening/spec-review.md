# Spec pre-build review — Hardening Login y tabs

Fecha: 2026-09-12
Artefactos revisados: `meta.md`, `1-functional/spec.md`, `2-technical/spec.md`, `3-tasks/tasks.json`.

## Análisis mecánico

| Área | Resultado | Evidencia / decisión |
|---|---|---|
| Structure | OK | Feature con metadata, especificación funcional/técnica y tareas trazables. |
| Design quality | OK | Lecturas separadas de mutaciones; el `Mutex` queda limitado a escrituras Daily. |
| Contracts & resilience | OK | Los detalles de servidor no cruzan a `UiState`; Login posee una sola autenticación activa. |
| Baseline alignment | OK | Respeta `sdd/PROJECT.md`, MVVM/Compose y los gates JaCoCo/lint existentes. |
| Complexity/size | Media, sin split | El cambio toca Login, Daily y Meals, pero comparten el mismo riesgo de estado asíncrono y queda cubierto por pruebas focalizadas. Workouts se excluye y permanece en #19. |

## Council adversarial

- **Architect:** aprobado tras cambiar el contrato: el `Mutex` no debe envolver lecturas Daily.
- **Critic:** aprobado tras exigir pruebas de navegación durante una mutación pendiente y de sanitización de mensajes remotos.
- **Pragmatist:** aprobado: no añade infraestructura; reutiliza los componentes y gates actuales.
- **Privacy/Security:** aprobado: los mensajes de backend sensibles quedan fuera de la UI y no se agregan secretos.

## Trazabilidad de correcciones aplicadas

- HT-01: navegación Daily no bloqueada por una actualización pendiente y guard de respuesta tardía.
- HT-02: mensajes de `ServerError` Daily/Meals mapeados a `language.errorTexts.dataLoadError` con pruebas distintivas.
- HT-03: Login ignora acciones mientras carga y sus campos, biometría y CTA de registro se deshabilitan visualmente; CTAs sin flujo no se renderizan.
- HT-04: pruebas focalizadas y gates `testDebugUnitTest`, JaCoCo y lint completados con éxito.

## Decisión

🟢 **LISTO PARA BUILD / PR**. El build ya se validó contra estos criterios; el PR requiere CI verde y dos revisiones independientes antes del merge.
## Remediación tras revisión Sol

Las dos revisiones independientes solicitaron cambios. Se aplicaron antes de continuar:

- Una mutación Daily ya encolada no se descarta al cambiar de fecha; solo se restringe la publicación del resultado a la fecha vigente.
- Daily y Meals conservan y cancelan el job de lectura anterior, manteniendo el request-id como defensa adicional.
- Un `ValidationError` de credenciales biométricas muestra feedback genérico localizado.
- `TextFieldComponent` muestra un estado visual deshabilitado y una preview verificable.
- Los KDoc de resultados Daily aclaran que el mensaje remoto es técnico y debe sanitizarse.

La revalidación final requiere tests, gates y una segunda pasada de ambos reviewers Sol.
- Segunda pasada Kotlin: se detectó y corrigió que una mutación de la fecha anterior no puede marcar como refrescando la fecha visible. La regresión confirma `isRefreshing == false` para la fecha B.