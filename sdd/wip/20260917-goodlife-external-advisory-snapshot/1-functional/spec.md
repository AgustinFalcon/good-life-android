# Especificación funcional — snapshot de advisories externos

## Objetivo

El monitor de lint debe comparar el snapshot de advisories externos contra evidencia que generó CI sin esconder cambios ni modificar la deuda lint determinista.

## Criterios de aceptación

1. Un XML creado por un runner Linux puede importarse localmente solo al declarar su raíz de reporte exacta.
2. Rutas fuera de esa raíz, relativas, UNC, de dispositivo o con traversal siguen rechazándose.
3. El refresh es atómico: primero exige igualdad del inventario determinista y solo entonces actualiza `externalAdvisories`; `variants`, `exceptions` y `generatedFrom` se preservan por igualdad profunda.
4. Cada advisory regenerado referencia #52 y la verificación estricta sobre los mismos XML no detecta drift.
5. La evidencia versionada registra run, SHA y una identidad/digest admisible del artefacto sin secretos, PII, URLs privadas o rutas locales.
6. El procedimiento queda documentado para uso posterior.

## Fuera de alcance

Actualizar dependencias/AGP, relajar el gate determinista, convertir el monitor externo en no bloqueante o modificar producto Android.