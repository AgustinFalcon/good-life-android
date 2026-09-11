# Especificación funcional — smoke autenticado

## Objetivo

Obtener evidencia reproducible y redacted de que un usuario de prueba controlado puede iniciar sesión, restaurar sesión, actualizar credenciales, cerrar sesión y leer/actualizar Daily en un emulador o dispositivo.

## Precondiciones

- Compilación debug actual instalada desde una fuente local confiable.
- Emulador o dispositivo con depuración habilitada y conectividad al servicio canónico.
- Cuenta de prueba efímera o dedicada, entregada fuera de Git y eliminable. No reutilizar cuentas personales.
- Un item Daily de prueba cuyo estado original se registre sólo como categoría (`pendiente`, `completado` o `omitido`) y pueda restaurarse.

## Recorrido

1. Abrir la app sin sesión y llegar a Login.
2. Iniciar sesión manualmente con la cuenta controlada y confirmar acceso a Main.
3. Abrir Daily, confirmar que carga datos y cambiar el estado de un único item de prueba.
4. Restaurar el estado original del item antes de terminar.
5. Forzar recreación de proceso/app y comprobar restauración de sesión hacia Main.
6. Provocar una lectura autenticada que requiera refresh de token mediante el mecanismo de prueba aprobado, sin capturar tokens ni headers.
7. Cerrar sesión, reiniciar la app y comprobar que vuelve al flujo sin sesión.

## Evidencia aceptable

Un registro redacted por paso: resultado, timestamp aproximado, tipo de dispositivo/emulador, build/commit, y clasificación del resultado. No se adjuntan capturas con correo, nombre, tokens, URL completa, IP, cuerpo HTTP, datos Daily ni identificadores de hardware.

## Clasificación de fallos

- **Contrato/API:** el servicio responde de forma incompatible a una llamada válida.
- **Configuración app:** URL/build/interceptor/almacenamiento no apuntan o no conservan el flujo esperado.
- **Entorno local:** emulador/dispositivo/red no alcanza el servicio.
- **Producto:** la UI o navegación no satisface el recorrido.

Un fallo de entorno no se presenta como fallo de contrato ni se corrige cambiando producción sin evidencia.

## Criterios de aceptación

- Se ejecutan todos los pasos o se registra un bloqueo con la clasificación anterior.
- El estado Daily alterado se restaura o se elimina la cuenta de prueba.
- No se filtra información sensible en consola, issue, PR, screenshot o artefacto.
- La evidencia queda versionada mediante PR sin afirmar una release pública.

## Fuera de alcance

Automatización con secretos en CI, publicación, pruebas de carga, biometría y validación de cada feature de producto.
