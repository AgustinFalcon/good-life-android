---
type: Release readiness contract
version: 1
validated: 2026-09-14
update_when: "Cambien versión, firma, distribución, gates, rollback o el registro externo."
scope:
  - app/build.gradle.kts
  - .github/workflows/android-ci.yml
  - docs/agent/runbook.md
  - sdd/wip/20260911-goodlife-internal-release-readiness
---

# Contrato de readiness para release interno

## Propósito y límite

Este contrato prepara una entrega interna reproducible. No autoriza publicación pública, distribución automática, selección de proveedor, firma improvisada ni el versionado de secretos. La SDD `20260911-goodlife-internal-release-readiness` conserva la fuente de verdad del requisito; este documento opera su ejecución sin datos sensibles.

## Fuente externa de autoridad

Antes de distribuir, una persona operadora designada mantiene un registro externo autorizado. Git sólo referencia un identificador opaco de ese registro; nunca almacena binarios, ubicación privada, listas de testers, certificados, contraseñas, tokens ni identificadores de dispositivos.

Cada candidato debe contener como mínimo:

| Campo | Regla |
| --- | --- |
| `release_record_id` | Identificador opaco y único del registro externo. |
| `commit_sha` | SHA exacto revisado que produjo el candidato. |
| `version_code` / `version_name` | Mayores/trazables respecto del último candidato interno distribuido. |
| `artifact_sha256` | Hash del artefacto firmado exacto; no la ruta ni el binario. |
| `signing_status` | `verified`, `blocked` o `failed`; el mecanismo real permanece externo. |
| `quality_gates` | Resultado redacted de cobertura, lint release, build release y gobierno. |
| `r01_debug` | Resultado explícito `PASS`, `BLOCKED` o `FAIL`; es precondición, no sustituto. |
| `release_smoke` | Resultado UI-only redacted sobre el artefacto firmado exacto. |
| `distribution_state` | `not_started`, `distributed`, `withdrawn`, `restored_verified` o `blocked_incompatible`. |
| `previous_known_good` | Identificador opaco, commit y hash del candidato recuperable. |
| `compatibility_note` | Compatibilidad declarada con backend/API y riesgo de rollback. |
| `operator_role` | Rol responsable; no identidad personal. |

## Orden obligatorio

1. Seleccionar un SHA aprobado y una versión incremental.
2. Ejecutar y registrar los gates estáticos del mismo SHA: cobertura, gobierno, `lintRelease` y build release.
3. Confirmar que no hay secretos, keystores ni artefactos locales en el cambio.
4. Firmar fuera de Git mediante el mecanismo y responsable aprobados. Si no existe, registrar `blocked` y detenerse.
5. Vincular el hash firmado con el registro externo y ejecutar el smoke UI-only redacted en ese artefacto exacto.
6. Distribuir sólo por el canal interno autorizado si todo figura `PASS`/`verified`.
7. Ante incidente bloqueante, retirar/suspender el candidato y restaurar sólo un candidato compatible; verificar canal y arranque/login de forma redacted.

Un resultado `BLOCKED` o `FAIL` de firma, gates o smoke detiene distribución. Un debug smoke exitoso no valida por sí mismo el artefacto release firmado.

## Contrato de rollback

Antes de distribuir se declara la compatibilidad con backend/API y se identifica un candidato anterior conocido bueno. Si ocurre un incidente de autenticación, sesión, datos, crash bloqueante o incompatibilidad de API:

1. se marca el candidato como `withdrawn` en el registro externo;
2. se evalúa compatibilidad del candidato anterior;
3. si es compatible, se lo reactiva y se verifica canal más arranque/login sin PII;
4. sólo entonces se registra `restored_verified`;
5. si no es compatible, se registra `blocked_incompatible` y se escala. No se declara rollback exitoso.

## Decisiones aún requeridas

Los siguientes puntos quedan deliberadamente bloqueados hasta una aprobación explícita; no deben resolverse por inferencia en código:

- proveedor/custodio de firma y rol operador;
- canal interno, audiencia y retención externa;
- formato/ubicación del registro externo;
- política de minificación y firma para el candidato;
- mecanismo de distribución y retiro.

## Evidencia admisible en PR

Una PR puede aportar el identificador opaco del registro, SHA, versión, hash, estados de gate y resultado redacted. Quedan prohibidos valores de secretos, credenciales, JWT, PII, paths locales, URL privadas, IDs de dispositivos, binarios y capturas con contenido de usuario.