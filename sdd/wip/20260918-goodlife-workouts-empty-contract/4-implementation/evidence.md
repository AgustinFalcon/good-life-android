# Evidence — implementation and local validation

- The authenticated smoke observed an HTTP/envelope success without a routine payload and a misleading Workouts connection error; no tokens, messages, routine values or credentials were retained.
- The endpoint-only nullable propagation converts `Success(null)` into `GetActiveRoutineResult.NotFound` and the existing UI maps that to `NoRoutine`.
- Local forced regression suite passed on 2026-09-18: `ApiExtTest` (4 cases: absent payload, populated payload, server envelope, cancellation propagation) and `WorkoutsTabViewModelTest` (8 cases including null active routine).
- Existing baseline compilation warnings are unrelated deprecations; no new warning suppression was added.
- Final independent review approved the implementation: nullable scope remains endpoint-only, cancellation is rethrown, and SDD/docs/changelog match the code.
- PR #61 CI [run 35366970563](https://github.com/AgustinFalcon/good-life-android/actions/runs/35366970563) completed both logic/coverage and lint/unsigned-release jobs successfully.
- Post-merge smoke on 2026-09-19: an APK Debug assembled from Android `master` was installed as an update without clearing app data. Authenticated `GET /api/v1/routines/active` returned HTTP 200 and Workouts rendered the localized no-active-routine state, with neither connection-error nor retry UI. No routine was created, activated or mutated.
- No credentials, tokens, routine content, screenshots, device identifiers or personal data are retained.
