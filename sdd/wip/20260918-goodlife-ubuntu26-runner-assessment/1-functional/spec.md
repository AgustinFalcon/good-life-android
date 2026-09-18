# Especificación funcional — evaluación Ubuntu 26

## Objetivo

Mantener los gates Android equivalentes durante la transición anunciada de `ubuntu-latest`, probándolos primero sobre Ubuntu 26 explícito.

## Criterios de aceptación

1. Los dos jobs de Android CI y el job del monitor de advisories usan `ubuntu-26.04` de forma uniforme.
2. No cambian triggers, permisos, acciones fijadas, comandos Gradle, nombres/rutas de artefacto ni condiciones de upload.
3. Android CI del PR termina verde en Ubuntu 26 para cobertura/gobernanza y lint/assembly unsigned.
4. El monitor se despacha manualmente desde la rama de evaluación y termina verde en Ubuntu 26.
5. La evidencia identifica las ejecuciones, rollback y decisión final; no declara firma, release o smoke autenticado.

## Fuera de alcance

Modificar dependencias, AGP, JDK, SDK, calidad de producto, credenciales, distribución o el contrato del smoke #5.