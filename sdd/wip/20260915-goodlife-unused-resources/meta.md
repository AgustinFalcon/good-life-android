# GoodLife Android — recursos no usados

- Feature id: `20260915-goodlife-unused-resources`
- Estado: `spec_review`
- Issue: #38 (abierto; label `type: chore`; asignación al tablero bloqueada porque el token no tiene `read:project`; no se inventa estado).
- Rama: `chore/android-unused-resources-spec`.
- Base verificada: `origin/master` `f7733cc`.
- PRD: no requerido; remediación técnica acotada, sin comportamiento nuevo.
- Límite: esta spec no elimina recursos, modifica dependencias/AGP ni cambia packaging.

## Decisiones de alcance

- El snapshot inicial es 35 símbolos por variante y 70 fingerprints, pero la baseline posterior es evolutiva: cada remediation PR conserva `before`, `after` y `remaining` y declara un delta exacto.
- `core_compat.xml` queda `retain_compat` en #38. #38 puede inspeccionar AAR/merged resources read-only; sólo #39 puede investigar y cambiar dependency/AGP/packaging.
- Toda la familia launcher queda `retain_launcher` hasta probar roots del manifest, qualifiers y smoke de instalación/upgrade/launcher.
- Las otras filas quedan `needs_owner`; no se marca una tarea de borrado como terminada.
