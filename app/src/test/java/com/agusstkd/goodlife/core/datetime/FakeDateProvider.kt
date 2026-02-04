package com.agusstkd.goodlife.core.datetime

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

/**
 * Implementación fake de [DateProvider] para testing determinista.
 *
 * ## Responsabilidades:
 * - Proveer fecha fija inyectada en el constructor
 * - Habilitar tests con fechas predecibles (no dependen del reloj del sistema)
 * - Permitir testing de lógica de fechas (hoy, ayer, mañana)
 * - Facilitar testing de formateo de fechas
 *
 * ## Arquitectura de Testing:
 * ```
 * Test
 *     ├── FakeDateProvider(fixedDate)
 *     └── ViewModel(fakeProvider)
 *             └── UiState con fecha predecible
 * ```
 *
 * ## Ventajas:
 * - **Determinismo:** Tests siempre pasan con la misma fecha
 * - **Velocidad:** No depende de Clock.System
 * - **Control:** Puedes testear cualquier fecha (pasado, futuro, casos edge)
 * - **Simplicidad:** Constructor simple con una sola fecha
 *
 * ## Ejemplo 1: Test de inicialización
 * ```kotlin
 * @Test
 * fun `al inicializar debe mostrar fecha de hoy`() {
 *     // Given
 *     val fixedDate = LocalDate(2025, 2, 15)
 *     val fakeProvider = FakeDateProvider(fixedDate)
 *
 *     // When
 *     val viewModel = DailyTabViewModel(fakeProvider, AppLanguage.Spanish)
 *
 *     // Then
 *     assertEquals(fixedDate, viewModel.uiState.value.date)
 *     assertEquals(15, viewModel.uiState.value.dayNumber)
 *     assertEquals("Hoy", viewModel.uiState.value.headerText)
 * }
 * ```
 *
 * ## Ejemplo 2: Test de navegación
 * ```kotlin
 * @Test
 * fun `al navegar al día siguiente, fecha aumenta 1 día`() {
 *     // Given
 *     val today = LocalDate(2025, 2, 15)
 *     val fakeProvider = FakeDateProvider(today)
 *     val viewModel = DailyTabViewModel(fakeProvider, AppLanguage.Spanish)
 *
 *     // When
 *     viewModel.onAction(DailyUiAction.OnNextDay)
 *
 *     // Then
 *     assertEquals(LocalDate(2025, 2, 16), viewModel.uiState.value.date)
 *     assertEquals("Mañana", viewModel.uiState.value.headerText)
 * }
 * ```
 *
 * ## Ejemplo 3: Test de formateo
 * ```kotlin
 * @Test
 * fun `muestra texto relativo cuando es ayer`() {
 *     // Given
 *     val today = LocalDate(2025, 2, 15)
 *     val fakeProvider = FakeDateProvider(today)
 *     val viewModel = DailyTabViewModel(fakeProvider, AppLanguage.Spanish)
 *
 *     // When
 *     viewModel.onAction(DailyUiAction.OnPreviousDay)
 *
 *     // Then
 *     assertEquals(LocalDate(2025, 2, 14), viewModel.uiState.value.date)
 *     assertEquals("Ayer", viewModel.uiState.value.headerText)
 * }
 * ```
 *
 * ## Nota sobre now():
 * Devuelve el inicio del día (00:00:00) en UTC.
 * Si necesitás un timestamp diferente, extendé esta clase.
 *
 * @param fixedDate Fecha fija que siempre devolverá [today]
 * @see DateProvider Interfaz abstraída
 * @see RealDateProvider Implementación real
 */
class FakeDateProvider(
    private val fixedDate: LocalDate
) : DateProvider {

    /**
     * Siempre devuelve la fecha fija pasada en el constructor.
     *
     * Esta es la base del testing determinista:
     * - No importa cuántas veces se llame, siempre devuelve lo mismo
     * - No depende del reloj del sistema
     * - Permite testear cualquier fecha sin cambiar el reloj del emulador
     *
     * @return [fixedDate] inyectada en el constructor
     */
    override fun today(): LocalDate = fixedDate

    /**
     * Devuelve el Instant correspondiente al inicio del día (00:00:00)
     * de [fixedDate] en UTC.
     *
     * ## Uso:
     * Útil para tests que necesitan timestamp, no solo fecha.
     *
     * ## Ejemplo:
     * ```kotlin
     * val provider = FakeDateProvider(LocalDate(2025, 2, 15))
     * val instant = provider.now()
     * // instant = 2025-02-15T00:00:00Z
     * ```
     *
     * @return Instant de [fixedDate] a las 00:00:00 UTC
     */
    override fun now(): Instant =
        fixedDate.atStartOfDayIn(TimeZone.UTC)
}
