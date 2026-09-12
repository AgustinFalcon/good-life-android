# Progress — GoodLife Android revival

## 2026-09-11: SDD ownership established

- This repository now owns the Android recovery specification and task ledger.
- Historical Desktop SDD material is not copied as implementation truth because it contains stale topology, hostname and quality claims.
- R-02/R-06 evidence is in open PR #4; do not treat it as merged baseline.

## Open sequence

1. R-01 authenticated smoke (#5) — specification review in `sdd/wip/20260911-goodlife-authenticated-smoke/`; execution remains pending controlled credentials/device.
2. R-03 Workouts vertical (#6) — in progress: typed detail is being implemented and verified.
3. R-04 Daily/Meals details (#7) — specification review in sdd/wip/20260911-goodlife-daily-meals-details/; implementation has not started.
4. R-05 internal-release readiness (#8).
5. R-07 lint baseline triage (#10).

## Exit gate

A release is not implied by this recovery work. Every feature requires its issue, a GitFlow branch, a PR review, relevant tests and redacted device evidence where required.