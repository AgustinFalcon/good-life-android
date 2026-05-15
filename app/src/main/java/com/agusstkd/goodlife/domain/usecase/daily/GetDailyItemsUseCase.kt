package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.DailyRepository
import com.agusstkd.goodlife.domain.usecase.daily.result.GetDailyItemsResult
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/**
 * Caso de uso para obtener los items del daily log de una fecha específica.
 *
 * ## Responsabilidades
 * - Cambiar el dispatcher a IO (no bloquear main thread).
 * - Llamar al repositorio.
 * - Convertir el [Result] genérico del repositorio a [GetDailyItemsResult],
 *   traduciendo cada tipo de [ApiException] a un subtipo semántico concreto
 *   para que el ViewModel no necesite conocer detalles de red.
 *
 * ## Por qué la conversión ocurre aquí y no en el ViewModel
 * El ViewModel no debería conocer [ApiException] ni ningún detalle de red.
 * El UseCase es la frontera entre datos técnicos (Result + excepción) y
 * semántica de dominio ([GetDailyItemsResult] con subtipos exhaustivos).
 *
 * ## Mapping de errores
 * | Result.Error causa                  | GetDailyItemsResult         |
 * |-------------------------------------|-----------------------------|
 * | [ApiException.NotFoundException]    | [GetDailyItemsResult.NotFound]     |
 * | [ApiException.ServerException]      | [GetDailyItemsResult.ServerError]  |
 * | IOException / cualquier otro        | [GetDailyItemsResult.NetworkError] |
 */
class GetDailyItemsUseCase(
    private val repository: DailyRepository,
    private val dispatcher: DispatcherProvider
) {

    /**
     * Obtiene el daily log de una fecha.
     *
     * @param date Fecha del daily log a consultar.
     * @return [GetDailyItemsResult] con el subtipo correspondiente al resultado.
     */
    suspend operator fun invoke(date: LocalDate): GetDailyItemsResult {
        return withContext(dispatcher.io) {
            when (val result = repository.getDailyLog(date)) {
                is Result.Success -> GetDailyItemsResult.Success(dailyLog = result.data)
                is Result.Error   -> when (val ex = result.exception) {
                    is ApiException.NotFoundException -> GetDailyItemsResult.NotFound
                    is ApiException.ServerException   -> GetDailyItemsResult.ServerError(
                        message = ex.message ?: "Error de servidor"
                    )
                    else -> GetDailyItemsResult.NetworkError
                }
            }
        }
    }
}
