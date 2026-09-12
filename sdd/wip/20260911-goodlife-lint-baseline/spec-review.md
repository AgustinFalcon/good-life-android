# Spec pre-build review — lint baseline

## Review metadata

- Mode: local.
- Target: `20260911-goodlife-lint-baseline`.
- Base: lint evidence recorded on 2026-09-11; implementation baseline remains `origin/master` `3dcedfc` unless re-measured during build.
- Sources reviewed: project SDD contracts; feature metadata, functional/technical specs and task plan; recorded warning taxonomy.
- Redaction: no absolute paths, machine data, secrets or runtime data are included.
- Review status: user-approved proposals applied; awaiting the independent final re-review recorded below.

## Coverage matrix

| Lens | Execution | Result | Consolidated risks |
|---|---|---|---|
| Structure | sequential | pass | complete metadata/spec/task set |
| Design quality | sequential | findings | baseline identity and exception schema |
| Contracts and resilience | sequential | findings | stable warning fingerprint and expiry behavior |
| Baseline alignment | sequential | findings | lint evidence must be remeasured on build baseline |
| Complexity and size | sequential | pass | implementation should be sliced |
| Architect | sequential | findings | validator responsibility boundary |
| Critic | sequential | findings | count-only comparison loophole |
| Pragmatist | sequential | findings | task grouping conflicts with PR-size policy |
| Privacy | sequential | pass | relative paths and no runtime artifacts |
| Pipeline | sequential | findings | CI inputs and deterministic outputs |

All ten lenses ran sequentially because independent reviewer capacity was unavailable.

## Evidence ledger

| ID | Type | Source | Evidence | Confidence |
|---|---|---|---|---|
| E-01 | observed | `meta.md#Evidencia observada` | 60 warnings/0 errors are classified by rule, but the source is described as equivalent baseline work rather than a reproducible committed input. | high |
| E-02 | observed | `2-technical/spec.md#Fuente de verdad` | The verifier compares rule and normalized relative location, while functional acceptance also describes counts by rule. | high |
| E-03 | observed | `1-functional/spec.md#Criterios de aceptación` | Exceptions require owner and removal condition but no machine-readable schema or expiry comparison contract is defined. | high |
| E-04 | observed | `2-technical/spec.md#Plan de cambios` and `3-tasks/tasks.json#LB-02` | The plan calls for isolated manifest/icon and Compose work, but one task groups them together. | high |
| E-05 | inferred | E-01 and E-02 | A count-only gate can replace one warning with another in the same rule class without detecting new debt. | high |

## Complexity and split assessment

| Dimension | Score (0–2) | Evidence |
|---|---:|---|
| Interfaces and integration | 1 | Gradle lint XML and CI verifier |
| Data or state change | 0 | no product data change |
| Failure and concurrency behavior | 1 | expiry and baseline drift handling |
| Cross-domain coordination | 1 | Android, manifest, Compose, Gradle and CI |
| Delivery and rollout risk | 1 | false positive gate can block all PRs |
| **Total** | **4/10** | medium complexity |

Recommendation: keep the feature but implement it in small PRs: inventory/validator first, then one warning family per PR. This matches the stated policy and prevents unrelated code movement.

## Findings

### F-01 — Count-only comparison does not fully prevent new warning debt

- Severity: HIGH.
- Evidence: E-02, E-05.
- Impact: one existing warning can disappear while a different warning of the same rule appears; counts stay unchanged and the policy goal is bypassed.
- Proposed specification change: `2-technical/spec.md#Fuente de verdad` — define a stable warning fingerprint consisting of lint rule ID, normalized repository-relative location and normalized message/category; compare both fingerprint set and aggregate counts. Explicitly document the reviewed migration path for legitimate line/path movement.
- Acceptance evidence: validator tests prove it fails for a new same-rule warning with unchanged count, and passes only after a reviewed inventory migration.
- Proposal state: pending approval.

### F-02 — Exception lifecycle lacks a deterministic schema

- Severity: MEDIUM.
- Evidence: E-03.
- Impact: CI cannot reliably determine owner, expiry or removal condition; temporary exceptions can become permanent invisible debt.
- Proposed specification change: `2-technical/spec.md#Inventario inicial` — define a versioned inventory schema with fingerprint, class, owner role, justification, created date, expiry date or measurable removal condition, and linked issue/PR. Expired entries fail validation unless an explicit reviewed renewal changes the record.
- Acceptance evidence: validator tests cover missing owner, expired exception, renewal, and resolved entry removal.
- Proposal state: pending approval.

### F-03 — Evidence must be remeasured against the implementation baseline

- Severity: MEDIUM.
- Evidence: E-01.
- Impact: 60 warnings is useful triage, but may not match the branch/Gradle environment that will own the validator, producing accidental gate failures or omissions.
- Proposed specification change: `meta.md#Evidencia observada` and `3-tasks/tasks.json#LB-01` — require the first implementation PR to regenerate lint from its declared commit and record tool/Gradle inputs, rule counts and relative fingerprints; it may not assert the 60-count inventory until that measurement is committed and reviewed.
- Acceptance evidence: baseline generation command is reproducible in CI and its inventory matches the generated report on the declared commit.
- Proposal state: pending approval.

### F-04 — Task LB-02 is broader than the stated small-PR policy

- Severity: LOW.
- Evidence: E-04.
- Impact: a combined manifest, icon and Compose change makes visual/security review less focused and rollback harder.
- Proposed specification change: `3-tasks/tasks.json` — split LB-02 into separate manifest/privacy, launcher/icon and Compose/TOML tasks; each names its proportional validation.
- Acceptance evidence: each follow-up PR contains one warning family and its exact lint/visual/manifest checks.
- Proposal state: pending approval.

## Proposal register

| Proposal | Related finding | Proposed change | User decision | Applied evidence |
|---|---|---|---|---|
| P-01 | F-01 | Use fingerprint-set plus count comparison. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`dc769c2`) |
| P-02 | F-02 | Define exception lifecycle schema. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`dc769c2`) |
| P-03 | F-03 | Re-measure from declared implementation baseline. | approved / applied | `meta.md#Evidencia observada` and `LB-01` (current branch) |
| P-04 | F-04 | Split LB-02 by warning family. | approved / applied | `tasks.json#LB-02a..LB-02c` and `2-technical/spec.md#Plan` (current branch) |

## Decision

**REVISAR — re-review final pendiente**

Las propuestas P-01 a P-04 fueron aprobadas por el usuario y aplicadas. El baseline se medirá desde el SHA de implementación en CI Linux y las familias de warning se entregan por PR aislada. El veredicto final queda reservado a la re-revisión independiente posterior a este cambio.

## Residual risks and next action

- Residual risk: upgrades de dependency/AGP/SDK siguen separados y requieren evidencia de compatibilidad.
- Next action: re-ejecutar las lentes afectadas y cambiar a `LISTO PARA BUILD` sólo si no aparece un hallazgo nuevo.
