package com.agusstkd.goodlife.domain.usecase.home.result

/**
 * Resultado de cerrar sesión. El ViewModel no inspecciona excepciones.
 */
sealed interface LogoutResult {
    data object Success : LogoutResult
    data object Failed : LogoutResult
}
