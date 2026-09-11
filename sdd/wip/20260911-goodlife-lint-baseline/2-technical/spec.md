# Especificación técnica — política de lint

## Fuente de verdad

`:app:lintDebug` genera el XML de lint. Se añade una verificación versionada que resume reglas/cantidades y compara el resultado con un inventario explícito de deuda. La comparación usa identificador de regla y una ubicación relativa normalizada, nunca rutas absolutas de una máquina.

La verificación falla si aparece un error, si aumenta la cantidad de una regla sin actualización revisada del inventario, o si una excepción vencida sigue presente. No reemplaza el XML de lint ni usa `lint-baseline.xml` como mecanismo de ocultamiento global.

## Inventario inicial

El inventario inicial parte de las 60 advertencias observadas y clasifica cada una como:

- **fix now:** cambio pequeño y verificable;
- **split upgrade:** requiere PR aislada y pruebas de compatibilidad;
- **investigate:** origen no confirmado, por ejemplo compatibilidad/generación de recursos;
- **temporary exception:** sólo con owner y condición de retiro.

El detalle de cada archivo se genera desde lint durante implementación; el spec no congela rutas locales ni supone que todos los `UnusedResources` son eliminables.

## Integración CI

El workflow ejecuta lint y la verificación de baseline después de compilar. El estado de cobertura lógica sigue independiente. La salida de CI publica sólo conteos y rutas relativas de código; no registra secretos ni artefactos personales.

## Plan de cambios

1. Crear inventario/validador y test del validador.
2. Resolver manifest/backup e iconos en PR pequeña con revisión Android.
3. Resolver firmas Compose y dependencias TOML en PRs separadas.
4. Reducir recursos sólo tras identificar su ownership.
5. Planificar upgrades de AGP/SDK/dependencias con matriz de compatibilidad y gates completos.
6. Reducir el baseline hasta que warnings puedan convertirse en gate bloqueante.

## Riesgos

Los upgrades de dependency/AGP pueden cambiar APIs y toolchain; no se aprueban sólo porque lint los sugiera. Los recursos de compatibilidad pueden provenir de bibliotecas o packaging. Los cambios de backup requieren revisión de seguridad porque la app maneja sesión local.
