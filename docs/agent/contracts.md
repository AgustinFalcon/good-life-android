---
type: Contracts
version: a841b13
validated: 2026-07-13
update_when: "Cambien endpoints, DTOs, auth, estados o integraciones."
scope:
  - README.md
  - app/build.gradle.kts
  - app/src/main/java/com/agusstkd/goodlife
  - docs
---

# Contracts

## Expuestos

- Android applicationId `com.agusstkd.goodlife`, minSdk 26, target/compile 35.
- Routes AppRoute Splash/Login/Register/Main/Create* y TabRoute Daily/Workouts/Meals/Settings/Profile.

## Consumidos

- GoodLife REST `/api/v1` por Retrofit
- Room
- Koin 4, Compose, Navigation, OkHttp
- Biometric y EncryptedSharedPreferences APIs

## Compatibilidad

- Mantener DTOs Retrofit alineados al backend.
- Owners manejan DI/side effects; screens son puros.
- Textos via AppLanguage y fecha via DateProvider.
- No exponer JWT/credenciales en logs; 401/refresh sigue siendo área crítica.

