package com.agusstkd.goodlife.presentation.components.modal.model

/**
 * Tipos de acciones rápidas en el modal del FAB.
 *
 * Cada tipo corresponde a un flujo de creación de un item
 * del Daily Log. Al seleccionar uno, se navega a la pantalla
 * de creación correspondiente.
 *
 * @see com.agusstkd.goodlife.presentation.components.modal.AddActionModalComponent
 */
enum class QuickActionType {
    /** Crear nueva tarea (puntual o recurrente) */
    TASK,
    /** Crear nuevo hábito */
    HABIT,
    /** Crear nueva rutina de ejercicios */
    WORKOUT,
    /** Registrar comida / alimentación */
    MEAL,
    /** Otras acciones */
    OTHER,
}
