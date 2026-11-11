package com.agusstkd.goodlife.data.local.session

import kotlinx.coroutines.CancellationException

/**
 * Ejecuta todos los pasos de limpieza y recuerda el primer fallo.
 * Un error en cache Daily no debe saltar el borrado de usuario o tokens.
 */
internal suspend fun runSessionClearSteps(vararg steps: suspend () -> Unit): Exception? {
    var firstError: Exception? = null
    for (step in steps) {
        try {
            step()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (firstError == null) firstError = e
        }
    }
    return firstError
}
