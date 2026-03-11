package com.agusstkd.goodlife.core.pagination

/**
 * Resultado paginado genérico.
 *
 * Representa una página de datos proveniente de cualquier endpoint paginado
 * (ejercicios, comidas, notificaciones, etc.).
 *
 * @param T Tipo del item contenido en la página.
 * @property items Elementos de la página actual.
 * @property page Número de página (0-indexed).
 * @property pageSize Cantidad de items solicitados por página.
 * @property totalItems Total de items existentes en el backend.
 * @property isLastPage Si esta es la última página disponible.
 */
data class PageResult<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Long,
    val isLastPage: Boolean,
)
