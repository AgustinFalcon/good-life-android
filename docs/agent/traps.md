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
- No permitir login manual y biométrico en paralelo: el repositorio puede persistir tokens antes de que un ViewModel descarte una respuesta tardía.
- `../../sdd/` es una fuente local no versionada; el contrato que viaja por PR es `docs/agent/`. Migrar el SDD canónico al repositorio antes de tratarlo como evidencia entregable.
