# GoodLife Android Falcon Monitoring Readiness

Status: no-code readiness dossier.

## Evidence

- Repository is Git-backed and branchable from `master`: GoodLife Android.
- Android app uses Kotlin 2.1.0, Jetpack Compose, coroutines, kotlinx serialization, Room, Retrofit, and Koin.
- Application id and namespace are `com.agusstkd.goodlife`.
- The local Monitoring repository currently exposes JVM/Spring SDK modules: `sdk-core`, `sdk-jvm`, and `sdk-spring`.
- No `falcon-monitoring-kmp` or Android SDK module exists in the inspected Monitoring tree.
- This Android repo has no existing crash/analytics integration point that clearly supports wiring Monitoring without an Android/KMP SDK contract.

## Decision

Do not add runtime Monitoring code or dependencies to the Android app in this tranche. A fake JVM/Spring dependency would be invalid for Android and would not satisfy the Monitoring contract.

## Readiness Requirements Before Code Adoption

- Monitoring publishes a supported Android or KMP SDK artifact with documented Gradle coordinates.
- The SDK supports Android lifecycle-safe initialization, offline spool/retry, crash capture, and secret-safe configuration.
- GoodLife Android receives non-secret build config fields only; Falcon keys and ingestion endpoints must come from the approved runtime/deployment mechanism.
- The integration branch proves `./gradlew.bat test` and the relevant debug assemble task pass without requiring real Falcon credentials.

## Proposed Future Integration Points

- Initialize the supported SDK from the application startup path only after a contract exists.
- Configure release identity as `goodlife-android` plus version/build metadata.
- Keep Monitoring disabled by default for local developer builds unless explicit non-production config is supplied.

## Non-Goals

- No AssistTime changes.
- No committed Falcon keys, ingestion URLs, tenant IDs, project IDs, or environment IDs.
- No invented Monitoring client, fake crash handler, or JVM/Spring starter usage inside Android.
