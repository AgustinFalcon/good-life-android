# Pre-build review — 2026-09-18

## Resultado: LISTO PARA BUILD

- **Contrato:** el endpoint es opcional; se limita el nullable a la frontera de rutina activa.
- **Resiliencia:** `data == null` en 2xx no se generaliza a todos los endpoints y los fallos reales siguen diferenciados.
- **Complejidad:** cambio pequeño, sin migraciones ni efectos de escritura.
- **Riesgo principal:** no tratar un `null` como rutina vacía con identificador inventado; se propaga explícitamente hasta el use case.

Cambios requeridos antes de cierre: pruebas de contrato nulo y de preservación de NetworkError.
