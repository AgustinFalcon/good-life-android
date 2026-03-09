package com.agusstkd.goodlife.core.extensions

/**
 * Reemplaza placeholders {0}, {1}, ... con los valores proporcionados.
 *
 * Uso:
 * ```kotlin
 * "Completaste {0} de {1} tareas".formatArgs(3, 5)
 * // → "Completaste 3 de 5 tareas"
 * ```
 */
fun String.formatArgs(vararg args: Any): String {
    var result = this
    args.forEachIndexed { index, value ->
        result = result.replace("{$index}", value.toString())
    }
    return result
}
