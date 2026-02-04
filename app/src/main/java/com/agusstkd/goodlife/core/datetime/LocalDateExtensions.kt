package com.agusstkd.goodlife.core.datetime

/**
 * Extensiones de fechas para kotlinx.datetime.LocalDate.
 *
 * ## Nota importante:
 * Este archivo está vacío porque todas las funciones de formateo
 * se movieron a los ViewModels (DailyTabViewModel, MainScaffoldViewModel, etc.).
 *
 * ## Filosofía:
 * - ViewModels formatean fechas usando `DateProvider` + `AppLanguage`
 * - UI recibe strings ya formateados en el `UiState`
 * - Componentes NO conocen lógica de fechas
 *
 * ## Migración:
 * - `Clock.todayHere()` → `dateProvider.today()`
 * - `date.isToday(clock)` → `date == dateProvider.today()`
 * - `date.toFriendlyString(clock, language)` → formateo en ViewModel
 *
 * Este archivo se mantiene para futuras extensiones de LocalDate
 * que NO dependan de Clock ni Language (ej: conversiones puras).
 */