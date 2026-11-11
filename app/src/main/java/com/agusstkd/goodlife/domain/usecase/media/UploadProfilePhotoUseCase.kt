package com.agusstkd.goodlife.domain.usecase.media

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.media.LocalImageRef
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.repository.MediaRepository
import com.agusstkd.goodlife.domain.usecase.media.result.UploadProfilePhotoResult
import java.io.IOException
import kotlinx.coroutines.withContext

/**
 * Sube una foto de perfil y persiste la URL en el usuario local.
 *
 * El ViewModel no habla con repositorios ni inspecciona excepciones.
 */
class UploadProfilePhotoUseCase(
    private val mediaRepository: MediaRepository,
    private val authRepository: AuthRepository,
    private val dispatcher: DispatcherProvider,
) {
    /**
     * Sube la imagen local a la carpeta de perfil y guarda la URL.
     *
     * @param image Referencia local a la foto seleccionada.
     */
    suspend operator fun invoke(image: LocalImageRef): UploadProfilePhotoResult =
        withContext(dispatcher.io) {
            when (val upload = mediaRepository.uploadImage(image, PROFILE_FOLDER)) {
                is Result.Success -> persistUrl(upload.data)
                is Result.Error -> upload.toPhotoResult()
            }
        }

    private suspend fun persistUrl(url: String): UploadProfilePhotoResult =
        when (val persist = authRepository.updateProfileImageUrl(url)) {
            is Result.Success -> UploadProfilePhotoResult.Success
            is Result.Error -> persist.toPhotoResult()
        }

    private fun Result.Error.toPhotoResult(): UploadProfilePhotoResult =
        if (exception is IOException) UploadProfilePhotoResult.NetworkError
        else UploadProfilePhotoResult.ServerError

    private companion object {
        const val PROFILE_FOLDER = "profile"
    }
}
