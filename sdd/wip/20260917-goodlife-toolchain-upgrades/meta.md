# GoodLife Android — plan de actualización de toolchain

- Feature id: `20260917-goodlife-toolchain-upgrades`.
- Estado: `ready_for_execution`.
- Issue: #39.
- Rama: `chore/android-toolchain-upgrade-plan`.
- Idioma: español (es-AR).

## Decisión de alcance

Este artefacto planifica familias compatibles de actualización; no cambia versiones ni intenta silenciar los advisories externos de CI. Cada familia se entregará en su propio issue y PR, solamente después de evidencia oficial de compatibilidad y gates ejecutables.

## Baseline observado

- AGP `8.10.0`, Gradle Wrapper `8.12.1`, JDK 17, Kotlin `2.1.0` y KSP `2.1.0-1.0.29`.
- `compileSdk`/`targetSdk` 35 y `minSdk` 26.
- Compose BOM `2024.12.01`; Navigation `2.8.7`; Room `2.6.1`; Lifecycle `2.8.7`; Activity `1.9.3`.
- CI exige tests lógicos, cobertura elegible >= 80 %, lint debug/release, ensamblado release no firmado y un inventario determinista de advisories.

No se almacenan resultados de feeds, credenciales, tokens, IPs o identificadores de dispositivos en este artefacto.