package com.agusstkd.goodlife.domain.usecase.media.result

/**
 * Resultado de subir y persistir la foto de perfil. El ViewModel no inspecciona excepciones.
 */
sealed interface UploadProfilePhotoResult {
    data object Success : UploadProfilePhotoResult
    data object NetworkError : UploadProfilePhotoResult
    data object ServerError : UploadProfilePhotoResult
}
