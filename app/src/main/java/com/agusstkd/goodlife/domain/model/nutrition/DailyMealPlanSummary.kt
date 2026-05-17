package com.agusstkd.goodlife.domain.model.nutrition

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Resumen de un plan de comidas para la vista del tab Meals.
 *
 * Modelo de dominio liviano, optimizado para el listado diario.
 * No incluye ingredientes completos ni pasos de preparación.
 *
 * @property id ID del plan de comida asignado por el backend.
 * @property name Nombre del plan (ej: "Plan Volumen Marzo").
 * @property date Fecha a la que aplica este plan.
 * @property mealType Tipo de comida del plan (desayuno, almuerzo, etc.).
 * @property scheduledTime Hora programada de la comida, si fue definida.
 * @property mealName Nombre de la comida principal del plan (opcional).
 * @property imageUrl URL de la imagen de la comida principal (opcional).
 * @property totalCalories Calorías totales del plan en kcal.
 * @property totalProtein Proteínas totales en gramos.
 * @property totalCarbs Carbohidratos totales en gramos.
 * @property totalFat Grasas totales en gramos.
 * @property isActive Si el plan está activo para el día.
 */
data class DailyMealPlanSummary(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val mealType: MealType,
    val scheduledTime: LocalTime?,
    val mealName: String?,
    val imageUrl: String?,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double,
    val isActive: Boolean,
)
