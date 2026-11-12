# Feature Meta — android-stitch-tabs-redesign

**Feature Name**: android-stitch-tabs-redesign
**Feature ID**: feat-20261112-android-stitch-tabs-redesign
**Feature UUID**: 4b8dd589-a1b3-4ae5-b828-589ebd886033
**date_prefix**: 20261112
**created_at**: 2026-11-12
**project_mode**: brownfield
**execution_mode**: standard
**project_type**: production
**spec_language**: es
**stack_profile**: mobile
**platform**: android
**application**: good-life-android
**Mode**: standard
**ltp_enabled**: false
**vision_prompt_shown**: true

user_profile:
  type: technical
  source: global
  selected_at: 2026-11-12

## Status

| Phase | Status |
|-------|--------|
| 1 — Functional Spec | ✅ Approved (review loop Android+backend+Stitch) |
| 2 — Technical Spec | ✅ Approved (review loop Android+backend+coverage) |
| 3 — Tasks | ✅ Approved (sequential, Agustín implementa) |
| 4 — Implementation | ⏳ Ramas GitFlow creadas; Compose lo implementa Agustín |

## Context

Rediseño de las tabs **Entrenar**, **Comida** y **Más** a partir del proyecto Stitch [Daily Item Component Variants](https://stitch.google.com/projects/9630221387926338009). El canvas ya tenía pantallas de Diario, hidratación, catálogo de comidas y wizards de crear rutina/comida. El 2026-11-12 se generaron 4 pantallas nuevas (cascarones de tab + sesión en curso + hub Más). HTML, PNG y DESIGN.md viven en `4-implementation/artifacts/stitch/`.

Kit: perfil técnico (`C:\Users\agustin\.sdd-kit\user-profile.yaml`). `detect-stack.sh` / `check-frontend-skill.sh` no corren limpio en WSL (BOM/CRLF). Stack conocido: Kotlin, Jetpack Compose, Material 3, Clean MVVM, Koin.

**Branches (GitFlow liviano, base `master`)**:
- `feature/android-entrenar-stitch-tab` — fase 1 (empezar acá)
- `feature/android-comida-stitch-tab` — fase 2 (rebase a master tras merge de fase 1)
- `feature/android-mas-settings-hub` — fase 3 (rebase tras fase 2)

Working tree de `master` sigue sucio (CRLF + PopupResult / `.idea`). Esas ramas **no** incluyen ese ruido.

execution_strategy: sequential

saved_description: Extraer y documentar las pantallas Stitch (alimentos, vasos de agua, tabs Entrenar/Comida/Más) y rediseñar esas superficies Android en Jetpack Compose sin campana.

## Brownfield Context

- Project conventions: `sdd/PROJECT.md`
- Related archived: `sdd/features/20261106-android-finish-workout-session`
- Related wip: `sdd/wip/20260911-goodlife-android-revival` (R-03 workout detail)
- Related wip: `sdd/wip/20260918-goodlife-workouts-empty-contract`
- Related archived: `sdd/features/20260911-goodlife-daily-meals-details`
- Existing screens: `WorkoutsScreen`, `WorkoutDetailScreen`, `MealsScreen`, `MealDetail`, `SettingsScreen`, `ProfileScreen`, Daily tab
- Campana / FAB Otros: fuera de alcance
- Ranking social entre usuarios: no existe en API; XP/niveles/rachas/PRs son personales (`gamification.spec.md`)

## Relationship Check

relationship_check:
  performed_at: 2026-11-12T15:45:00Z
  tier: WEAK
  decision: standalone
  decided_at: 2026-11-12T15:45:00Z
  decided_by: express-auto
  candidates: []
  candidates_weak:
    - android-finish-workout-session (sesión en curso / RPE ya implementada)
    - goodlife-workouts-empty-contract (estado vacío Entrenar)
    - goodlife-daily-meals-details (detalle comida)
  candidates_dropped_by_llm: []
  dismissed_conflicts: []
  acknowledged_conflicts: []

relates_to: []

## Sensitive-data policy

Sin credenciales, tokens ni datos de sesión reales en evidencia. El HTML de Stitch usa copy de ejemplo (Agustín Silva / agus.silva@goodlife.app).
