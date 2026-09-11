# Functional specification — Android revival

## Objective

Resume Android development against the recovered canonical API with real, reviewable verticals and no exposed placeholder action.

## Confirmed user outcomes

1. A user can authenticate, restore a session, refresh it and log out without exposing credentials.
2. A user can read and update Daily for a selected date with observable loading and error states.
3. A user can complete a Workouts flow whose visible actions lead to real create/detail behavior.
4. A user can use Daily and Meals details only when their routes, loading, not-found and error contracts exist.

## Scope and non-goals

The immediate sequence is authenticated smoke, Workouts, Daily/Meals details, then internal-release readiness. Public deep links, push, payments, iOS/KMP migration and public release are out of scope unless a product decision adds them.

## Acceptance rules

- Internal navigation is type-safe and every visible CTA has observable behavior.
- No flow claims device validation until the redacted authenticated smoke has executed.
- A feature PR updates this task ledger, its GitHub issue and relevant docs.