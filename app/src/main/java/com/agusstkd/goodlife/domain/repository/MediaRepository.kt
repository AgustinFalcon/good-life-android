package com.agusstkd.goodlife.domain.repository

import android.net.Uri
import com.agusstkd.goodlife.core.result.Result

/**
 * Repositorio de subida de imágenes.
 * Proxy hacia el backend que a su vez sube a Cloudinary.
 */
interface MediaRepository {
    /**
     * Sube una imagen al backend y retorna la URL pública de Cloudinary.
     *
     * @param imageUri URI local de la imagen seleccionada (galería o cámara)
     * @param context Subcarpeta en Cloudinary: "profile" | "meal" | "ingredient"
     * @return URL pública de la imagen subida
     */
    suspend fun uploadImage(imageUri: Uri, context: String): Result<String>
}
