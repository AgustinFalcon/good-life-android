package com.agusstkd.goodlife.data.remote.dto.response.training

import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import kotlinx.serialization.Serializable

/**
 * Wrapper de paginación de Spring Data.
 *
 * El backend devuelve ejercicios paginados con esta estructura:
 * ```json
 * {
 *   "content": [...],
 *   "totalElements": 50,
 *   "totalPages": 3,
 *   "size": 20,
 *   "number": 0,
 *   "first": true,
 *   "last": false
 * }
 * ```
 *
 * Este DTO lo parsea y [toDomain] lo convierte al genérico [PageResult].
 */
@Serializable
data class ExercisePageResponse(
    val content: List<ExerciseMasterResponse> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 20,
    val number: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
)

fun ExercisePageResponse.toDomain(): PageResult<ExerciseMaster> = PageResult(
    items = content.map { it.toDomain() },
    page = number,
    pageSize = size,
    totalItems = totalElements,
    isLastPage = last,
)
