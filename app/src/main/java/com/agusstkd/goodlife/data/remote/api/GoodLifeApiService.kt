package com.agusstkd.goodlife.data.remote.api

import com.agusstkd.goodlife.data.remote.dto.request.RegisterRequest
import com.agusstkd.goodlife.data.remote.dto.response.AuthResponse
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.RegisterResponse
import com.agusstkd.goodlife.data.remote.dto.response.daily.DailyLogDto
import com.agusstkd.goodlife.data.remote.dto.response.daily.DailyLogResponse
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Service principal de GoodLife.
 *
 * Contiene todos los endpoints del backend.
 * Actualmente implementados:
 * - Auth: login, refresh, register
 *
 * TODO: Agregar endpoints de:
 * - Tasks
 * - Habits
 * - Routines
 * - Meals
 * - Daily Log
 * - Gamification
 */
interface GoodLifeApiService {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // AUTH ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Login con credenciales.
     *
     * Endpoint: POST /api/v1/token
     * Content-Type: application/x-www-form-urlencoded
     *
     * @param grantType Tipo de grant ("password" para login)
     * @param username Nombre de usuario o email
     * @param password Contraseña del usuario
     * @return BaseResponse con AuthResponse (accessToken + refreshToken)
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
     * @return BaseResponse con AuthResponse (solo accessToken)
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
     * @return BaseResponse con RegisterResponse (datos del usuario creado)
     */
    @POST("api/v1/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): BaseResponse<RegisterResponse>



    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DAILY ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene el daily log de una fecha específica.
     *
     * Endpoint: GET /api/v1/daily-logs/{date}
     * Formato de fecha: ISO-8601 (YYYY-MM-DD), ej: "2026-02-05"
     *
     * El backend genera automáticamente el daily log si no existe,
     * incluyendo todos los items (tasks, habits, workouts, meals) programados para ese día.
     *
     * @param date Fecha en formato ISO-8601 ("2026-02-05")
     * @return BaseResponse con DailyLogResponse (log + items)
     */
    @GET("api/v1/daily-logs/{date}")
    suspend fun getDailyLogByDate(
        @Path("date") date: String
    ): BaseResponse<DailyLogResponse>

    /**
     * Actualiza el status de un item del daily log.
     *
     * Endpoint: PATCH /api/v1/daily-logs/items/{itemId}/status
     * Query param: status (COMPLETED, SKIPPED, PENDING)
     *
     * El backend recalcula automáticamente el completionRate del daily log.
     *
     * @param itemId ID del item a actualizar
     * @param status Nuevo status del item
     * @return BaseResponse con DailyLogResponse actualizado (incluye nuevo completionRate)
     */
    @PATCH("api/v1/daily-logs/items/{itemId}/status")
    suspend fun updateItemStatus(
        @Path("itemId") itemId: Long,
        @Query("status") status: String
    ): BaseResponse<DailyLogResponse>


    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TODO: TASKS ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TODO: HABITS ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TODO: ROUTINES ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TODO: MEALS ENDPOINTS
    // ═══════════════════════════════════════════════════════════════════════════════════════════


}
