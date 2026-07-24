# GoodLife Android: guía para agentes

Aplicación Android Kotlin/Jetpack Compose, módulo único `:app`. Cliente móvil de GoodLife con Clean Architecture/MVVM, Room, Retrofit, Koin y navegación type-safe.

Leer primero `../../sdd/PROJECT.md` y `../../sdd/PATTERNS.md`; contrastar con `README.md`, `app/build.gradle.kts`, `app/src/main/java/com/agusstkd/goodlife`, `docs`. Cada hecho debe rastrearse a una ruta.

Actualizar `docs/agent/` en el mismo PR cuando cambien arquitectura, contratos, runbook o traps.

## Reglas críticas

- Mantener DTOs Retrofit alineados al backend.
- Owners manejan DI/side effects; screens son puros.
- Textos via AppLanguage y fecha via DateProvider.
- No exponer JWT/credenciales en logs; 401/refresh sigue siendo área crítica.

Preservar cambios locales y no desplegar/usar secretos por defecto.

## Índice

- [Overview](docs/agent/overview.md)
- [Architecture](docs/agent/architecture.md)
- [Contracts](docs/agent/contracts.md)
- [Runbook](docs/agent/runbook.md)
- [Traps](docs/agent/traps.md)

