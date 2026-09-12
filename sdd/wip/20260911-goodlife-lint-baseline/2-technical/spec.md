# Especificación técnica — política de lint

## Fuente de verdad

`:app:lintDebug` genera el XML de lint. Se añade una verificación versionada que resume reglas/cantidades y compara el resultado con un inventario explícito de deuda. La comparación usa identificador de regla y una ubicación relativa normalizada, nunca rutas absolutas de una máquina.

La verificación falla si aparece un error, si aumenta la cantidad de una regla sin actualización revisada del inventario, o si una excepción vencida sigue presente. No reemplaza el XML de lint ni usa `lint-baseline.xml` como mecanismo de ocultamiento global.

## Inventario inicial

El inventario inicial se genera desde el SHA de la primera PR de implementación en CI Linux; registra comando, versión del wrapper, AGP y SDK/JDK relevantes, conteos y fingerprints. Las 60 advertencias observadas son sólo triage y no se congelan hasta esa medición revisada. El inventario clasifica cada una como:

- **fix now:** cambio pequeño y verificable;
- **split upgrade:** requiere PR aislada y pruebas de compatibilidad;
- **investigate:** origen no confirmado, por ejemplo compatibilidad/generación de recursos;
- **temporary exception:** sólo con owner y condición de retiro.

El detalle de cada archivo se genera desde lint durante implementación; el spec no congela rutas locales ni supone que todos los `UnusedResources` son eliminables.

## Integración CI

El workflow ejecuta lint y la verificación de baseline después de compilar. El estado de cobertura lógica sigue independiente. La salida de CI publica sólo conteos y rutas relativas de código; no registra secretos ni artefactos personales.

## Plan de cambios

1. Crear inventario/validador y test del validador.
2. Resolver manifest/backup en una PR pequeña con revisión Android y privacidad.
3. Resolver launcher/iconos en otra PR pequeña con validación visual.
4. Resolver firmas Compose y dependencias TOML en PRs separados por familia.
5. Reducir recursos sólo tras identificar su ownership.
6. Planificar upgrades de AGP/SDK/dependencias con matriz de compatibilidad y gates completos.
7. Reducir el baseline hasta que warnings puedan convertirse en gate bloqueante.

## Riesgos

Los upgrades de dependency/AGP pueden cambiar APIs y toolchain; no se aprueban sólo porque lint los sugiera. Los recursos de compatibilidad pueden provenir de bibliotecas o packaging. Los cambios de backup requieren revisión de seguridad porque la app maneja sesión local.

## Addendum aprobado — CI y baseline determinista

El workflow Android ejecuta sobre todo `pull_request` sin filtro de rama base (para PRs apiladas) y conserva `push` a `master`. Cada check se asocia al SHA de la PR; si hay rebase o dispatch manual, se registra SHA/ref y se reejecuta antes de merge a `master`.

Para PRs ordinarias, CI ejecuta `:app:lintDebug` y luego el verificador. El verificador falla cerrado si el XML esperado no existe/no se lee, si hay errores, si aparece un fingerprint nuevo o si vence una excepción. El candidato interno además ejecuta lint de variante release según R-05.

El fingerprint es `ruleId + ruta repo-relativa con / + mensaje/categoría normalizados UTF-8 NFC`, ordenado de forma determinista; no usa ruta absoluta ni línea/columna salvo regla documentada. CI Linux es autoridad. El inventario define owner, justificación, fecha de creación, vencimiento o condición medible de retiro y issue/PR. Fixtures Windows/Unix equivalentes deben producir el mismo inventario.
