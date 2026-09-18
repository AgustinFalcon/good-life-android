# Especificación técnica — pin oficial upload-artifact v6

Reemplazar el SHA de `actions/upload-artifact` en `.github/workflows/android-ci.yml` y `.github/workflows/lint-advisory-monitor.yml` por `b7c566a772e6b6bfb58ed0dc250532a479d7789f`, referencia oficial del tag `v6.0.0`.

La release oficial exige runner mínimo `2.327.1`; ambos workflows usan `ubuntu-latest` GitHub-hosted, no runners self-hosted, por lo que no se agrega configuración de runner.

Se elige v6.0.0 y no v7: es la migración mínima para runtime Node 24, mantiene los inputs usados y evita introducir cambios independientes de direct-upload/ESM; una actualización mayor futura queda fuera de #53.

No modificar keys `with`, paths, permisos, nombres de artifact, triggers ni jobs. Validar sintaxis YAML, ambos usos exactos y Android CI en PR. El monitor programado se valida por equivalencia estática; no se dispara ni se modifica su schedule. Rollback: revertir el commit si la Action no conserva compatibilidad con el runner GitHub-hosted.