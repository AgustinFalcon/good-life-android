# Especificación funcional — recursos no usados

## Objetivo

Reducir sólo recursos Android demostrablemente muertos, sin alterar copy, navegación, biometría, launcher, notificaciones o compatibilidad. Esta entrega cierra el contrato de inventario y triage; no implementa borrados.

## Reglas de decisión

- Cada familia `type/name` aparece una sola vez en `4-implementation/resource-inventory.md`, con todas sus definiciones, source sets y qualifiers, fingerprints debug/release, multiplicidad, clave semántica, accountable, búsquedas/evidencia, issue/PR, clasificación, rationale y trigger de reevaluación.
- El snapshot inicial de 35 símbolos por variante/70 fingerprints es histórico. Después de una remediación el criterio es comparar `before`, `after` y `remaining`, no exigir que permanezcan 35/70.
- Las únicas clasificaciones son `remove`, `retain_compat`, `retain_dynamic`, `retain_launcher` y `needs_owner`. En Plan A/PR #41 no se permite `remove`; #38 puede abrir remediaciones posteriores por PR separadas. `needs_owner` bloquea hasta tener owner, issue y trigger concreto.
- Sólo una PR posterior con clasificación `remove` puede eliminar una familia completa. Está prohibido borrar una location individual o suprimir lint.
- Strings visibles requieren búsqueda en Compose, XML, manifest, tests, navegación, localizaciones y lookups dinámicos. Ausencia en `rg` no basta ante rutas dinámicas.
- `core_compat.xml` queda `retain_compat`: el comentario de override AAPT2 es una señal, no prueba de procedencia. Launcher queda `retain_launcher` y se evalúa como familia desde todos los roots del manifest y qualifiers.

## Criterios de aceptación de esta spec

- El inventario tiene exactamente 35 filas semánticas y 35 fingerprints por variante en el snapshot inicial; cada fila tiene multiplicidad D×1/R×1 verificable.
- La derivación falla cerrado si schema v2 cambia, el mensaje no coincide con el formato canonicalizado esperado, no se puede extraer `type/name` o cambia el multiset de occurrences.
- El inventario no contiene decisiones de borrado ni cambios de dependencia en Plan A/PR #41.
- UR-05 documenta changelog, docs, Knowledge y el finish/archive posterior; ninguna tarea de eliminación se marca done.

## Fuera de alcance

Upgrades de dependencias/AGP/packaging (#39), cambios de UI/copy, migraciones de recursos, release/distribución, eliminación de recursos y cierre del issue #38.
