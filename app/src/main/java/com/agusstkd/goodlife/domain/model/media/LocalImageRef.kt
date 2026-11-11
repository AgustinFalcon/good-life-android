package com.agusstkd.goodlife.domain.model.media

/**
 * Referencia a una imagen local seleccionada por el usuario.
 *
 * El valor es el content URI serializado como [String] para que el dominio
 * no dependa de Android (`android.net.Uri`).
 *
 * @property value Content URI de la imagen seleccionada.
 */
data class LocalImageRef(
    val value: String,
)
