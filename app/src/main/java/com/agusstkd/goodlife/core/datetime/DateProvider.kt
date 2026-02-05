package com.agusstkd.goodlife.core.datetime

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.ExperimentalTime

/**
 * Proveedor de fecha/hora abstraído para testing determinista.
 *
 * ## Responsabilidades:
 * - Proveer fecha actual respetando timezone local del dispositivo
 * - Habilitar tests con fechas fijas (dependency injection)
 * - Encapsular el uso de `Clock.System` y `@OptIn(ExperimentalTime)`
 * - Exponer operaciones comunes de fechas (today, yesterday, tomorrow)
 *
 * ## Arquitectura:
 * ```
 * DateProvider (interface)
 *     ├── RealDateProvider (producción)
 *     │   └── Clock.System + TimeZone.currentSystemDefault()
 *     └── FakeDateProvider (testing)
 *         └── Fecha fija inyectada
 * ```
 *
 * ## Problema que resuelve:
 * Antes se usaba `Clock.todayHere()` que NO respetaba el timezone local en
 * emuladores Android, devolviendo fecha UTC y causando errores de +1 día.
 * DateProvider usa correctamente `clock.now().toLocalDateTime(timeZone).date`.
 *
 * ## Uso en ViewModels:
 * ```kotlin
 * class DailyTabViewModel(
 *     private val dateProvider: DateProvider,
 *     private val language: AppLanguage
 * ) : ViewModel() {
 *     private val today = dateProvider.today()
 *     private val isToday = date == dateProvider.today()
 * }
 * ```
 *
 * ## Uso en Tests:
 * ```kotlin
 * @Test
 * fun `muestra fecha correcta al inicializar`() {
 *     val fakeProvider = FakeDateProvider(LocalDate(2025, 12, 25))
 *     val viewModel = DailyTabViewModel(fakeProvider, AppLanguage.Spanish)
 *     
 *     assertEquals(LocalDate(2025, 12, 25), viewModel.uiState.value.date)
 *     assertEquals("Hoy", viewModel.uiState.value.headerText)
 * }
 * ```
 *
 * ## Inyección de Dependencias:
 * ```kotlin
 * // Koin - AppModule.kt
 * single<DateProvider> { RealDateProvider() }
 * 
 * // ViewModels
 * viewModel { DailyTabViewModel(dateProvider = get(), language = get()) }
 * ```
 *
 * ## Serialización para API:
 * ```kotlin
 * val date: LocalDate = dateProvider.today()  // LocalDate(2026, 2, 3)
 * val dateString = date.toString()            // "2026-02-03" (ISO-8601)
 * ```
 * **Importante:** `kotlinx.datetime.LocalDate.toString()` devuelve formato ISO-8601
 * (`YYYY-MM-DD`), compatible directamente con el backend sin conversión adicional.
 *
 * @see RealDateProvider Implementación real para producción
 * @see FakeDateProvider Implementación fake para testing
 */
interface DateProvider {

    /**
     * Obtiene la fecha actual en timezone local del dispositivo.
     *
     * ## Garantía:
     * Si son las 23:00 del 3 de febrero en Argentina (GMT-3):
     * - UTC: 02:00 del 4 de febrero
     * - Esta función devuelve: 3 de febrero ✅
     *
     * @return Fecha de hoy (año, mes, día) en timezone local
     */
    fun today(): LocalDate

    /**
     * Obtiene el timestamp actual UTC.
     *
     * Útil para operaciones de logging, timestamps de API, etc.
     *
     * @return Instant (timestamp UTC)
     */
    @OptIn(ExperimentalTime::class)
    fun now(): Instant

    /**
     * Obtiene la fecha de ayer.
     *
     * Default implementation: `today() - 1 día`
     *
     * @return Fecha de ayer en timezone local
     */
    fun yesterday(): LocalDate = today().minus(1, DateTimeUnit.DAY)

    /**
     * Obtiene la fecha de mañana.
     *
     * Default implementation: `today() + 1 día`
     *
     * @return Fecha de mañana en timezone local
     */
    fun tomorrow(): LocalDate = today().plus(1, DateTimeUnit.DAY)
}
