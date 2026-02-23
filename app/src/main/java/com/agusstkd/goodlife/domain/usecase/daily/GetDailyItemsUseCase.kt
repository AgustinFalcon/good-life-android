package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/**
 * Caso de uso para obtener los items del daily log de una fecha específica.
 *
 * ## Responsabilidades:
 * - Cambiar el dispatcher a IO (no bloquear main thread)
 * - Llamar al repositorio
 * - Devolver Result tal cual (sin mapeo adicional)
 *
 * ## Manejo de errores:
 * El UseCase NO debe interpretar errores ni modificar el Result.
 * El ViewModel es responsable de:
 * - Convertir Result.Error en UiState.Error
 * - Mapear ApiException a mensajes de UI
 * - Mostrar loading/skeleton durante Result.Loading
 */
class GetDailyItemsUseCase(
    private val repository: DailyRepository,
    private val dispatcher: DispatcherProvider
) {

    /**
     * Obtiene el daily log de una fecha.
     *
     * @param date Fecha del daily log
     * @return Result con DailyLog (Domain Model)
     */
    suspend operator fun invoke(date: LocalDate): Result<DailyLog> {
        return withContext(dispatcher.io) {
            repository.getDailyLog(date)
        }
    }
}
