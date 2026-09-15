# Especificación funcional — política de lint

## Objetivo

Convertir la deuda lint existente en un inventario versionado, medido desde el SHA de implementación y reducible sin que un PR pueda introducir, ocultar o reintroducir advertencias sin revisión.

## Política

- `lintDebug` y `lintRelease` sin errores siguen siendo obligatorios.
- Cada warning determinista aceptado se representa por un fingerprint exacto y variante, con clasificación y dueño; no existe una baseline opaca ni supresión global.
- El inventario determinista se compara como **multiset exacto**: un warning nuevo, una multiplicidad mayor o menor, o una entrada inventariada que ya no aparece falla hasta que el mismo PR actualice de forma revisada el inventario.
- `GradleDependency` y `AndroidGradlePluginVersion` son advisories externos: se ejecutan, se versionan por separado y son visibles en PR, pero no bloquean por cambios del feed remoto. Un monitor programado sobre `master` sí falla ante drift y deja evidencia para crear su issue/PR aislado.
- Un warning nuevo sólo puede llegar al inventario mediante una excepción temporal exacta, con owner, justificación, issue/PR y vencimiento o condición de retiro medible. No se aceptan wildcards ni excepciones por regla completa.
- Los PRs no mezclan upgrades grandes, borrados masivos de recursos ni cambios de manifest no relacionados.

## Variantes y bootstrap

El gate cubre explícitamente `debug` y `release`. El primer inventario se genera para ambas variantes desde una `sourceRevision` no autorreferencial (el commit base que no incluye inventario ni scripts). CI Linux produce ambos XML en el HEAD de la PR y exige igualdad exacta contra ese inventario antes de aceptar el bootstrap; el SHA evaluado queda sólo en la evidencia efímera de CI. La cifra histórica no es contrato: el conteo vigente se obtiene sólo del XML y del inventario revisado.

## Priorización

1. Seguridad/compatibilidad: `DataExtractionRules`, target/SDK y recursos de plataforma.
2. Correctitud UI: `ModifierParameter`, `IconLocation` y catálogo TOML.
3. Higiene de recursos: `UnusedResources` sólo con ownership confirmado.
4. Toolchain/dependencias: `GradleDependency` y AGP en PRs aisladas con matriz de compatibilidad.

## Criterios de aceptación

- Los XML de debug y release producen inventarios deterministas y snapshots de advisories externos, relativos, seguros y reproducibles desde el mismo SHA.
- El verificador falla cerrado por XML ausente o ilegible, error lint, rutas absolutas, regla determinista nueva, entrada stale/resuelta, cardinalidad distinta o excepción vencida/malformada; el drift externo emite anotación, summary y artefacto en PR, y falla exclusivamente en el monitor programado.
- La CI no admite una reducción de warnings sin retirar su entrada en la misma PR, ni una reintroducción posterior sin una nueva excepción exacta.
- Cada reducción mantiene lint y la validación proporcional a UI, manifest o build.

## Fuera de alcance

Actualizar todas las dependencias de una vez, usar `lint-baseline.xml`, `disable`, `warningsAsErrors` global mientras exista deuda, borrar recursos generados sin trazabilidad o bloquear features por deuda no relacionada.
