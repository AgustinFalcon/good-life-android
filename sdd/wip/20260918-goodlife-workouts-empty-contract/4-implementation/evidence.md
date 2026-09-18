# Evidence — implementation and local validation

- The authenticated smoke observed an HTTP/envelope success without a routine payload and a misleading Workouts connection error; no tokens, messages, routine values or credentials were retained.
- The endpoint-only nullable propagation converts `Success(null)` into `GetActiveRoutineResult.NotFound` and the existing UI maps that to `NoRoutine`.
- Local forced regression suite passed on 2026-09-18: `ApiExtTest` (4 cases: absent payload, populated payload, server envelope, cancellation propagation) and `WorkoutsTabViewModelTest` (8 cases including null active routine).
- Existing baseline compilation warnings are unrelated deprecations; no new warning suppression was added.
- Final independent review approved the implementation: nullable scope remains endpoint-only, cancellation is rethrown, and SDD/docs/changelog match the code.
- Remaining release evidence: install a combined post-merge debug candidate and observe the existing Workouts empty state. No routine was created or mutated for this work.
