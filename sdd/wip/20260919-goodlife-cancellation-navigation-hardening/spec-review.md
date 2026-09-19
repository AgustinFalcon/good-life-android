# Pre-build review — 2026-09-19

## Decision: READY FOR BUILD

### Structure

The feature contains meta, functional/technical specifications, independently verifiable tasks and an implementation evidence directory. Scope is one bounded robustness fix, not a product feature.

### Design and resilience

- Structured cancellation is a correctness requirement: it must not become a user-facing error.
- Logout is the sole exception where minimal local cleanup intentionally survives caller cancellation; the cancellation still propagates.
- Channel delivery removes both unmanaged lifetime and replay-after-consumption while preserving the initial Splash handoff; collection is lifecycle-aware at STARTED.

### Contract and security

No Retrofit DTO, endpoint, route, deep link or user-visible copy changes. Diagnostics remain generic and no sensitive values are introduced.

### Baseline alignment

The design preserves the repository Owner/ViewModel/navigation contract, existing tab-local rendezvous effects, `collectAsStateWithLifecycle`, and the current release boundary.

### Complexity

Small-to-medium, split into four reviewable tasks. Token-storage migration is deliberately excluded: the official deprecation of `EncryptedSharedPreferences` requires a separate migration design; replacing it with plaintext preferences would not be a valid hardening.

### Required build evidence

Focused cancellation/navigation tests, the scoped coverage gate, resource/baseline guardrails, diff check, and a review after implementation.
