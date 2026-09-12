# Especificación funcional — readiness de release interno

## Objetivo

Definir una entrega interna reproducible y reversible para validar GoodLife Android con testers autorizados, sin publicar una release pública ni introducir secretos en el repositorio.

## Alcance

La readiness produce una checklist operativa aprobable, no una distribución automática. Antes de cualquier entrega, debe existir:

- build release trazable a commit y versión;
- firma administrada fuera de Git;
- gate de calidad y smoke autenticado R-01 con evidencia redacted;
- canal interno autorizado con audiencia limitada;
- plan de rollback y responsable operativo;
- registro mínimo de resultado sin datos personales.

## Recorrido de entrega interna

1. Seleccionar un commit revisado y una versión incremental.
2. Ejecutar gates de calidad y verificar que no hay secretos/archivos locales en el artefacto de entrega.
3. Construir el artefacto release firmado mediante el mecanismo externo aprobado.
4. Asociar hash del artefacto, commit, versión y changelog a una entrada de distribución interna.
5. Distribuir únicamente a la audiencia autorizada.
6. Ejecutar el smoke aprobado y registrar evidencia redacted.
7. Si hay un incidente bloqueante, detener la distribución y restaurar la versión interna anterior conocida.

## Criterios de aceptación

- Ningún keystore, contraseña, certificado, token o lista de testers entra en Git, PR, logs o screenshots.
- Una persona operadora puede identificar commit, versión, artefacto y rollback sin ambigüedad.
- R-01 informa explícitamente su estado: aprobado, bloqueado por entorno o fallido; no se lo infiere.
- El mecanismo de distribución deja claro si reemplaza, suspende o segmenta una versión anterior.
- No se declara apto para público, tienda, pagos ni cumplimiento regulatorio.

## Fuera de alcance

Publicación pública, automatización de firma con secretos, crash analytics, feature flags, beta externa, soporte de usuarios y cumplimiento legal de store.
