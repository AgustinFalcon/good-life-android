# Snapshot externo de advisories lint

- Feature id: `20260917-goodlife-external-advisory-snapshot`.
- Estado: `done`.
- Issue: #52.
- Rama: `chore/android-refresh-lint-advisories`.

## Decisión

El snapshot de advisories externos se refresca desde el XML que produjo CI para el mismo SHA, sin mezclarlo con resultados locales. El modo de refresh preserva por igualdad profunda `variants`, `exceptions` y `generatedFrom`; aborta antes de escribir ante cualquier drift determinista. La evidencia declara run, SHA y digest/identidad admisible del artefacto, sin rutas privadas ni datos sensibles.