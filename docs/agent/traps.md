---
type: Traps
version: a841b13
validated: 2026-07-13
update_when: "Aparezca una discrepancia o riesgo real."
---

# Traps

- README dice JDK 17; bytecode Kotlin/Java está fijado a JVM 11. Son conceptos distintos.
- SSD decía 12 tests y README también afirma 60+; contar suite real antes de comunicar cobertura.
- `KMP-ready` no significa módulo KMP: settings solo incluye `:app`.
- No hay CI ni gate de cobertura aunque SSD fija 80%.
- Worktree actual modifica DTOs/APIs/componentes; preservarlo.

