---
type: Traps
version: recovery-audit-2026-09.1
validated: 2026-09-10
update_when: "Aparezca una discrepancia o riesgo real."
---

# Traps

- README dice JDK 17; bytecode Kotlin/Java está fijado a JVM 11. Son conceptos distintos.
- El inventario histórico de tests no es una métrica de cobertura: validar siempre el reporte JaCoCo antes de comunicar un resultado.
- `KMP-ready` no significa módulo KMP: settings solo incluye `:app`.
- El gate JaCoCo vigente es `:app:logicDebugUnitTestCoverageVerification`: cubre lógica elegible y exige cobertura de líneas >= 80%. No reemplaza las pruebas instrumentadas.
- El workflow android-ci.yml invoca explícitamente logicDebugUnitTestCoverageVerification; la documentación que dice que solo corre testDebugUnitTest está obsoleta.
- La cobertura no debe inflarse excluyendo ViewModels o casos de uso: las exclusiones se limitan a UI Compose y superficies framework-bound documentadas en Gradle/runbook.
- Preservar cambios locales; no registrar credenciales, artefactos del SDK ni reportes con datos de usuario.
- Una carga cancelable no demuestra que el guard contra respuestas tardías funcione: para Daily/Meals usar una fuente no cancelable y observar emisiones de estado.
- `CancellationException` no es un error de dominio: todo wrapper suspendido debe relanzarla antes de mapear fallos ordinarios. El cleanup local de logout es la única sección permitida en `NonCancellable`, y debe relanzar la cancelación original.
- `EncryptedSharedPreferences` está deprecado en AndroidX. No reemplazarlo por preferencias planas como “fix”; una migración debe diseñar almacenamiento/rotación con Keystore, compatibilidad de sesión y rollback antes de cambiar tokens.
- No permitir login manual y biométrico en paralelo: el repositorio puede persistir tokens antes de que un ViewModel descarte una respuesta tardía.
- `sdd/PROJECT.md` y los artefactos SDD versionados son la fuente canónica que viaja por PR. `docs/agent/` complementa la operación; no sustituye el contrato SDD.

- Un smoke debug no valida el APK/AAB release firmado: para distribución interna se exige evidencia UI-only redacted del artefacto exacto y su registro externo opaco.
- Nunca resolver firma, canal, audiencia, hash, rollback o proveedor por inferencia: hasta una aprobación explícita el estado correcto es BLOCKED, sin secretos ni artefactos en Git.

- Nunca regenere `config/lint-baseline.json` completo para aceptar advisories remotos: use el refresh externo atómico con XML CI, `--report-root` POSIX explícito e issue trazable; rutas remotas no mapeadas se rechazan.
