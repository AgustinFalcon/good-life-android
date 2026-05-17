package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import kotlinx.datetime.LocalDate

/**
 * Repositorio de planes de comida.
 */
interface MealPlanRepository {

    /**
     * Obtiene los planes de comida del usuario para una fecha específica.
     *
     * @param date Fecha del día a consultar.
     * @return Lista de resúmenes de planes de comida del día.
     */
    suspend fun getMealPlansByDate(date: LocalDate): Result<List<DailyMealPlanSummary>>

    /**
     * Crea un plan de comidas completo.
     */
    suspend fun createMealPlan(
        request: CreateMealPlanRequestDto
    ): Result<MealPlanResponseDto>
}