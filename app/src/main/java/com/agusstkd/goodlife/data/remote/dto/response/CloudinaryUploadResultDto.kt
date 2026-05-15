package com.agusstkd.goodlife.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * Respuesta del endpoint POST /api/v1/media/upload
 *
 * @property url URL pública segura (https) de la imagen en Cloudinary
 * @property publicId Identificador público en Cloudinary (necesario para eliminar o transformar)
 */
@Serializable
data class CloudinaryUploadResultDto(
    val url: String,
    val publicId: String
)
