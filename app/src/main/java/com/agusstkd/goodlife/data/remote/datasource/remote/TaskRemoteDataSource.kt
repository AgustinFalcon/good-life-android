package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.task.TaskApiService
import com.agusstkd.goodlife.data.remote.dto.request.task.CreateTaskRequest
import com.agusstkd.goodlife.data.remote.dto.response.task.TaskResponse
import com.agusstkd.goodlife.core.result.Result


/**
 * DataSource remoto para operaciones de tareas.
 *
 * Responsabilidades:
 * - Ejecutar llamadas HTTP al backend usando [TaskApiService].
 * - Usar [executeApiCall] para manejo uniforme de errores y conversión a [Result].
 * - Devolver DTOs de red ([TaskResponse]); el mapeo a dominio es del repositorio.
 *
 * @see TaskApiService
 * @see com.agusstkd.goodlife.data.repository.TaskRepositoryImpl
 */
class TaskRemoteDataSource(
    private val taskApiService: TaskApiService,
) {

    /**
     * Crea una nueva tarea en el backend.
     *
     * @param request DTO con los datos de la tarea a crear.
     * @return [Result.Success] con [TaskResponse] o [Result.Error] con la excepción.
     */
    suspend fun createTask(request: CreateTaskRequest): Result<TaskResponse> {
        return executeApiCall { taskApiService.createTask(request) }
    }
}
