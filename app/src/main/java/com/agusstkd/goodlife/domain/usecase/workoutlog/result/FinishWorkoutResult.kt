package com.agusstkd.goodlife.domain.usecase.workoutlog.result

/**
 * Resultado de [com.agusstkd.goodlife.domain.usecase.workoutlog.FinishWorkoutUseCase].
 *
 * El UseCase no emite copy. El ViewModel traduce a `AppLanguage`.
 *
 * ## Casos
 * - [Success] — el backend creó el log y devolvió su id.
 * - [EmptyExercises] — validación local: no hay ejercicios para registrar.
 * - [NotFound] — el workout o la rutina no existen (HTTP 404).
 * - [ServerError] — el backend respondió 5xx. `message` es técnico; no va crudo a UI.
 * - [NetworkError] — la request falló antes de llegar al servidor.
 *
 * ## Mapping de errores
 * | Result.Error causa               | FinishWorkoutResult |
 * |----------------------------------|---------------------|
 * | `ApiException.NotFoundException` | [NotFound]          |
 * | `ApiException.ServerException`   | [ServerError]       |
 * | IOException / cualquier otro     | [NetworkError]      |
 */
sealed interface FinishWorkoutResult {

    data class Success(val logId: Long) : FinishWorkoutResult

    data object EmptyExercises : FinishWorkoutResult

    data object NotFound : FinishWorkoutResult

    /**
     * @param message Detalle técnico del servidor; no debe cruzar sin sanitizar a `UiState`.
     */
    data class ServerError(val message: String) : FinishWorkoutResult

    data object NetworkError : FinishWorkoutResult
}
