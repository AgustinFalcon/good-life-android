package com.agusstkd.goodlife.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Bus de eventos de sesión para comunicación entre capas.
 *
 * Permite que componentes de red (GoodLifeAuthenticator) notifiquen
 * a la capa de presentación (MainActivity) sobre eventos de sesión,
 * sin dependencia directa entre ellos.
 *
 * Uso:
 * - Emitir: SessionEventBus.emit(SessionEvent.SessionExpired)
 * - Escuchar: SessionEventBus.events.collect { ... }
 */
object SessionEventBus {

    private val _events = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun emit(event: SessionEvent) {
        _events.tryEmit(event)
    }

    sealed interface SessionEvent {
        /**
         * El refresh token también expiró — el usuario debe autenticarse de nuevo.
         * Emitido por GoodLifeAuthenticator cuando el retry de refresh falla.
         */
        data object SessionExpired : SessionEvent
    }
}
