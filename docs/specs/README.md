# 📋 Especificaciones de GoodLife Android

Este directorio contiene las especificaciones técnicas detalladas para las features del proyecto.

---

## 📑 Índice de Specs

| ID | Feature | Prioridad | Estado |
|----|---------|-----------|--------|
| [SPEC-001](./SPEC-001-register-screen.md) | Pantalla de Registro | Alta | 🔜 Pendiente |
| [SPEC-002](./SPEC-002-biometric-login.md) | Login con Huella Digital | Media | ✅ Completado |
| [SPEC-003](./SPEC-003-plan-implementation-register.md) | Plan de Implementación - Registro | Alta | 📝 Guía |

---

## 🏗️ Estructura de un Spec

Cada especificación sigue esta estructura:

1. **Información General**: ID, tipo, prioridad, estado
2. **Objetivo**: Qué problema resuelve
3. **Diseño Visual**: Mockups ASCII y estructura
4. **Modelos de Datos**: States, Actions, DTOs
5. **Arquitectura**: Archivos a crear/modificar
6. **Flujos**: Diagramas de flujo de usuario
7. **Integración**: Endpoints, dependencias
8. **Checklist**: Tareas específicas para implementar

---

## 📌 Estados de Specs

| Estado | Descripción |
|--------|-------------|
| 📝 Pendiente | Aún no iniciado |
| 🔜 Pendiente | Próximo a implementar |
| 🚧 En Progreso | Implementación activa |
| ✅ Completado | Feature implementada y testeada |
| ⏸️ Pausado | Postergado temporalmente |
| 📝 Guía | Documento de referencia/plan |

---

## 🎯 Próximos Pasos

1. **Implementar Registro**: Seguir [SPEC-003](./SPEC-003-plan-implementation-register.md) paso a paso
2. **Endpoint /me**: Backend debe implementar para obtener datos completos del usuario

---

## 🔗 Documentación Relacionada

- **CHANGELOG**: Ver `/CHANGELOG.md` para historial de versiones
- **ARCHITECTURE**: Ver `/docs/ARCHITECTURE.md` para arquitectura general
- **Backend Mejoras**: Ver `/back-end/GoodLife-backend-v2/docs/mejoras/`

---

## 📊 Resumen de Progreso

| Módulo | Estado |
|--------|--------|
| Login | ✅ Completado |
| Biometría | ✅ Completado |
| Registro | 🔜 Próximo |
| Home | ✅ Básico |
| Workouts | ⏸️ Pendiente |
| Meals | ⏸️ Pendiente |
| Settings | ⏸️ Pendiente |

---

**Última actualización:** 2026-01-20
