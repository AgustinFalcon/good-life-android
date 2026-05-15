package com.agusstkd.goodlife.data.remote.api.media

import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.CloudinaryUploadResultDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Endpoint de subida de imágenes.
 *
 * El backend actúa como proxy seguro hacia Cloudinary.
 * Las credenciales de Cloudinary nunca llegan al cliente.
 *
 * Endpoint: POST /api/v1/media/upload
 * Content-Type: multipart/form-data
 */
interface MediaApiService {

    @Multipart
    @POST("api/v1/media/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("context") context: RequestBody
    ): BaseResponse<CloudinaryUploadResultDto>
}
