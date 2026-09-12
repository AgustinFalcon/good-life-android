package com.agusstkd.goodlife.domain.usecase.routine.result

import com.agusstkd.goodlife.domain.model.training.Routine

sealed interface GetActiveRoutineResult {
    data class Success(val routine: Routine) : GetActiveRoutineResult
    data object NotFound : GetActiveRoutineResult

    /**
     * El mensaje es detalle técnico remoto y nunca debe cruzar sin sanitizar a UI.
     */
    data class ServerError(val message: String) : GetActiveRoutineResult

    data object NetworkError : GetActiveRoutineResult
}
