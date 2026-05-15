@file:OptIn(ExperimentalTime::class)

package com.agusstkd.goodlife.core.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Implementación real de [DateProvider] para producción en Android.
 *
 * ## Responsabilidades:
 * - Proveer fecha actual usando timezone local del dispositivo
 * - Cachear TimeZone para optimizar performance
 * - Encapsular `@OptIn(ExperimentalTime)` (único lugar con esta anotación)
 * - Garantizar compatibilidad KMP (solo usa kotlinx.datetime)
 *
 * ## Arquitectura:
 * ```
 * RealDateProvider
 *     ├── Clock.System (kotlinx.datetime)
 *     └── TimeZone.currentSystemDefault() (cacheado)
 * ```
 *
 * ## Implementación correcta de timezone:
 * ```kotlin
 * clock.now()                   // Instant UTC (ej: 02:00 del 4 de febrero UTC)
 *   .toLocalDateTime(timeZone)  // LocalDateTime local (ej: 23:00 del 3 de feb GMT-3)
 *   .date                        // LocalDate (2026-02-03) ✅
 * ```
 *
 * ## Bug que resolvió:
 * Antes se usaba:
 * ```kotlin
 * fun Clock.todayHere(): LocalDate {
 *     return java.time.LocalDate.now().toKotlinLocalDate()  // ❌ UTC en emuladores
 * }
 * ```
 * Causaba fecha +1 día porque NO usaba el TimeZone correctamente.
 *
 * ## Performance:
 * - `TimeZone.currentSystemDefault()` es costoso → cacheado en init
 * - `Clock.System.now()` es ligero → llamado cada vez
 * - Timezone no cambia durante ejecución → cache válido siempre
 *
 * ## KMP Compatibility:
 * Este código es compatible con Kotlin Multiplatform.
 * Para iOS, se puede usar expect/actual:
 * ```kotlin
 * // commonMain/DateProvider.kt
 * expect class RealDateProvider() : DateProvider
 * 
 * // androidMain/RealDateProvider.kt
 * actual class RealDateProvider : DateProvider { ... }
 * 
 * // iosMain/RealDateProvider.kt
 * actual class RealDateProvider : DateProvider { ... }
 * ```
 *
 * @see DateProvider Interfaz abstraída
 * @see FakeDateProvider Implementación fake para testing
 */
class RealDateProvider : DateProvider {

    /**
     * TimeZone local reportado por el sistema operativo.
     *
     * ## Caching:
     * Cacheado al instanciar la clase porque:
     * - `TimeZone.currentSystemDefault()` es una operación costosa
     * - El timezone no cambia durante la ejecución de la app
     * - Se llama múltiples veces en ViewModels y formateos
     *
     * ## Nota:
     * No se aplica ningún fallback geográfico hardcodeado.
     * En emulador, si el timezone está mal (ej: UTC), debe corregirse desde ajustes del dispositivo.
     */
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()

    /**
     * Reloj del sistema que provee tiempo UTC actual.
     *
     * Usa `Clock.System` de kotlinx.datetime (KMP-compatible).
     * NO cachear porque `now()` debe ser llamada cada vez para obtener tiempo actual.
     */
    private val clock: Clock = Clock.System

    /**
     * Obtiene la fecha de hoy en el timezone local del dispositivo.
     *
     * ## Ejemplo real:
     * ```
     * Hora local: 23:00 del 3 de febrero de 2026 (GMT-3 Argentina)
     * Hora UTC:   02:00 del 4 de febrero de 2026
     * 
     * clock.now() → Instant(2026-02-04T02:00:00Z)
     * .toLocalDateTime(timeZone) → LocalDateTime(2026-02-03T23:00:00)
     * .date → LocalDate(2026-02-03) ✅ CORRECTO
     * ```
     *
     * ## Garantía:
     * Siempre devuelve la fecha del calendario local del dispositivo,
     * NO la fecha UTC.
     *
     * @return LocalDate de hoy en timezone local
     */
    override fun today(): LocalDate {
        return clock.now().toLocalDateTime(timeZone).date
    }

    /**
     * Obtiene el timestamp UTC actual.
     *
     * Útil para:
     * - Logging con timestamp preciso
     * - Timestamps de API (siempre UTC)
     * - Cálculos de duración entre eventos
     *
     * @return Instant (timestamp UTC)
     */
    override fun now(): Instant {
        return clock.now()
    }
}
