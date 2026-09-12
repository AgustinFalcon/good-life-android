# Spec pre-build review — internal release readiness

## Review metadata

- Mode: local.
- Target: `20260911-goodlife-internal-release-readiness`.
- Base: `origin/master` `3dcedfc`, as declared in feature metadata.
- Sources reviewed: project SDD contracts; feature metadata, functional/technical specs and task plan; R-01 smoke specification as release dependency.
- Redaction: no signing material, tester identity, distribution location or private artifact metadata is included.
- Review status: independent final re-review approved; no open finding in scope.

## Coverage matrix

| Lens | Execution | Result | Consolidated risks |
|---|---|---|---|
| Structure | sequential | pass | complete metadata/spec/task set |
| Design quality | sequential | findings | release candidate identity |
| Contracts and resilience | sequential | findings | smoke candidate and rollback compatibility |
| Baseline alignment | sequential | findings | R-01 semantics |
| Complexity and size | sequential | pass | no split required |
| Architect | sequential | findings | external signing/provenance boundary |
| Critic | sequential | findings | debug-vs-release evidence gap |
| Pragmatist | sequential | pass | checklist before automation is appropriate |
| Privacy | sequential | pass | externalized sensitive material |
| Pipeline | sequential | findings | reproducible candidate gate |

All ten lenses ran sequentially because independent reviewer capacity was unavailable.

## Evidence ledger

| ID | Type | Source | Evidence | Confidence |
|---|---|---|---|---|
| E-01 | observed | `1-functional/spec.md#Recorrido de entrega interna` | A signed release artifact is built before distribution and smoke execution. | high |
| E-02 | observed | `2-technical/spec.md#Validación` | The future PR must prove install/launch for the release build before distribution. | high |
| E-03 | observed | R-01 `1-functional/spec.md#Precondiciones` | The authenticated smoke currently starts from a debug build. | high |
| E-04 | observed | `2-technical/spec.md#Distribución y rollback` | Rollback identifies a previous version but does not require compatibility assessment or a channel-specific verification result. | high |
| E-05 | inferred | E-01 through E-03 | A successful debug smoke cannot establish behavior of the exact signed artifact being distributed. | high |

## Complexity and split assessment

| Dimension | Score (0–2) | Evidence |
|---|---:|---|
| Interfaces and integration | 2 | external signing, distribution and internal audience |
| Data or state change | 0 | no product schema/data change |
| Failure and concurrency behavior | 1 | candidate withdrawal and prior-version restoration |
| Cross-domain coordination | 2 | source control, operator, signing and distribution systems |
| Delivery and rollout risk | 2 | signed executable reaches authorized testers |
| **Total** | **7/10** | large operational scope |

Recommendation: retain a single readiness spec but deliver its future work in two reviewable PRs: first the redacted release-candidate/runbook contract, then any provider-specific automation after a separate approval. Do not split the product behavior itself.

## Findings

### F-01 — R-01 debug smoke cannot be the final release-candidate smoke

- Severity: HIGH.
- Evidence: E-01, E-02, E-03, E-05.
- Impact: the signed release artifact may differ in signing, shrinker, configuration or install behavior from debug while still passing the existing smoke.
- Proposed specification change: `1-functional/spec.md#Recorrido de entrega interna`, `2-technical/spec.md#Gates previos`, and the R-01 relationship — require a redacted, UI-only release-candidate smoke on the exact signed artifact identified by its external hash. R-01 debug evidence remains a prerequisite, not a substitute. The release-candidate smoke must use the same PASS/BLOCKED/FAIL semantics once R-01 P-01 is decided.
- Acceptance evidence: distribution record references one commit/version/hash and a PASS result tied to that same candidate; blocked or failed result stops distribution.
- Proposal state: pending approval.

### F-02 — Candidate provenance lacks an explicit external source of authority

- Severity: MEDIUM.
- Evidence: E-01, E-02.
- Impact: operators can disagree about which version/hash is “last internal” or which signed artifact corresponds to the selected commit.
- Proposed specification change: `2-technical/spec.md#Versión y artefacto` — name a redacted external release register as the authoritative source for version code, commit, candidate hash, signed-artifact status, distribution state and previous known-good candidate; require one accountable operator role to update it before distribution.
- Acceptance evidence: a reviewer can trace the candidate in that external register without accessing secrets, tester lists or binary storage.
- Proposal state: pending approval.

### F-03 — Rollback needs explicit compatibility and verification criteria

- Severity: MEDIUM.
- Evidence: E-04.
- Impact: reactivating a previous APK can be ineffective or harmful if backend/API compatibility has changed, and an operator could declare rollback complete without confirming it.
- Proposed specification change: `2-technical/spec.md#Distribución y rollback` — before release, record the candidate's declared backward-compatibility requirement; after withdrawal/reactivation, verify channel state and a redacted launch/login observation of the restored candidate. If the previous candidate is incompatible, stop and escalate rather than presenting it as rollback success.
- Acceptance evidence: release register contains `withdrawn`, `restored verified`, or `blocked incompatible` state.
- Proposal state: pending approval.

## Proposal register

| Proposal | Related finding | Proposed change | User decision | Applied evidence |
|---|---|---|---|---|
| P-01 | F-01 | Add exact signed-candidate smoke gate. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`f40ccfd`) |
| P-02 | F-02 | Define external provenance authority and operator role. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`f40ccfd`) |
| P-03 | F-03 | Define rollback compatibility and verification. | approved / applied | `2-technical/spec.md#Addendum aprobado` (`f40ccfd`) |

## Decision

**LISTO PARA BUILD**

Las propuestas P-01 a P-03 fueron aprobadas por el usuario y aplicadas. El candidato release debe vincular su smoke con el identificador opaco del registro externo exacto. La re-revisión independiente final aprobó el cambio sin hallazgos nuevos.

## Residual risks and next action

- Residual risk: firma y distribución siguen externas y requieren un proveedor/runbook aprobado para implementar.
- Next action: implementar en PRs aisladas, conservar los gates definidos y revisar cualquier cambio de contrato.
