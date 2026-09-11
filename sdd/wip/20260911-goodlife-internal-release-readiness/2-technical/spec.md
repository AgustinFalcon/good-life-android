# Especificación técnica — readiness de release interno

## Versión y artefacto

El build release debe tomar `versionCode` estrictamente mayor al último artefacto interno distribuido y un `versionName` humano trazable. La PR que cambia versión registra motivo y compatibilidad. El tipo de artefacto, hash y ubicación de distribución se mantienen en el sistema externo autorizado, no en Git.

## Firma y secretos

La firma se resuelve mediante un proveedor de secretos aprobado o un flujo local controlado que inyecta material de firma en tiempo de build. Las variables/archivos secretos no son parte de Gradle versionado, documentación, cache ni artefactos de CI. Si falta el mecanismo de firma, la preparación se marca bloqueada; no se crea una firma improvisada ni se publica un APK sin trazabilidad.

## Gates previos

Para un candidato interno se requiere: revisión de PR, tests unitarios, cobertura lógica elegible >= 80%, lint sin errores, validación de gobierno, diff sin secretos y resultado explícito de R-01. Las advertencias de lint sólo se aceptan conforme la política R-07; nunca por silencio.

## Distribución y rollback

La distribución utiliza un canal interno externo y una audiencia administrada fuera del repo. El rollback conserva la referencia de la última versión interna conocida, con commit y hash. Un incidente de autenticación, pérdida de sesión, corrupción de datos, crash bloqueante o incompatibilidad de API detiene distribución; el operador retira/suspende el candidato y reactiva la versión anterior según el canal autorizado.

## Observabilidad y privacidad

No se presupone SDK de analytics o crash reporting. Durante readiness, los resultados provienen de smoke redacted y reportes manuales que no incluyan identidad, credenciales, contenido diario ni identificadores de dispositivo. Cualquier telemetría futura requiere una feature separada con contrato de privacidad, retención y consentimiento.

## Validación

La PR de implementación futura debe probar el build release en un entorno autorizado, adjuntar sólo metadatos redacted y comprobar instalabilidad/arranque antes de distribuir. La documentación se actualiza con resultado, riesgos residuales y rollback real utilizado si aplica.

## Addendum aprobado — candidato exacto y gates

La entrega interna exige dos evidencias distintas: R-01 debug como precondición y un smoke UI-only redacted ejecutado sobre el **artefacto release firmado exacto** antes de distribuir. El registro externo autorizado es la fuente de verdad para commit, versión, hash, estado de firma, distribución, resultado del smoke y último candidato conocido bueno. Un `BLOCKED` o `FAIL` detiene distribución.

El candidato ejecuta `lintRelease` (o la tarea release equivalente validada) y build release desde el mismo SHA antes de firma/distribución; `lintDebug` sólo cubre deuda de PR. El registro conserva tarea, SHA y resultado redacted.

Rollback registra compatibilidad requerida. Tras retirar/reactivar, el operador verifica estado del canal y arranque/login redacted del candidato restaurado; si resulta incompatible, queda `blocked incompatible` y se escala, no se declara rollback exitoso.
