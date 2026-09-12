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

## Addendum aprobado — resultado, refresh y evidencia

Este addendum tiene precedencia para la ejecución. Cada paso obligatorio declara `PASS`, `BLOCKED` o `FAIL`. El smoke sólo es `PASS` si todos los pasos obligatorios pasan; `BLOCKED` deja el smoke incompleto y no satisface un gate de release; `FAIL` identifica un defecto observado. Un refresh sin mecanismo seguro/determinista es `BLOCKED`, no éxito implícito.

La observación de refresh es sólo por UI: (a) éxito = la misma sesión continúa y la lectura se renderiza; (b) expiración terminal = Login visible por el flujo de sesión existente; (c) mecanismo ausente = `BLOCKED`. Un simple error de lectura no prueba expiración terminal. Si se necesita esa cobertura determinista, se abre una feature Auth separada; no se inspeccionan headers, tokens ni storage.

Antes de reintentar una mutación se relee el item mediante UI. Si no se puede restaurar su categoría original, se detiene el run, se registra `cleanup required` sin identificadores y el owner de datos de prueba lo resuelve antes de repetir. Logout se prueba al reiniciar: flujo no autenticado visible y sin contenido Daily autenticado renderizado; no se hacen probes directos de API.
