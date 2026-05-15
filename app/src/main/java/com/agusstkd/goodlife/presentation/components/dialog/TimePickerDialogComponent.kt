package com.agusstkd.goodlife.presentation.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalTime

/**
 * Parámetros de configuración del dialog de selección de hora.
 *
 * @property initialHour Hora inicial del picker (0-23). Default: 9.
 * @property initialMinute Minuto inicial del picker (0-59). Default: 0.
 * @property is24Hour Si es `true`, muestra formato 24h. Default: true.
 * @property confirmLabel Texto del botón de confirmar (ej: "Confirmar").
 * @property cancelLabel Texto del botón de cancelar (ej: "Cancelar").
 */
data class TimePickerDialogParams(
    val initialHour: Int = 9,
    val initialMinute: Int = 0,
    val is24Hour: Boolean = true,
    val confirmLabel: String,
    val cancelLabel: String,
)

/**
 * Dialog modal que envuelve el [TimePicker] de Material3.
 *
 * Reutilizable en cualquier formulario que necesite selección de hora.
 * Devuelve un [LocalTime] (kotlinx.datetime) al confirmar.
 *
 * @param params Configuración del dialog.
 * @param onTimeSelected Callback con la hora seleccionada al confirmar.
 * @param onDismiss Callback al cancelar o cerrar el dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogComponent(
    params: TimePickerDialogParams,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = params.initialHour,
        initialMinute = params.initialMinute,
        is24Hour = params.is24Hour,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime(timePickerState.hour, timePickerState.minute))
                },
            ) {
                Text(params.confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(params.cancelLabel)
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}
