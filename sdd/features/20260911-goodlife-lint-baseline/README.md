# GoodLife Android — lint baseline

## Estado

Cerrado como mejora de calidad y trazabilidad. PR #37 integró el inventario schema-v2, el gate de CI y el monitor de advisories; este archivo conserva su alcance y límite.

## Alcance entregado

- inventario exacto por variante de 41 warnings deterministas;
- snapshot de 12 advisories Gradle/AGP por variante; PR #37 observó además un drift externo por variante como warning/delta no bloqueante; el monitor sobre `master` está configurado para bloquear drift normalizado;
- parser XML fail-closed, excepciones acotadas y 16 pruebas de regresión;
- XML y delta JSON de lint conservados como evidencia no ejecutable en CI.

## No entregado

No se borraron recursos masivamente, no se actualizaron dependencias/AGP, no se añadieron supresiones globales y no se habilitó `warningsAsErrors`. Esas remediaciones fueron transferidas, no cerradas: #38 (recursos no usados) y #39 (toolchain/dependencias).

## Trazabilidad

- Issue cerrado: #10.
- Implementación integrada: PR #37, squash commit `474048f`.
- Validación: CI Linux verde e independiente APPROVE.
- Ledger de revival: R-07 actualizado como `done`.
- Backlog transferido: #38 y #39 (sin milestone de release ni implementación incluida en este cierre).
