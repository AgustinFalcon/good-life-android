package com.agusstkd.goodlife.data.remote.api.task

import com.agusstkd.goodlife.data.remote.dto.request.CreateTaskRequest
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.task.TaskResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Endpoints del módulo Task.
 *
 * Separado de otros services siguiendo el Interface Segregation Principle:
 * - [com.agusstkd.goodlife.data.remote.datasource.TaskRemoteDataSource] inyecta
 *   solo los endpoints de tareas.
 * - Cambios en Auth o Daily no afectan la compilación de Task.
 *
 * Todos los endpoints requieren Bearer token (gestionado por
 * [com.agusstkd.goodlife.core.network.GoodLifeInterceptor]).
 *
 * @see CreateTaskRequest
 * @see TaskResponse
 */
interface TaskApiService {

    /**
     * Crea una nueva tarea para el usuario autenticado.
     *
     * Endpoint: POST /api/v1/tasks
     *
     * Regla de negocio del backend: [CreateTaskRequest.scheduledDate] y
     * [CreateTaskRequest.recurrentDays] son mutuamente excluyentes (XOR).
     * Si ambos tienen valor, el backend responde 400 Bad Request.
     *
     * @param request Datos de la tarea a crear.
     * @return [BaseResponse] con [TaskResponse] del objeto creado y su ID asignado.
     */
    @POST("api/v1/tasks")
    suspend fun createTask(
        @Body request: CreateTaskRequest,
    ): BaseResponse<TaskResponse>
}
