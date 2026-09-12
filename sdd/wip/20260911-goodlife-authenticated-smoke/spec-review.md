# Spec pre-build review — smoke autenticado

## Review metadata

- Mode: local.
- Target: `20260911-goodlife-authenticated-smoke`.
- Base: `origin/master` `3dcedfc`, as declared in feature metadata.
- Sources reviewed: project SDD contracts; feature metadata, functional/technical specs and task plan; the documented authenticated-session and Daily boundaries.
- Redaction: no account, credential, token, address, device identifier, payload or test-record detail is included.
- Review status: independent final re-review approved; no open finding in scope.

## Coverage matrix

| Lens | Execution | Result | Consolidated risks |
|---|---|---|---|
| Structure | sequential | pass | complete metadata/spec/task set |
| Design quality | sequential | findings | controllable manual operation |
| Contracts and resilience | sequential | findings | mutation recovery and outcome semantics |
| Baseline alignment | sequential | pass | aligned to existing session/Daily responsibility |
| Complexity and size | sequential | pass | no split required |
| Architect | sequential | pass | no production test hook proposed |
| Critic | sequential | findings | blocked refresh ambiguity |
| Pragmatist | sequential | pass | manual smoke is the smallest safe first step |
| Auth | sequential | findings | refresh/logout assertions |
| Privacy | sequential | pass | evidence is redacted by design |

All ten lenses ran sequentially because independent reviewer capacity was unavailable.

## Evidence ledger

| ID | Type | Source | Evidence | Confidence |
|---|---|---|---|---|
| E-01 | observed | `1-functional/spec.md#Recorrido` | The flow includes login, reversible Daily mutation, session restore, refresh observation and logout. | high |
| E-02 | observed | `1-functional/spec.md#Criterios de aceptación` | A blocked step may be recorded, but no rule distinguishes a blocked mandatory refresh from a successful smoke. | high |
| E-03 | observed | `2-technical/spec.md#Límites y rollback` | Recovery after an interrupted mutation starts by reviewing/restoring the test item, but the stop/escalation condition is unspecified. | high |
| E-04 | observed | `2-technical/spec.md#Estrategia` | Refresh may be marked blocked when no safe deterministic mechanism exists; production manipulation is forbidden. | high |
| E-05 | inferred | feature relationship to R-05 | Internal release readiness consumes R-01 status, so its pass/block/fail meaning must be unambiguous. | high |

## Complexity and split assessment

| Dimension | Score (0–2) | Evidence |
|---|---:|---|
| Interfaces and integration | 1 | app, backend availability and device are external dependencies |
| Data or state change | 1 | one reversible Daily state update |
| Failure and concurrency behavior | 1 | interrupted mutation and refresh/session behavior |
| Cross-domain coordination | 1 | app, test operator and release readiness |
| Delivery and rollout risk | 0 | no production rollout or automated distribution |
| **Total** | **4/10** | medium operational complexity |

Recommendation: retain one manual smoke feature. The dependencies are coupled and splitting it would make the evidence less useful.

## Findings

### F-01 — Mandatory smoke outcome is ambiguous when refresh is blocked

- Severity: HIGH.
- Evidence: E-02, E-04, E-05.
- Impact: a release gate could treat an untested refresh path as acceptable merely because the blockage was documented.
- Proposed specification change: `1-functional/spec.md#Criterios de aceptación` and `2-technical/spec.md#Estrategia` — define `PASS` only when every mandatory step completes; `BLOCKED` means the smoke is incomplete and cannot satisfy a release gate; `FAIL` means observed product/contract/configuration failure. A blocked refresh must include the owner of the separate decision and does not permit a workaround that inspects or manipulates secrets.
- Acceptance evidence: the redacted record template captures one of the three outcomes for every mandatory step, and R-05 treats `BLOCKED` as not ready.
- Proposal state: pending approval.

### F-02 — Interrupted Daily mutation needs an explicit safe stop condition

- Severity: MEDIUM.
- Evidence: E-01, E-03.
- Impact: an operator could repeat the flow while the remote state remains uncertain, risking an untracked change to test data.
- Proposed specification change: `2-technical/spec.md#Límites y rollback` — require a fresh UI read before any retry; if the original category cannot be restored through the approved UI, stop the run, classify it, record no item identifier, and hand the account/data cleanup to the designated test-data owner before another smoke run.
- Acceptance evidence: the evidence template contains `restored`, `cleanup required`, and `run stopped` outcomes without recording personal or item data.
- Proposal state: pending approval.

### F-03 — Logout verification should be UI-observable only

- Severity: MEDIUM.
- Evidence: E-01, E-04.
- Impact: “authenticated read not accessible” could invite direct endpoint or log inspection that conflicts with the redaction rules.
- Proposed specification change: `1-functional/spec.md#Recorrido` and `2-technical/spec.md#Límites y rollback` — define logout proof as restart reaching the unauthenticated flow and the app not rendering cached authenticated Daily content; prohibit direct API probes, header inspection and manual storage inspection for this smoke.
- Acceptance evidence: operator records only the visible restart destination and absence/presence category of authenticated UI.
- Proposal state: pending approval.

## Proposal register

| Proposal | Related finding | Proposed change | User decision | Applied evidence |
|---|---|---|---|---|
| P-01 | F-01 | Make PASS/BLOCKED/FAIL and R-05 gate semantics explicit. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`12c52dd`) |
| P-02 | F-02 | Add safe stop/cleanup behavior for an interrupted mutation. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`12c52dd`) |
| P-03 | F-03 | Restrict logout proof to observable app UI. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`12c52dd`) |

## Decision

**LISTO PARA BUILD**

Las propuestas P-01 a P-03 fueron aprobadas por el usuario y aplicadas. La variante `release-candidate` también exige referencia opaca al registro externo exacto. La re-revisión independiente final aprobó el cambio sin hallazgos nuevos.

## Residual risks and next action

- Residual risk: dispositivo o red pueden bloquear la ejecución sin probar un defecto Android.
- Next action: implementar en PRs aisladas, conservar los gates definidos y revisar cualquier cambio de contrato.
