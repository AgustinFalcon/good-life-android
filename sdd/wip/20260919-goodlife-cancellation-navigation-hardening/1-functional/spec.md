# Functional specification — cancellation and navigation hardening

## User-visible behavior

1. Leaving a loading screen, cancelling a request or replacing it with a newer request must not surface a connection/error state solely because its coroutine was cancelled.
2. A global navigation instruction emitted before the root host begins collection (for example, Splash handoff) must arrive once.
3. A navigation instruction already handled must not be replayed merely because the host is recreated.
4. Existing typed routes, back-stack options and tab-local navigation behavior remain unchanged.
5. A rejected malformed navigation action is handled without printing an exception stack trace or sensitive request/session content.

## Acceptance criteria

- A cancellation thrown by `executeApiCall`, `suspendResultOf`, media upload, Daily cache write, or logout propagation reaches the caller as `CancellationException`.
- Local logout cleanup remains best-effort/non-cancellable only when a caller has explicitly started logout; cancellation is rethrown afterwards.
- Navigation emitted before collection is observed exactly once; a second collector observes no consumed event.
- Navigation delivery has no process-lifetime `CoroutineScope` and no `NonCancellable` dispatch scope.
- Regression tests cover cancellation propagation and one-shot navigation semantics.

## Out of scope

Storage encryption migration, server errors/copy, route definitions, deep links, backend changes, test accounts and release distribution.
