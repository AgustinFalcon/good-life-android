# Spec pre-build review — #39

- Fecha: 2026-09-17.
- Alcance: `20260917-goodlife-toolchain-upgrades`.
- Resultado final: **LISTO PARA EJECUCIÓN PLANIFICADA**.

## Hallazgos que bloquearon la primera revisión

1. El plan agrupaba AndroidX de forma demasiado amplia y no inventariaba cada advisory real.
2. Lifecycle y Room requerían invariantes de actualización conjunta.
3. Faltaban bytecode Java/Kotlin 11, comandos de gates y semántica exacta del monitor de advisories.

## Resolución verificada

- Ledger exhaustivo de 12 advisories únicos, deduplicados entre debug y release.
- Subfamilias T1a–T1g atómicas; Lifecycle runtime/viewmodel y Room runtime/KTX/compiler quedan acoplados explícitamente.
- Matriz separa JDK 17 de bytecode 11.
- Gates ejecutables y la actualización del snapshot desde XML quedan definidos; el monitor de `master` es confirmación post-merge.
- No se autorizan actualizaciones masivas ni cambios por el mero advisory.

## Condición para los PRs de ejecución

Cada issue hijo debe declarar las instrumentadas exigidas por su superficie; si no se pueden ejecutar, no se mergea esa subfamilia como validada.