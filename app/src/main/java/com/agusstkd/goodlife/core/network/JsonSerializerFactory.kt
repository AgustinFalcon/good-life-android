package com.agusstkd.goodlife.core.network

import kotlinx.serialization.json.Json

/**
 * Factory para crear la instancia de Kotlinx Serialization Json.
 *
 * Configuración optimizada para consumo de APIs REST:
 * - ignoreUnknownKeys: Ignora campos no mapeados en DTOs
 * - isLenient: Más permisivo con el parsing (ej: comillas)
 * - coerceInputValues: Usa valores por defecto si recibe null
 * - encodeDefaults: Incluye valores por defecto al serializar
 *
 * @see [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
 */
object JsonSerializerFactory {

    /**
     * Crea una instancia configurada de Json.
     *
     * @return Instancia de Json lista para usar con Retrofit.
     */
    fun create(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }
}
