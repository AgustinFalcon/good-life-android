# 📋 Especificaciones de GoodLife Android

Este directorio contiene las especificaciones técnicas detalladas para las features del proyecto.

---

## 📑 Índice de Specs

| ID | Feature | Tipo | Estado |
|----|---------|------|--------|
| [SPEC-001](./SPEC-001-register-screen.md) | Pantalla de Registro | Feature | ✅ Completado |
| [SPEC-002](./SPEC-002-biometric-login.md) | Login con Huella Digital | Feature | ✅ Completado |
| [SPEC-003](./SPEC-003-main-scaffold.md) | Main Scaffold + Bottom Nav | Feature | 🚧 En Progreso (90%) |
| [SPEC-004](./SPEC-004-date-provider.md) | DateProvider Pattern (Core de Fechas) | Core Architecture | ✅ Completado |
| [SPEC-005](./SPEC-005-network-service.md) | Network Service | Core Architecture | ✅ Completado |
| [SPEC-006](./SPEC-006-offline-first-swr.md) | Offline-First SWR | Architecture Pattern | 📝 Planificado |
| [SPEC-007](./SPEC-007-app-language.md) | Sistema de Localización KMP-Ready (AppLanguage) | Core Architecture | ✅ Completado |
| [SPEC-008](./SPEC-008-notifications-deeplinks.md) | Notificaciones en Tiempo Real + Deep Links | Feature | 📝 Planificado |

---

## 🏗️ Estructura de un Spec

Cada especificación sigue esta estructura:

1. **Información General**: ID, tipo, prioridad, estado, tiempo estimado
2. **Objetivo**: Qué problema resuelve
3. **Problemas que resolvió**: Antes/después con ejemplos de código
4. **Solución Implementada**: Arquitectura, flujo de datos
5. **Archivos del Sistema**: Tablas de archivos creados/modificados
6. **Reglas Arquitectónicas**: Prohibiciones y patrones obligatorios
7. **Cómo extender**: Paso a paso para agregar funcionalidad nueva
8. **Checklist de Validación**: Verificación post-implementación

---

## 📌 Estados de Specs

| Estado | Descripción |
|--------|-------------|
| 📝 Planificado | Diseño documentado, pendiente de implementar |
| 🚧 En Progreso | Implementación activa |
| ✅ Completado | Feature implementada y validada |
| ⏸️ Pausado | Postergado temporalmente |

---

## 🔗 Relaciones entre SPECs

```
SPEC-001 (Register)
    └── usa → SPEC-007 (AppLanguage: AuthTexts, ValidationTexts)

SPEC-002 (Biometric Login)
    └── usa → SPEC-007 (AppLanguage: AuthTexts.biometricPrompt*)

SPEC-003 (Main Scaffold)
    ├── usa → SPEC-004 (DateProvider para formateo de fechas)
    └── usa → SPEC-007 (AppLanguage: MainScaffoldTexts, AccessibilityTexts)

SPEC-004 (DateProvider)
    └── usa → SPEC-007 (AppLanguage: formats, relativeTexts)

SPEC-005 (Network)
    └── usa → SPEC-007 (AppLanguage: ErrorTexts)

SPEC-008 (Notifications + Deep Links)
    ├── usa → SPEC-005 (Network: OkHttpClient compartido para WebSocket)
    └── usa → SPEC-007 (AppLanguage: ErrorTexts, NotificationTexts)

SPEC-007 (AppLanguage) ← Core para toda la app
    └── alimenta → todos los demás SPECs
```

---

## 📊 Resumen de Progreso

| Módulo | Estado |
|--------|--------|
| Login | ✅ Completado |
| Biometría | ✅ Completado |
| Registro | ✅ Completado |
| DateProvider | ✅ Completado |
| Network Service | ✅ Completado |
| Localización (AppLanguage) | ✅ Completado |
| Main Scaffold | 🚧 En Progreso (90%) |
| Daily Tab — Arquitectura | ✅ Completado |
| Daily Tab — DailyItemCard | ✅ Completado |
| Workouts Tab | ⏸️ Pendiente |
| Meals Tab | ⏸️ Pendiente |
| Settings/More Tab | ⏸️ Pendiente |
| EncryptedSharedPreferences (TokenManager) | ⏸️ Pendiente (crítico) |
| Offline-First SWR | 📝 Planificado |
| Notificaciones + Deep Links | 📝 Planificado |

---

## 🔗 Documentación Relacionada

- **ARCHITECTURE**: Ver `/docs/ARCHITECTURE.md` para arquitectura general
- **ARCHITECTURE_GUIDE**: Ver `/docs/ARCHITECTURE_GUIDE.md` para la guía completa de principios, patrones y reglas
- **PROGRESS**: Ver `/docs/PROGRESS.md` para el estado detallado de implementación

---

**Última actualización:** 2026-02-24
