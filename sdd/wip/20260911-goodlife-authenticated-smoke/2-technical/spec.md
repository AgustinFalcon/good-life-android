# Especificación técnica — smoke autenticado

## Estrategia

El smoke es una validación manual controlada, no una suite con credenciales hardcodeadas. El build se valida previamente con los gates nativos de unit tests, cobertura lógica y lint. El operador conserva secretos solamente en la interfaz de Login; no los pasa por argumentos, variables registradas, scripts versionados ni herramientas de captura.

La comprobación de refresh se observa por continuidad de sesión y resultado de la lectura autenticada. No se inspeccionan JWTs, encabezados Authorization, cookies ni logs HTTP. Si no existe una forma segura y determinista de expirar la sesión de prueba, el paso se marca bloqueado y se abre una decisión separada; no se manipula producción.

## Límites y rollback

- Usar una única cuenta de prueba y un único item Daily identificable por el operador, nunca por texto almacenado en evidencia.
- Antes de mutar, registrar la categoría de estado; luego restaurarla con la misma UI.
- Ante caída durante la mutación, el siguiente intento comienza revisando/restaurando ese item antes de repetir el resto.
- Logout se considera correcto sólo si el reinicio no recupera sesión y la lectura autenticada no queda accesible.

## Observabilidad segura

La plantilla de evidencia contiene: commit, variante debug, plataforma genérica, fecha/hora aproximada, paso, resultado y clasificación. Se excluyen nombres, emails, credenciales, IDs, payloads, tokens, endpoints concretos y screenshots no redactados.

## Validación previa y posterior

Previa: gates locales del repositorio y verificación de que el emulador/dispositivo es visible para Android tooling. Posterior: revisar el diff de evidencia, confirmar restauración de datos y adjuntar únicamente el resumen redacted a #5/PR.

## Riesgos

Un dispositivo en una red aislada puede impedir la ejecución sin indicar un defecto Android. La cobertura unitaria no sustituye este smoke. El smoke tampoco valida firma de release, distribución o rollback de una APK interna; esos temas pertenecen a R-05.
