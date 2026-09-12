# Plantilla de evidencia — smoke autenticado

> Artefacto redacted. No incluir cuenta, email, ID de item, URL, token, header, payload, captura no redactada ni identificador de dispositivo.

- Run ID no personal: `<run-id>`
- Commit y variante: `<commit>` / `debug | release-candidate`
- Referencia de candidato externo: `<id-opaco obligatorio si variante = release-candidate>`
- Plataforma genérica: `emulador | dispositivo`
- Resultado general: `PASS | BLOCKED | FAIL`

| Paso | Resultado | Clasificación | Restauración/cleanup | Referencia redacted |
|---|---|---|---|---|
| Login | | | n/a | |
| Daily reversible | | | restored | |
| Restauración de sesión | | | n/a | |
| Refresh | | | n/a | |
| Logout/reinicio | | | n/a | |

Un `BLOCKED` identifica el owner de la decisión externa sin registrar datos operativos. Para `release-candidate`, la referencia opaca enlaza en el registro externo el commit, versión, hash, firma y resultado del smoke; no se reemplaza por una URL, hash ni dato sensible en este artefacto.
