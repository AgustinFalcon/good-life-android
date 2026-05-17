package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.GetMealPlansResult
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/**
 * Caso de uso: obtiene los planes de comida del usuario para una fecha dada.
 *
 * ## Responsabilidades:
 * - Ejecutar en [DispatcherProvider.io] (decisión arquitectónica #1)
 * - Traducir [Result] del repositorio a [GetMealPlansResult] semántico
 * - Mapear lista vacía → [GetMealPlansResult.NotFound] (no hay plan para ese día)
 *
 * ## Filosofía:
 * El ViewModel NO maneja excepciones. Este UseCase las absorbe y devuelve
 * un subtipo semántico que el ViewModel consume con un when exhaustivo.
 *
 * @param repository Repositorio de planes de comida.
 * @param dispatcher Proveedor de dispatchers de coroutines.
 */
class GetMealPlansUseCase(
    private val repository: MealPlanRepository,
    private val dispatcher: DispatcherProvider,
) {

    suspend operator fun invoke(date: LocalDate): GetMealPlansResult =
        withContext(dispatcher.io) {
            when (val result = repository.getMealPlansByDate(date)) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        GetMealPlansResult.NotFound
                    } else {
                        GetMealPlansResult.Success(result.data)
                    }
                }

                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.NotFoundException -> GetMealPlansResult.NotFound
                    is ApiException.ServerException -> GetMealPlansResult.ServerError(
                        ex.message ?: "Error del servidor"
                    )
                    else -> GetMealPlansResult.NetworkError
                }
            }
        }
}
