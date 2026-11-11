package com.agusstkd.goodlife.domain.usecase.home.result

import com.agusstkd.goodlife.domain.model.auth.User

/**
 * Resultado de leer el usuario persistido. El ViewModel no inspecciona excepciones.
 */
sealed interface GetCurrentUserResult {
    data class Success(val user: User) : GetCurrentUserResult
    data object Unavailable : GetCurrentUserResult
}
