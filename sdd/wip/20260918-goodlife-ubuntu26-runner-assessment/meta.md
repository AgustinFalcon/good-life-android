# GoodLife Android — evaluación de runner Ubuntu 26

- Feature id: `20260918-goodlife-ubuntu26-runner-assessment`.
- Estado: `in_progress`.
- Issue: #56.
- Rama: `chore/ubuntu-26-runner-assessment`.
- Alcance: validar de forma anticipada los tres jobs GitHub-hosted que hoy usan `ubuntu-latest`; no cambia producto, Gradle, dependencias, permisos ni distribución.

## Decisión a validar

GitHub anunció que `ubuntu-latest` migrará gradualmente de Ubuntu 24.04 a Ubuntu 26.04 entre 2026-10-19 y 2026-11-19. Se selecciona la etiqueta explícita `ubuntu-26.04` para hacer visible el contrato del runner y verificarlo antes de esa ventana. Si algún workflow falla, se revierte el cambio y el issue documenta un pin temporal a una imagen soportada sólo después de evidencia.