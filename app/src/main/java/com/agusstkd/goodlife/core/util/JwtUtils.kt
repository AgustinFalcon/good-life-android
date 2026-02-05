package com.agusstkd.goodlife.core.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Utilidades para decodificar JWT.
 *
 * Usa solo Kotlin stdlib para mantener compatibilidad con KMP.
 * NO usa android.util.Base64.
 *
 * Estructura JWT: header.payload.signature
 * - Header: Algoritmo y tipo de token
 * - Payload: Claims (sub, iat, exp, scope, etc.)
 * - Signature: Firma para verificación
 *
 * Ejemplo payload decodificado:
 * {"iss":"GoodLife-backend-v2","sub":"2","exp":1767934703,"iat":1767932903,"scope":"USER"}
 */
object JwtUtils {

    /**
     * Extrae el subject (userId) del token JWT.
     *
     * @param token Token JWT completo
     * @return El valor del claim "sub" o null si no se puede extraer
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun extractSubject(token: String): String? {
        return try {
            val payload = decodePayload(token) ?: return null
            extractClaim(payload, "sub")
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extrae el scope (rol) del token JWT.
     *
     * @param token Token JWT completo
     * @return El valor del claim "scope" o null si no se puede extraer
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun extractScope(token: String): String? {
        return try {
            val payload = decodePayload(token) ?: return null
            extractClaim(payload, "scope")
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verifica si el token está expirado basándose en el claim "exp".
     *
     * @param token Token JWT completo
     * @return true si está expirado, false si es válido, null si no se puede determinar
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun isExpired(token: String): Boolean? {
        return try {
            val payload = decodePayload(token) ?: return null
            val expStr = extractClaim(payload, "exp") ?: return null
            val expSeconds = expStr.toLongOrNull() ?: return null
            val expMillis = expSeconds * 1000
            System.currentTimeMillis() > expMillis
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Decodifica el payload del JWT (segunda parte).
     *
     * @param token Token JWT completo
     * @return Payload decodificado como string JSON o null si falla
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun decodePayload(token: String): String? {
        val parts = token.split(".")
        if (parts.size != 3) return null

        val payloadBase64 = parts[1]
        // JWT usa Base64 URL-safe, necesitamos reemplazar caracteres
        val base64Standard = payloadBase64
            .replace('-', '+')
            .replace('_', '/')

        // Agregar padding si es necesario
        val padded = when (base64Standard.length % 4) {
            2 -> "$base64Standard=="
            3 -> "$base64Standard="
            else -> base64Standard
        }

        return try {
            val decoded = Base64.Default.decode(padded)
            decoded.decodeToString()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extrae un claim específico del payload JSON.
     *
     * Implementación simple sin librería JSON (para KMP compatibility).
     *
     * @param payload Payload JSON decodificado
     * @param claimName Nombre del claim a extraer
     * @return Valor del claim o null si no existe
     */
    private fun extractClaim(payload: String, claimName: String): String? {
        // Buscar pattern: "claimName":"value" o "claimName":value
        val patterns = listOf(
            """"$claimName":"([^"]+)"""".toRegex(),  // String value
            """"$claimName":(\d+)""".toRegex()        // Numeric value
        )

        for (pattern in patterns) {
            val match = pattern.find(payload)
            if (match != null) {
                return match.groupValues.getOrNull(1)
            }
        }
        return null
    }
}
