package com.agusstkd.goodlife.data.remote.dto.response

import com.agusstkd.goodlife.core.pagination.PageResult
import kotlinx.serialization.Serializable

/**
 * Wrapper genérico de paginación de Spring Data.
 *
 * El backend (Spring Boot) devuelve cualquier endpoint paginado con esta estructura:
 * ```json
 * {
 *   "content": [...],           // Lista de items del tipo T
 *   "totalElements": 50,        // Total items en la base de datos
 *   "totalPages": 3,            // Total páginas calculado
 *   "size": 20,                 // Cantidad solicitada por página
 *   "number": 0,                // Número de página actual (0-indexed)
 *   "first": true,              // Si es la primera página
 *   "last": false               // Si es la última página
 * }
 * ```
 *
 * Este DTO genérico lo parsea y [toDomain] lo convierte al [PageResult] de dominio.
 *
 * @param T Tipo del DTO de response (ej: IngredientResponseDto, ExerciseMasterResponse)
 */
@Serializable
data class  PageResponseDto<T>(
    val content: List<T> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 20,
    val number: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
)

/**
 * Convierte PageResponseDto<T> a PageResult<R> aplicando mapper a cada item.
 *
 * @param mapper Función que transforma DTO → Domain model (ej: dto.toIngredient())
 * @return PageResult con items transformados al dominio.
 */
fun <T, R> PageResponseDto<T>.toDomain(
    mapper: (T) -> R
): PageResult<R> = PageResult(
    items = content.map(mapper),
    page = number,
    pageSize = size,
    totalItems = totalElements,
    isLastPage = last,
)
