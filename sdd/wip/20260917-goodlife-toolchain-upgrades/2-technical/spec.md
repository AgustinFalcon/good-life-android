# Especificación técnica — familias, ledger y matriz de compatibilidad

## Familias atómicas propuestas

| Subfamilia | Componentes | Estado inicial | Regla de atomicidad |
|---|---|---|---|
| T1a — Core | `androidx.core:core-ktx` | `candidate` | Un issue/PR exclusivo. |
| T1b — Activity | `androidx.activity:activity-compose` | `candidate` | Un issue/PR exclusivo; validar arranque y navegación. |
| T1c — Lifecycle | `lifecycle-runtime-ktx` + `lifecycle-viewmodel-ktx` | `candidate` | Viajan juntos: comparten `lifecycleRuntimeKtx` y contratos de ViewModel. |
| T1d — Navigation | `navigation-compose` | `candidate` | Un issue/PR exclusivo; validar rutas tipadas y back stack. |
| T1e — Room | `room-runtime` + `room-ktx` + `room-compiler` | `candidate` | Viajan juntos: runtime, extensiones y procesador deben tener la misma versión. |
| T1f — AndroidX JUnit | `androidx.test.ext:junit` | `candidate` | Un issue/PR exclusivo. |
| T1g — Espresso | `androidx.test.espresso:espresso-core` | `candidate` | Un issue/PR exclusivo y prueba instrumentada local. |
| T2 — Compose UI | Compose BOM, Material 3, icons y tooling | `hold` | La BOM avanza sola, con validación visual/instrumentada; nunca con AGP. |
| T3 — Kotlin build | Kotlin, KSP y plugins Kotlin | `hold` | Kotlin y KSP permanecen emparejados, con compatibilidad oficial de procesadores. |
| T4 — Android build toolchain | AGP, Gradle Wrapper, JDK y SDK si aplica | `hold` | Migración mayor separada; puede cambiar requisitos de Gradle/JDK y lint/packaging. |
| T5 — terceros | Koin, Retrofit, OkHttp, Coil, Lottie, coroutines y datetime | `candidate` individual | Cada proveedor y versión tiene un issue/PR propio. |

`candidate` no autoriza un cambio: es una familia apta para investigación. `hold` requiere primero una decisión explícita de compatibilidad. Ninguna columna representa una actualización masiva.

## Ledger de advisories externos de #39

Fuente: `config/lint-baseline.json.externalAdvisories`, deduplicada por componente/versión sobre `debug` y `release`. El issue común es #39; cada ejecución abre su issue hijo y PR.

| Advisory único | Baseline | Subfamilia | Estado | Motivo y trigger de revisión |
|---|---:|---|---|---|
| `com.android.application` | 8.10.0 | T4 | `hold` | Requiere matriz oficial AGP–Gradle–JDK y PR aislado. |
| `androidx.navigation:navigation-compose` | 2.8.7 | T1d | `candidate` | Tras #5 o su bloqueo documentado; rutas y back stack instrumentados. |
| `androidx.test.espresso:espresso-core` | 3.6.1 | T1g | `candidate` | Dispositivo/emulador estable y `connectedDebugAndroidTest`. |
| `androidx.test.ext:junit` | 1.2.1 | T1f | `candidate` | Verificar runner e instrumentadas disponibles. |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.8.7 | T1c | `candidate` | Debe viajar con `lifecycle-viewmodel-ktx`; tests de ViewModel. |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | 2.8.7 | T1c | `candidate` | Mismo PR y versión que runtime; no se actualiza aislado. |
| `androidx.room:room-runtime` | 2.6.1 | T1e | `candidate` | Debe viajar con KTX y compiler; tests de DB/DI. |
| `androidx.room:room-ktx` | 2.6.1 | T1e | `candidate` | Mismo PR y versión que runtime/compiler. |
| `androidx.room:room-compiler` | 2.6.1 | T1e | `candidate` | Mismo PR y versión que runtime/KTX; validar KSP. |
| `androidx.core:core-ktx` | 1.13.1 | T1a | `candidate` | PR aislado y gates estándar. |
| `androidx.compose:compose-bom` | 2024.12.01 | T2 | `hold` | Resolver #5/bloqueo y exigir instrumentadas + revisión visual. |
| `androidx.activity:activity-compose` | 1.9.3 | T1b | `candidate` | PR aislado; validar arranque/navegación instrumentada. |

## Matriz mínima por PR de ejecución

| Dimensión | Baseline actual | Regla de propuesta |
|---|---|---|
| JDK de build | 17 | Mantener 17 salvo que documentación oficial de AGP/Gradle exija otra versión; declarar razón. |
| Java/Kotlin bytecode | 11 (`VERSION_11`, `JvmTarget.JVM_11`) | No cambiar implícitamente al modificar T3/T4; cualquier salto requiere decisión explícita y compatibilidad Android declarada. |
| Gradle | 8.12.1 | Solo T4; probar compatibilidad exacta con AGP propuesto. |
| AGP | 8.10.0 | Solo T4; nunca por advisory aislado. |
| Kotlin/KSP | 2.1.0 / 2.1.0-1.0.29 | Solo T3 y juntos, con tabla oficial de compatibilidad. |
| SDK | compile/target 35, min 26 | Mantener fuera de T4 salvo decisión explícita. |
| Compose | BOM 2024.12.01 | Solo T2 con instrumentadas y revisión visual. |

## Gates ejecutables y evidencia de advisories

| Momento | Gate obligatorio |
|---|---|
| Todo PR de ejecución | `:app:logicDebugUnitTestCoverageVerification`, `:app:lintDebug :app:lintRelease`, `.github/scripts/verify_lint_baseline.py` y `:app:assembleRelease`. |
| T1b/T1d/T1e/T1f/T1g/T2 | `:app:connectedDebugAndroidTest` en entorno local/dispositivo estable; si no puede ejecutarse, el PR no declara esa superficie validada y no avanza de `hold`/`blocked`. |
| T2 | Además, revisión visual manual redacted de las pantallas impactadas. |
| T3/T4 | Evidencia oficial de compatibilidad y build limpio antes de cualquier merge; #5 resuelto o bloqueado con causa real. |

El CI del PR publica el delta externo de lint como evidencia. Cuando un PR cambia una versión que altera advisories, actualiza `externalAdvisories` únicamente a partir de los XML generados en ese mismo PR y explica el delta esperado. El baseline determinista no se edita para ocultar regresiones. El monitor de `master` con `--fail-on-advisory-drift` confirma el estado post-merge; no reemplaza los gates del PR ni se usa como precondición ficticia.

## Orden, rollback y criterios de stop

1. Resolver #5 o registrar su bloqueo real antes de T2–T4, para no confundir herramienta con autenticación/conectividad.
2. Ejecutar como máximo una subfamilia atómica por PR; comenzar por una `candidate` pequeña sólo con compatibilidad oficial verificada.
3. Si cambia el inventario sin justificación, falla un gate, cae cobertura, se rompe release o aparece regresión visual/funcional, revertir únicamente ese PR.
4. Una incompatibilidad oficial, requisito no viable o fallo no diagnosticado deja la subfamilia `blocked` con evidencia; no autoriza atajos ni supresiones.