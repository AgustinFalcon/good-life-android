package com.agusstkd.goodlife.domain.usecase.routine.result

/**
 * Resultado de [com.agusstkd.goodlife.domain.usecase.routine.CreateRoutineUseCase].
 *
 * Las validaciones son [CreateRoutineValidation]: el UseCase no emite copy.
 * El ViewModel traduce cada caso con [com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts].
 */
sealed interface CreateRoutineResult {

    data class Success(val routineId: Long) : CreateRoutineResult

    data class Invalid(val reason: CreateRoutineValidation) : CreateRoutineResult

    /**
     * Detalle técnico remoto. No debe mostrarse crudo en UI.
     */
    data class ServerError(val message: String) : CreateRoutineResult

    data object NetworkError : CreateRoutineResult
}

/**
 * Motivo de validación local o rechazo 400.
 * Cada caso es un objeto; la etiqueta vive en AppLanguage.
 */
sealed interface CreateRoutineValidation {
    data object EmptyName : CreateRoutineValidation
    data object MissingDifficulty : CreateRoutineValidation
    data object MissingGoal : CreateRoutineValidation
    data object EmptyDays : CreateRoutineValidation
    data object EmptyWorkouts : CreateRoutineValidation
    data class WorkoutWithoutExercises(val workoutName: String) : CreateRoutineValidation
    data class ExerciseWithoutSets(val exerciseName: String) : CreateRoutineValidation
    data object EndBeforeStart : CreateRoutineValidation
    data object RemoteRejected : CreateRoutineValidation
}
