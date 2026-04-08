package com.agusstkd.goodlife.core.datetime.language

import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames

/**
 * Sistema de internacionalización KMP-ready.
 *
 * Centraliza TODOS los textos de la app en pure Kotlin, sin Android Context.
 * Cada idioma implementa todas las propiedades en su propio archivo.
 *
 * ## Agregar nuevo idioma:
 * 1. Crear un nuevo archivo `NuevoIdioma.kt` en este mismo package
 * 2. Declarar `data object NuevoIdioma : AppLanguage`
 * 3. Implementar TODAS las propiedades (el compilador valida exhaustividad)
 * 4. Registrar en `AppModule.kt` mapeando el locale correspondiente
 *
 * ## Selección automática:
 * En `AppModule.kt`, se detecta el locale del dispositivo y se mapea al
 * `AppLanguage` correspondiente.
 *
 * ## Implementaciones actuales:
 * - [Spanish] → `Spanish.kt`
 * - [English] → `English.kt`
 * - [Portuguese] → `Portuguese.kt`
 *
 * @see DailyItemLabels
 * @see ValidationTexts
 * @see ErrorTexts
 * @see DailyTexts
 * @see MainScaffoldTexts
 * @see HomeTexts
 * @see AuthTexts
 * @see AccessibilityTexts
 */
sealed interface AppLanguage {
    val monthNames: MonthNames
    val dayNamesShort: DayOfWeekNames
    val formats: DateFormats
    val relativeTexts: RelativeDateTexts
    val dailyItemLabels: DailyItemLabels
    val validationTexts: ValidationTexts
    val errorTexts: ErrorTexts
    val dailyTexts: DailyTexts
    val mainScaffoldTexts: MainScaffoldTexts
    val homeTexts: HomeTexts
    val authTexts: AuthTexts
    val accessibilityTexts: AccessibilityTexts
    val datePickerTexts: DatePickerTexts
    val createItemSharedTexts: CreateItemSharedTexts
    val createTaskTexts: CreateTaskTexts
    val habitCategoryTexts: HabitCategoryTexts
    val createHabitTexts: CreateHabitTexts
    val createRoutineTexts: CreateRoutineTexts
    val mealTypeTexts: MealTypeTexts
    val createMealPlanTexts: CreateMealPlanTexts
    val splashTexts: SplashTexts
}
