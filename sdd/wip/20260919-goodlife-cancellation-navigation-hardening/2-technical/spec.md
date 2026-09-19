# Technical specification — cancellation and one-shot navigation hardening

## Observed baseline

- `executeOptionalApiCall` already rethrows `CancellationException`, while the standard API wrapper does not.
- Some suspend repositories/helpers catch broad `Exception`, which can convert structured cancellation into `Result.Error`.
- The app navigation controller keeps `MutableSharedFlow(replay = 1)` and an unmanaged `CoroutineScope(Dispatchers.IO + NonCancellable)` to bridge Splash timing. That retains consumed events and escapes lifecycle ownership.

## Design decisions

### T1 — preserve structured cancellation

At each affected suspend catch boundary, catch `CancellationException` first and rethrow it. Keep mapping ordinary failures to the existing domain `Result.Error` contract.

For logout, local sensitive-session cleanup remains intentional. If cancellation interrupts remote logout, perform only local cleanup in `NonCancellable`, then rethrow the original cancellation; no result error is fabricated.

### T2 — app navigation is a bounded one-shot stream

Replace replayed `SharedFlow` plus ad-hoc scope with `Channel<NavigationAction>(Channel.BUFFERED)` exposed as `Flow` through `receiveAsFlow()`.

- A buffered action bridges the Splash-before-host race.
- Delivery consumes an action; it is not replayed to a later host collector.
- Synchronous controller methods use `trySend`; a rejected dispatch is fail-fast rather than silently dropped.
- There is one root collector by design, active only through `repeatOnLifecycle(STARTED)`; an action emitted during STOPPED stays buffered until the host returns. Tab-local effects remain unchanged.

### T3 — no raw exception print

Narrow navigation handling to expected invalid-navigation failures and emit only a generic, non-sensitive diagnostic. Unexpected programmer errors are not broadly swallowed.

## Affected boundaries

- `data/remote/api/ApiExt.kt`
- `core/result/Result.kt`
- `data/repository/DailyRepositoryImpl.kt`
- `data/repository/AuthRepositoryImpl.kt`
- `data/repository/MediaRepositoryImpl.kt`
- `presentation/navigation/core/ComposeNavigationController*.kt`
- `presentation/navigation/host/GoodLifeNavHost.kt`
- `fake/FakeNavigationController.kt` and focused unit tests
- `docs/agent/architecture.md`, `docs/agent/traps.md`, `CHANGELOG.md`

## Verification

- Focused unit tests for reusable cancellation wrappers and lifecycle-aware one-shot navigation cases.
- Concrete repository catches stay source-compatible and compile through the full logic gate. They do not gain artificial test seams in this bounded fix; introducing ports solely for direct mocks is a separate architectural change.
- `:app:logicDebugUnitTestCoverageVerification`.
- Existing resource/baseline guardrails.
- `git diff --check` and SDD JSON parse.

## Security and rollout

No token content, remote error body, account data or device data is logged or committed. This is source-compatible for callers except that cancellations now propagate as required by structured concurrency. Rollback is a small revert of this branch only.
