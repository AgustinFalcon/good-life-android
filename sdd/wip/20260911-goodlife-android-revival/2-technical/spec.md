# Technical specification — Android revival

## Contracts

- API base URL: `https://good-life.ddns.net/`.
- Presentation flow: Owner -> ViewModel -> use case -> repository -> Retrofit/Room.
- Navigation: ViewModels publish events through `ComposeNavigationController`; the navigation host owns `NavController`.
- Tests: unit logic and coverage gate run in CI; instrumented/device smoke is tracked separately.`r`n- `:app:lintDebug` currently reports 60 warnings and no errors; lint-baseline ownership is R-07 / issue #10, not an implicit product blocker.

## Evidence status

`master` records 164 unit tests and 80.38% eligible logic line coverage (1,483/1,845) as verified on 2026-09-11. The newer 172-test / 82.83% result belongs to open PR #4 and becomes baseline only after merge. This distinction prevents a branch-only metric from being reported as shipped.

## Delivery design

- R-01 uses a controlled emulator/device and redacted evidence; it distinguishes API contract, app configuration and local-network failure.
- R-03 and R-04 require a functional/technical delta before code and typed routes before UI exposure.
- R-05 documents signing, distribution, rollback and instrumentation without committing secrets or publishing a release.