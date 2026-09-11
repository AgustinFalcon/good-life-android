# Spec pre-build review — Daily y Meal Plan details

## Review metadata

- Mode: local.
- Target: `20260911-goodlife-daily-meals-details`.
- Base: `origin/master` at `3dcedfc` for implementation baseline; the active branch also contains the unmerged Workout detail pattern from stacked PR #14, used only as non-baseline comparison.
- Sources reviewed: `sdd/PROJECT.md`, `sdd/PATTERNS.md`, this feature's `meta.md`, functional and technical specs, `3-tasks/tasks.json`, and the repository-relative Android files cited below.
- Redaction: no runtime data, credentials, private endpoints or user data are included.
- Review status: draft; proposals require explicit human approval before changing a spec.

## Coverage matrix

| Lens | Execution | Result | Consolidated risks |
|---|---|---|---|
| Structure | sequential | pass with findings | artifact set and task traceability complete |
| Design quality | sequential | findings | route/Owner boundary |
| Contracts and resilience | sequential | findings | parameter validity, stale work, session result |
| Baseline alignment | sequential | findings | stacked PR merge order |
| Complexity and size | sequential | pass | no feature split required |
| Architect | sequential | findings | explicit navigation handoff |
| Critic | sequential | findings | localizable status and invalid input cases |
| Pragmatist | sequential | pass | tasks already allow thin incremental delivery |
| Auth | sequential | findings | session-expiry ownership |
| Privacy | sequential | findings | remote image loading |

All ten lenses ran sequentially because no independent reviewer capacity was available. No delegated coverage is claimed.

## Evidence ledger

| ID | Type | Source | Evidence | Confidence |
|---|---|---|---|---|
| E-01 | observed | `sdd/wip/20260911-goodlife-daily-meals-details/1-functional/spec.md#Decisión propuesta` | Routes carry a semantic ID plus `dateIso`; the feature is read-only and date-backed. | high |
| E-02 | observed | `sdd/wip/20260911-goodlife-daily-meals-details/2-technical/spec.md#Migración de rutas` | The technical spec names typed routes and states that `TabNavGraph` decodes parameters, but it does not define the Owner/back-navigation handoff. | high |
| E-03 | observed | `app/src/main/java/com/agusstkd/goodlife/presentation/navigation/route/TabNavGraph.kt` | The pending Workout detail pattern decodes `toRoute`, passes primitive arguments to its Owner, and gives `navController::navigateUp` to the Owner. | high |
| E-04 | observed | `app/src/main/java/com/agusstkd/goodlife/presentation/screen/tabs/daily/DailyTabViewModel.kt` and `.../meals/MealsTabViewModel.kt` | Both tab ViewModels keep a mutable selected date; their detail navigation remains a TODO on master. | high |
| E-05 | observed | `app/src/main/java/com/agusstkd/goodlife/domain/usecase/nutrition/GetMealPlansUseCase.kt` | Empty data and backend not-found become the same semantic `NotFound`; server and network are distinct outcomes. | high |
| E-06 | observed | `app/src/main/java/com/agusstkd/goodlife/domain/model/daily/DailyItem.kt` and `.../nutrition/DailyMealPlanSummary.kt` | A Daily item has type and status; a meal summary includes image URL and nutrition totals but not recipe content. | high |
| E-07 | observed | `app/src/main/java/com/agusstkd/goodlife/core/datetime/language/AppLanguage.kt` and `.../presentation/screen/tabs/meals/MealsScreen.kt` | Text is centralized through `AppLanguage`, while the current Meals UI still exposes hardcoded Spanish copy. | high |
| E-08 | observed | `sdd/PROJECT.md`, `sdd/PATTERNS.md`, and feature `meta.md` | The feature depends on PRs #11 and #14 conceptually; only `master` is implementation baseline. | high |
| E-09 | inferred | `DailyApiService.kt`, `MealPlanApiService.kt`, and existing session navigation extensions | Detail reads use the authenticated repository path; the spec needs an explicit rule that auth/session failure is not downgraded to missing content. | medium |

## Complexity and split assessment

| Dimension | Score (0–2) | Evidence |
|---|---:|---|
| Interfaces and integration | 1 | typed routes, Koin parameters and tab graph change |
| Data or state change | 1 | existing date-scoped reads only; no schema/API/migration |
| Failure and concurrency behavior | 1 | invalid parameters, retry freshness and not-found mapping |
| Cross-domain coordination | 1 | Daily, Meals, navigation and language contracts |
| Delivery and rollout risk | 1 | client-only, authenticated smoke after merge |
| **Total** | **5/10** | medium complexity |

Recommendation: keep one feature, delivered in the existing task order: route/text foundation, Daily detail, Meal detail, then cross-cutting verification. A feature split would duplicate the shared navigation/localization work without creating a cleaner user-value boundary.

## Findings

### F-01 — Navigation handoff is underspecified

- Severity: MEDIUM.
- Evidence: E-02, E-03.
- Impact: a build can reintroduce direct `NavController` access into a ViewModel or create a detail that cannot return to the same nested tab stack.
- Proposed specification change: `2-technical/spec.md#Migración de rutas` — state the full handoff: tab ViewModels emit typed detail routes through `ComposeNavigationController`; `TabNavGraph` uses `backStackEntry.toRoute`; each Owner receives only primitive route values plus `onNavigateUp = navController::navigateUp`; the Owner, not the ViewModel, passes this pure callback to its screen.
- Acceptance evidence: navigation unit test verifies ID/date route emission; graph/Owner test verifies decoded route arguments and back action returns to the source tab stack.
- Proposal state: pending approval.

### F-02 — Invalid route contract omits ID constraints

- Severity: MEDIUM.
- Evidence: E-01, E-04.
- Impact: `0` or negative IDs could invoke a valid date read and produce misleading `NotFound`; malformed input behavior would not be deterministic after process recreation.
- Proposed specification change: `1-functional/spec.md#Estados observables` and `2-technical/spec.md#Estados y frescura` — require `dailyItemId > 0` and `mealPlanId > 0`, exact ISO `YYYY-MM-DD` parsing, and a localized invalid-route error with no repository call when either parameter is invalid.
- Acceptance evidence: tests cover zero/negative IDs, malformed dates, no use-case invocation, and retry retaining the original parameters.
- Proposal state: pending approval.

### F-03 — Detail copy contract does not name status and state text ownership

- Severity: MEDIUM.
- Evidence: E-06, E-07.
- Impact: type/status labels, title, back action, empty/not-found, retry and invalid-route text could remain hardcoded or expose enum values in one language.
- Proposed specification change: `2-technical/spec.md#Mapeo` — define the `AppLanguage` contract that owns every new Daily/Meal detail state and action label, including localized Daily item type and status labels; require ES/EN/PT implementations and migrate all Meals copy touched by this vertical through that contract.
- Acceptance evidence: language-contract compilation/exhaustiveness plus UI/ViewModel tests asserting localized text models rather than literal screen strings.
- Proposal state: pending approval.

### F-04 — Auth/session outcome is not explicit for date-backed reads

- Severity: MEDIUM.
- Evidence: E-05, E-09.
- Impact: an authorization/session-expiry outcome could be rendered as `NotFound` or a generic content error, hiding the required global session recovery behavior.
- Proposed specification change: `2-technical/spec.md#Estados y frescura` — state that only an empty collection, date-level not-found, or absent ID yields detail `NotFound`; authorization/session-expiry remains owned by the existing authenticated-session flow and must never be translated to content absence or raw backend text.
- Acceptance evidence: fake/use-case or integration-boundary test covers the session-expiry signal separately from `NotFound` and server/network error mapping.
- Proposal state: pending approval.

### F-05 — Remote image handling needs a safe fallback rule

- Severity: LOW.
- Evidence: E-06, existing `MealsScreen.kt` image rendering.
- Impact: an absent or unacceptable image source can degrade the detail or surface loader errors inconsistent with the privacy/error policy.
- Proposed specification change: `2-technical/spec.md#Mapeo` — state that the image is optional, uses the app's existing approved image-loading path, falls back to the meal icon without logging the URL or rendering remote failure text, and does not expand the contract into recipe media.
- Acceptance evidence: UI tests/previews cover absent image and loader failure fallback without raw URL/error display.
- Proposal state: pending approval.

### F-06 — Stacked PR prerequisite needs an explicit integration checkpoint

- Severity: MEDIUM.
- Evidence: E-03, E-08.
- Impact: implementing against a pending Workout detail branch can create avoidable conflicts in shared routes, graph and language contracts, or misstate `master` behavior.
- Proposed specification change: `meta.md#Relación` and `3-tasks/tasks.json` — add a prerequisite that implementation starts after #11 and #14 are merged or after this branch is rebased onto their merged `master` state; re-run route, coverage, lint and governance gates after that rebase.
- Acceptance evidence: PR base is current `master`; shared route diff contains both no obsolete aliases and the expected Workout detail behavior.
- Proposal state: pending approval.

## Proposal register

| Proposal | Related finding | Proposed change | User decision | Applied evidence |
|---|---|---|---|---|
| P-01 | F-01 | Make graph/Owner/back handoff explicit. | pending approval | — |
| P-02 | F-02 | Validate positive ID and exact date before reads. | pending approval | — |
| P-03 | F-03 | Define complete localized detail text contract. | pending approval | — |
| P-04 | F-04 | Preserve session-expiry ownership apart from content states. | pending approval | — |
| P-05 | F-05 | Specify safe optional image fallback. | pending approval | — |
| P-06 | F-06 | Add stacked-PR rebase/validation checkpoint. | pending approval | — |

## Decision

**REVISAR**

The feature has all minimum SDD artifacts, a bounded client-only scope, clear domain identities and testable core behavior. It has no blocker, but the six material proposals above are pending; therefore it is not ready to authorize a build yet.

## Residual risks and next action

- Residual risk: master has changed independently only after the documented baseline; implementation must re-check it after stacked PRs land.
- Next action: approve, reject or modify P-01 through P-06. No implementation begins from this report alone.

## Addendum — decisiones aprobadas

El usuario aprobó aplicar las propuestas P-01…P-06. P-01 se reemplaza por evidencia posterior de revisión adversarial: `ComposeNavigationController` gobierna el grafo raíz y no puede resolver `TabRoute.*Detail`, que existe dentro del `TabNavGraph` interno. Por lo tanto, la corrección aprobada es un efecto tipado del ViewModel hacia un callback local del Owner, con navegación y back en el `NavHostController` del tab.

Se aplicaron al contrato los seis cambios: límite de navegación local, validación de ID/fecha, contrato completo de idioma, sesión separada de `NotFound`, fallback seguro de imagen y checkpoint de rebase/gates para #11/#14. La decisión se mantiene en **REVISAR** hasta implementar/corregir el patrón de Workout y ejecutar una nueva revisión independiente.
