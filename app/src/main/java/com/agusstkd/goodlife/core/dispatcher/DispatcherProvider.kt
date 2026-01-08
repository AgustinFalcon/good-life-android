package com.agusstkd.goodlife.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Abstracción de dispatchers de coroutines.
 *
 * Permite inyectar dispatchers personalizados para testing
 * y preparar la aplicación para Kotlin Multiplatform.
 *
 * @see AndroidDispatcherProvider
 */
interface DispatcherProvider {

    /** Dispatcher para operaciones de UI (Main thread). */
    val main: CoroutineDispatcher

    /** Dispatcher para operaciones de IO (red, base de datos, archivos). */
    val io: CoroutineDispatcher

    /** Dispatcher para operaciones CPU-intensivas (cálculos, parsing). */
    val default: CoroutineDispatcher
}

/**
 * Implementación de [DispatcherProvider] para Android.
 *
 * Utiliza los dispatchers estándar de kotlinx.coroutines.
 */
class AndroidDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}

