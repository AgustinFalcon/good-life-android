# GoodLife Android — verified implementation patterns

## Presentation

- `*ScreenOwner` is the only presentation layer allowed to obtain a ViewModel with Koin or manage platform side effects.
- Screens and reusable components are pure Compose functions; they receive state, callbacks and text models.
- ViewModels invoke use cases and publish UI state. They never receive `NavController` or Android `Context`.
- Product text comes from `AppLanguage`; dates come from `DateProvider`.

## Navigation and state

- Navigation is emitted through `ComposeNavigationController` and handled by the navigation host.
- Routes carry only typed identifiers or primitives; screens load their own data through the normal use-case path.
- UI state uses stable sealed interfaces. Loading, empty, error and content states are observable and tested.
- A user-facing CTA must produce real behavior or remain hidden; placeholders are not valid feature completion.

## Data and verification

- Retrofit DTOs map at data boundaries; domain/use-case contracts remain framework-free.
- Backend remains the source of truth. Room is a read fallback; do not promise offline mutation sync without a designed queue.
- Token, credentials and raw backend error content must not appear in logs or UI.
- Time-dependent logic uses `DateProvider`; request freshness and concurrent mutations require explicit coordination and tests.