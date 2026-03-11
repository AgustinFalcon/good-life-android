package com.agusstkd.goodlife.data.remote.api.auth

import com.agusstkd.goodlife.data.remote.dto.request.auth.RegisterRequest
import com.agusstkd.goodlife.data.remote.dto.response.AuthResponse
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Endpoints de autenticación.
 *
 * Separado de otros services para que:
 * - Cada DataSource inyecte solo los endpoints que usa.
 * - Un cambio en los endpoints de Meals no recompile el módulo de Auth.
 * - El [com.agusstkd.goodlife.core.network.GoodLifeAuthenticator] inyecte
 *   solo esta interface (la única que necesita para el refresh).
 */
interface AuthApiService {

    /**
     * Login con credenciales.
     *
     * Endpoint: POST /api/v1/token
     * Content-Type: application/x-www-form-urlencoded
     *
     * @param grantType Tipo de grant ("password" para login)
     * @param username Nombre de usuario o email
     * @param password Contraseña del usuario
     * @return [BaseResponse] con [AuthResponse] (accessToken + refreshToken)
     */
    @FormUrlEncoded
    @POST("api/v1/token")
    suspend fun login(
        @Field("grantType") grantType: String = "password",
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<AuthResponse>

    /**
     * Refresh del access token.
     *
     * Endpoint: POST /api/v1/token
     * Content-Type: application/x-www-form-urlencoded
     *
     * @param grantType Tipo de grant ("refreshToken")
     * @param refreshToken Token de refresco válido
     * @return [BaseResponse] con [AuthResponse] (nuevo accessToken)
     */
    @FormUrlEncoded
    @POST("api/v1/token")
    suspend fun refreshToken(
        @Field("grantType") grantType: String = "refreshToken",
        @Field("refreshToken") refreshToken: String
    ): BaseResponse<AuthResponse>

    /**
     * Registro de nuevo usuario.
     *
     * Endpoint: POST /api/v1/register
     * Content-Type: application/json
     *
     * @param request Datos del nuevo usuario
     * @return [BaseResponse] con [RegisterResponse] (datos del usuario creado)
     */
    @POST("api/v1/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): BaseResponse<RegisterResponse>
}
