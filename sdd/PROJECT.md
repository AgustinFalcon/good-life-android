# GoodLife Android — SDD project contract

## vision

summary: GoodLife es la app Android para registrar y completar el día: entrenos, comidas y hábitos, con progreso personal (XP, rachas, PRs).
target_users: Personas que entrenan y comen con un plan, en español rioplatense, sin red social.
value_proposition: Un solo lugar para ver qué entrenar, qué comer y qué hábitos completar hoy, con diseño Vitality Flow.
principles:
  - Diseño Stitch Vitality Flow (menta, voseo, sin campana).
  - Tipos cerrados (MealType, DailyItemStatus, DifficultyLevel, GoalType, niveles XP) — nada de strings mágicos.
  - Reusar pantallas Stitch ya existentes (Diario, agua, wizards) — no rediseñar de cero.
anti_goals:
  - Ranking social entre usuarios.
  - Campana / notificaciones hasta que exista librería de backend.
  - Inventar metas (kcal, semanas, minutos) que el API no envía.

## Ownership and precedence

This directory is the versioned source of truth for Android revival work in this repository. Product intent lives in Repo-minology feature 008; implementation truth is the checked-in code, Gradle configuration, tests, and reviewed pull-request evidence. The Desktop `../../sdd` directory is historical input only and must not drive implementation without reconfirmation.

Never commit credentials, JWTs, private addresses, personal data, device identifiers, or test-account details.

## Verified Android baseline

- Kotlin Android app with Jetpack Compose/Material 3, Clean Architecture and MVVM.
- One `:app` module; `minSdk` 26, `compileSdk`/`targetSdk` 35, JVM target 11.
- Koin, Retrofit/OkHttp, Room, kotlinx-datetime and type-safe Navigation Compose are configured in `app/build.gradle.kts`.
- Canonical API base URL: `https://good-life.ddns.net/`.
- The coverage gate is `:app:logicDebugUnitTestCoverageVerification`, with eligible logic line coverage >= 80%.

## Delivery rules

- Work only from `feature/`, `fix/`, `chore/`, `hotfix/`, or `release/X.Y.Z` branches.
- Every change has a GitHub issue, reviewable PR, tests proportionate to risk, and a changelog/docs update when behavior or contracts change.
- The master baseline verified on 2026-09-11 is 164 unit tests and 80.38% eligible logic coverage (1,483/1,845 lines). PR #4 contains newer review evidence (172 / 82.83%) but is not master until merged.