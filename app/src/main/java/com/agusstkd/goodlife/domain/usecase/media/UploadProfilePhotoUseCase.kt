package com.agusstkd.goodlife.domain.usecase.media

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.media.LocalImageRef
import com.agusstkd.goodlife.domain.repository.MediaRepository
import kotlinx.coroutines.withContext

/**
 * Sube una foto de perfil a Cloudinary vía el backend.
 *
 * @param mediaRepository Repositorio de subida de imágenes.
 * @param dispatcher Proveedor de dispatchers; la subida corre en IO.
 * @return URL pública de la imagen subida, o error.
 */
class UploadProfilePhotoUseCase(
    private val mediaRepository: MediaRepository,
    private val dispatcher: DispatcherProvider,
) {
    /**
     * Sube la imagen local a la carpeta de perfil.
     *
     * @param image Referencia local a la foto seleccionada.
     * @return URL pública de la imagen subida, o error.
     */
    suspend operator fun invoke(image: LocalImageRef): Result<String> = withContext(dispatcher.io) {
        mediaRepository.uploadImage(image, PROFILE_FOLDER)
    }

    private companion object {
        const val PROFILE_FOLDER = "profile"
    }
}
