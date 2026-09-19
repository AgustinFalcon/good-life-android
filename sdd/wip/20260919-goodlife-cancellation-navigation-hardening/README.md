# Cancellation and one-shot navigation hardening

## Objective

Make cancellation and app-level navigation deterministic without changing product routes, API contracts, UI copy, deep links or token storage.

## Scope

- Preserve `CancellationException` at affected suspend boundaries.
- Replace the process-lifetime navigation coroutine and replayed shared event with a bounded one-shot transport.
- Prevent raw navigation stack traces from being printed.
- Add focused regression tests and update agent documentation/changelog.

## Explicit non-goals

- No backend, route, deep-link, UI-copy, signing, distribution or token-storage migration.
- No claim of a signed release or authenticated release smoke.
