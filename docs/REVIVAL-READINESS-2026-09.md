# GoodLife Android — readiness para retomar desarrollo

**Fecha de auditoría:** 2026-09-09  
**Estado:** base recuperada; lista para desarrollo controlado, no para release público.  
**Fuente de verdad:** código actual, build Gradle, tests, API pública y el SDD versionado bajo `sdd/`. Este documento conserva una fotografía de recovery; si discrepa del SDD o del código, prevalecen estos últimos.

## Evidencia comprobada

| Área | Resultado | Evidencia |
|---|---|---|
| API pública | Operativa | `GET https://good-life.ddns.net/api/v1/health` devuelve `200 {"status":"UP"}`; Swagger y OpenAPI responden `200`. |
| Ruta Android | Configurada contra la API recuperada | `NetworkConstants.BASE_URL = https://good-life.ddns.net/`. No usar el hostname histórico `devtukychloe.ddns.net`. |
| Backend y datos | Recuperados en DEV | Docker API y PostgreSQL saludables; existen 3 usuarios, 42 ingredientes, 2 comidas, 2 rutinas, 3 workouts, 110 ejercicios y 36 daily logs. |
| Calidad Android | Gate vigente aprobado | `:app:logicDebugUnitTestCoverageVerification` pasó. JaCoCo: 1.483/1.845 líneas cubiertas = **80,38%** para la lógica incluida. |
| Pruebas Android | Suite unitaria aprobada | 164 tests, 0 failures/errors en el reporte actual. |
| Seguridad de sesión | Implementada en código | `EncryptedSharedPreferences`, sesión al arrancar y `GoodLifeAuthenticator` con refresh serializado por `Mutex`. |

## Qué está implementado de verdad

- Auth: registro, login, logout, persistencia cifrada de tokens, chequeo de sesión y biometría.
- Daily: lectura por fecha, actualización de estado, filtro y cache Room como fallback de lectura.
- Creación: task, habit, routine y meal plan.
- Tabs: Daily, Workouts, Meals, Settings y Profile tienen Owner/Screen/ViewModel reales; Settings permite logout y Profile es accesible.
- Infraestructura de cliente: Koin, Retrofit/OkHttp, Room, `DateProvider`, `AppLanguage`, navegación tipada y gate JaCoCo.

## Faltantes reales, ordenados por prioridad

### P0 — requisito antes de declarar una versión usable

1. **Prueba en un dispositivo o emulador real contra DEV.** No había `adb` con un dispositivo conectado durante la auditoría. Ejecutar login con una cuenta de prueba, auto-login tras reiniciar, refresh de token, logout, lectura y cambio de estado de Daily.
2. **Corregir la sincronización del perfil.** El backend expone `GET /api/v1/me` y el cliente declara `getMe()`, pero `AuthRepositoryImpl` todavía construye el usuario desde el JWT/login y conserva un TODO obsoleto. Consumir `/me` tras login/refresh y actualizar Room.
3. **Fijar una cuenta de smoke-test no personal y datos reproducibles.** Los datos recuperados son valiosos, pero no son un fixture de pruebas. No usar ni registrar credenciales en el repositorio.

### P1 — cerrar flujos que hoy llegan a pantallas sin producto

1. Workouts: inyectar navegación en `WorkoutsTabViewModel`; conectar "crear rutina" y definir ejecución/detalle de workout. El backend ya posee logs de workout, estadísticas y records que Android no consume.
2. Meals: implementar navegación y detalle de meal plan; completar logging de comidas si se adopta como flujo de producto.
3. Daily: decidir el destino por tipo de ítem. Hoy `DailyDetail` es un placeholder y el ViewModel no navega.
4. Reemplazar los tres placeholders de `TabNavGraph` (`DailyDetail`, `WorkoutDetail`, `MealDetail`) por rutas con contrato y pantalla real; no publicar pantallas que aparenten estar terminadas.
5. Resolver `QuickActionType.OTHER` y la acción pendiente de detalle de comida del wizard.

### P2 — calidad y producto antes de un beta externo

- Mover los textos visibles que siguen hardcodeados en tabs y placeholders a `AppLanguage`; la regla del proyecto ya exige que Screen/Component no creen texto de producto.
- Definir y probar offline de escritura. La cache SWR actual solo cubre lectura; no existe cola de sincronización para mutaciones.
- Diseñar recuperación de contraseña, términos, privacidad y, solo si hay producto/backend/privacidad definidos, Google/Apple sign-in.
- Agregar pruebas instrumentadas y un recorrido E2E de autenticación/Daily. El gate unitario no verifica Compose, Keystore, Retrofit real ni navegación en dispositivo.
- Definir release: versionado, signing seguro, minificación, distribución interna, política de crash/telemetría y rollback. Hoy `versionCode=1`, `versionName=1.0`, minify desactivado y no hay pipeline de release documentado.

### Fuera de la próxima iteración salvo decisión de producto

- Push/deep links, dark mode completo, KMP real/iOS, pagos, IA, recomendaciones y panel admin.
- No son prerrequisitos para volver útil el core Daily + rutinas + comidas.

## Contratos que se deben respetar

| Contrato | Estado | Acción |
|---|---|---|
| URL base | `https://good-life.ddns.net/` | Centralizarla en `NetworkConstants`; no duplicar ni cambiar por un alias caducado. |
| Login | `POST /api/v1/token`, form `grantType=password`, `username`, `password` | Mantener el nombre `username`, aunque la UI acepte email. |
| Refresh | `POST /api/v1/token`, form `grantType=refreshToken`, `refreshToken` | El camelCase es obligatorio. |
| Perfil | `GET /api/v1/me` autenticado | Convertirlo en fuente de Room; retirar el TODO histórico. |
| Respuestas | `BaseResponse<T>` | Preservar mapeos defensivos y no exponer tokens en logs. |
| UI | Owner → ViewModel → use case → repository | `koinViewModel()` solo en Owner; textos por `AppLanguage`; fecha por `DateProvider`. |

## Plan de reingreso recomendado

1. Crear una rama `codex/goodlife-android-revival` desde el estado actual y documentar una cuenta de smoke-test fuera del repo.
2. Conectar un emulador API 35 o dispositivo; ejecutar el smoke P0 y guardar solo resultados no sensibles.
3. Entregar la sincronización `GET /me` con tests de repositorio y de ViewModel.
4. Elegir **un** vertical P1. Recomendación: Workouts, porque ya hay 2 rutinas/3 workouts y 110 ejercicios recuperados para validar el flujo.
5. Para cada pantalla, escribir spec corta antes de código: alcance, rutas, endpoints, estados loading/empty/error, analítica, tests y rollback.
6. Antes de beta, cerrar P0/P1, textos de producto, prueba instrumentada crítica y release checklist.

## Criterio de terminado de la próxima feature

- El flujo se prueba en dispositivo real contra `good-life.ddns.net` usando datos de prueba.
- El ViewModel, use case y contrato Retrofit tienen cobertura; `:app:logicDebugUnitTestCoverageVerification` sigue aprobado.
- No quedan rutas o botones del flujo apuntando a placeholders.
- Los textos viven en `AppLanguage` y la navegación respeta Owner/ViewModel.
- La documentación de `docs/agent/`, esta guía y Knowledge se actualizan en el mismo cambio.

## Documentos históricos que requieren lectura con contexto

`PROGRESS.md`, `IMPLEMENTATION-STATUS.md`, `NEXT-STEPS-PLAN.md`, `sdd/PROJECT.md`, `sdd/specs/*` y `HANDOFF-IA-GOODLIFE.md` contienen inventarios de marzo–mayo de 2026. Son útiles como historial, pero varias afirmaciones ya no son válidas: tabs, auto-login, refresh 401, almacenamiento cifrado, conteo de tests, topología y hostname. Su reconciliación queda trazada en Knowledge feature 008.
