# Spec pre-build review — #52

- Fecha: 2026-09-17.
- Revisor: agente independiente de revisión SDD.
- Primera decisión: **REVISAR**.

## Hallazgo P1 resuelto en la especificación

El generador previo reconstruía toda la baseline. El diseño ahora exige `--refresh-external-from`, comparación determinista previa, preservación profunda de `generatedFrom`/`variants`/`exceptions` y escritura únicamente después del éxito.

## Controles incorporados

- Contrato léxico y fail-closed de `report_root` POSIX remoto.
- Vínculo `#N` obligatorio y validado para refresh externo.
- Evidencia de origen CI y pruebas E2E de ambos CLI, incluidos rechazos y no-escritura.

## Revisión de confirmación

- Segunda decisión: **LISTO PARA BUILD**.
- Obligaciones de implementación: validar el esquema de baseline de entrada con el mismo contrato estricto del verificador, usar temporal más reemplazo atómico y tratar `.`/`..` como segmentos completos.

## Code review

- Revisión independiente final: **APPROVE** tras corregir controles P2/P3 de validación upfront, payload, comandos y evidencia.
