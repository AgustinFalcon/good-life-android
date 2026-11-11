package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.media.LocalImageRef

/**
 * Repositorio de subida de imágenes.
 *
 * Proxy hacia el backend que a su vez sube a Cloudinary.
 */
interface MediaRepository {
    /**
     * Sube una imagen al backend y retorna la URL pública de Cloudinary.
     *
     * @param image Referencia local a la imagen seleccionada (galería o cámara).
     * @param folder Subcarpeta en Cloudinary: `"profile"` | `"meal"` | `"ingredient"`.
     * @return URL pública de la imagen subida.
     */
    suspend fun uploadImage(image: LocalImageRef, folder: String): Result<String>
}
