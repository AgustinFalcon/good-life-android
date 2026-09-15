---
type: Contracts
version: recovery-audit-2026-09
validated: 2026-09-10
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
- El icono primario instalable ic_logo_install es un adaptive icon base anydpi con capa monochrome; requiere una comprobación visual de launcher.
- Android mantiene `android:allowBackup=false`; no hay contrato de backup o device transfer para datos locales.
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
- No exponer JWT/credenciales en logs; refresh 401 está implementado y requiere validación en dispositivo.

