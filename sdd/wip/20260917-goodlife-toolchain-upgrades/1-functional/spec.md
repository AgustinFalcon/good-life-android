# Especificación funcional — plan de toolchain

## Objetivo

Reducir deuda de versiones de forma trazable, reversible y compatible, sin alterar el comportamiento de GoodLife ni bloquear el trabajo funcional.

## Resultado esperado

1. Cada advisory único del snapshot se clasifica en un ledger exhaustivo como `hold`, `candidate` o `blocked`, con baseline, subfamilia, motivo y trigger de revisión.
2. Ningún PR mezcla una actualización mayor de build toolchain con AndroidX/Compose, producto o una remediación de lint no relacionada.
3. Cada PR de ejecución declara la matriz soportada, gates, evidencia y rollback antes de modificar `libs.versions.toml` o el wrapper.
4. La decisión de mantener una versión es válida cuando la compatibilidad no está demostrada; no es un fallo ni una supresión.

## Fuera de alcance

Actualizar todas las dependencias, cambiar la API, reescribir UI, publicar artefactos o convertir advisories de terceros en un gate que impida `master`.