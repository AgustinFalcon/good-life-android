package com.agusstkd.goodlife.data.repository

import android.content.Context
import android.net.Uri
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.media.MediaApiService
import com.agusstkd.goodlife.domain.repository.MediaRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MediaRepositoryImpl(
    private val mediaApiService: MediaApiService,
    private val context: Context
) : MediaRepository {

    override suspend fun uploadImage(imageUri: Uri, folder: String): Result<String> {
        return try {
            val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                ?: return Result.Error(Exception("No se pudo leer el archivo"))

            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val fileName = "upload.${mimeType.substringAfter("/")}"

            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)
            val contextPart = folder.toRequestBody("text/plain".toMediaTypeOrNull())

            when (val result = executeApiCall { mediaApiService.uploadImage(filePart, contextPart) }) {
                is Result.Success -> Result.Success(result.data.url)
                is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
