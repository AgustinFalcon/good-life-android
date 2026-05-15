package com.agusstkd.goodlife.domain.usecase.media

import android.net.Uri
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.MediaRepository

/**
 * Sube una foto de perfil a Cloudinary vía el backend.
 *
 * @return URL pública de la imagen subida, o error descriptivo
 */
class UploadProfilePhotoUseCase(private val mediaRepository: MediaRepository) {
    suspend operator fun invoke(imageUri: Uri): Result<String> =
        mediaRepository.uploadImage(imageUri, "profile")
}
