# android-stitch-tabs-redesign

Feature SDD (2026-11-12) para alinear las tabs Entrenar, Comida y Más al proyecto Stitch [Daily Item Component Variants](https://stitch.google.com/projects/9630221387926338009).

## Estado

- Spec funcional: **aprobada** (`1-functional/spec.md`)
- Spec técnica: **aprobada** (`2-technical/spec.md`)
- Tareas: **aprobadas** (`3-tasks/tasks.json`)
- Guía: `IMPLEMENTATION-GUIDE.md`
- Ramas: `feature/android-entrenar-stitch-tab` (empezar acá) → comida → más

## Artefactos

- `4-implementation/artifacts/STITCH-INVENTORY.md` — 36 carpetas Stitch
- `4-implementation/artifacts/HTML-TO-COMPOSE-REPORT.md` — mapping visual → Compose
- `4-implementation/artifacts/stitch/` — HTML + PNG + `vitality_flow/DESIGN.md`

## Orden de build

1. Entrenar (tab + sesión; freeze finish; mapper `dayOrder`)
2. Comida (filtro Diario; detalle de plan; receta = 2b)
3. Más (idioma + biometría; sin XP)
4. Agua / XP / PRs cuando existan subpantallas Stitch
