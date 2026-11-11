package com.agusstkd.goodlife.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.media.MediaApiService
import com.agusstkd.goodlife.domain.model.media.LocalImageRef
import com.agusstkd.goodlife.domain.repository.MediaRepository
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Implementación de [MediaRepository] que lee bytes locales vía ContentResolver
 * y sube el archivo al backend.
 *
 * @param mediaApiService API de subida de imágenes.
 * @param context Contexto para leer el content URI.
 */
class MediaRepositoryImpl(
    private val mediaApiService: MediaApiService,
    private val context: Context
) : MediaRepository {

    override suspend fun uploadImage(image: LocalImageRef, folder: String): Result<String> {
        return try {
            val imageUri = image.value.toUri()
            val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                ?: return Result.Error(Exception("Unable to read image"))

            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val fileName = "upload.${mimeType.substringAfter("/")}"

            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)
            val contextPart = folder.toRequestBody("text/plain".toMediaTypeOrNull())

            when (val result = executeApiCall { mediaApiService.uploadImage(filePart, contextPart) }) {
                is Result.Success -> Result.Success(result.data.url)
                is Result.Error -> result
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
