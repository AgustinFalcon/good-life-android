---
type: Traps
version: 833307e
validated: 2026-07-25
update_when: "Aparezca una discrepancia o riesgo real."
---

# Traps

- README dice JDK 17; bytecode Kotlin/Java está fijado a JVM 11. Son conceptos distintos.
- El inventario histórico de tests no es una métrica de cobertura: validar siempre el reporte JaCoCo antes de comunicar un resultado.
- `KMP-ready` no significa módulo KMP: settings solo incluye `:app`.
- El gate JaCoCo vigente es `:app:logicDebugUnitTestCoverageVerification`: cubre lógica elegible y exige cobertura de líneas >= 80%. No reemplaza las pruebas instrumentadas.
- El workflow `android-ci.yml` se activa en `master` y actualmente ejecuta `:app:testDebugUnitTest`. El gate JaCoCo forma parte de `check` y debe ejecutarse en el gate de integración/release hasta que el workflow lo invoque explícitamente.
- La cobertura no debe inflarse excluyendo ViewModels o casos de uso: las exclusiones se limitan a UI Compose y superficies framework-bound documentadas en Gradle/runbook.
- Preservar cambios locales; no registrar credenciales, artefactos del SDK ni reportes con datos de usuario.
