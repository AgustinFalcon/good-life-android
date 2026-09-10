---
type: Overview
version: recovery-audit-2026-09
validated: 2026-09-10
update_when: "Cambien propósito, capacidades, actores o módulos."
scope:
  - ../../sdd/PROJECT.md
  - ../../sdd/PATTERNS.md
  - README.md
  - app/build.gradle.kts
  - app/src/main/java/com/agusstkd/goodlife
  - docs
---

# Overview

- **Repo:** GoodLife Android
- **Tipo:** Aplicación Android Kotlin/Jetpack Compose, módulo único `:app`
- **Propósito:** Cliente móvil de GoodLife con Clean Architecture/MVVM, Room, Retrofit, Koin y navegación type-safe.

## Capacidades/alcance

- Auth/login/register/biometría y session check.
- Daily cache SWR y creación de task/habit/routine/meal plan; tabs Daily, Workouts, Meals, Settings y Profile tienen base Owner/Screen/ViewModel. Los detalles Daily/Workout/Meal no son producto final.
- Room local, Retrofit backend y upload de perfil.
- Navegación AppRoute/TabRoute y UI Compose multilenguaje.

## Fuentes

- `../../sdd/PROJECT.md`
- `../../sdd/PATTERNS.md`
- `README.md`
- `app/build.gradle.kts`
- `app/src/main/java/com/agusstkd/goodlife`
- `docs`

