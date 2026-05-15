package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextTertiary

/**
 * Parámetros de configuración del selector de fecha.
 *
 * @property displayText Texto de la fecha seleccionada (vacío si no hay selección).
 * @property placeholderText Texto placeholder cuando no hay fecha seleccionada.
 * @property icon Icono del selector (default: calendario).
 */
data class DateSelectorParams(
    val displayText: String,
    val placeholderText: String,
    val icon: ImageVector = Icons.Default.CalendarToday,
)

/**
 * Card clickable que muestra una fecha seleccionada o un placeholder.
 *
 * Se usa como disparador para abrir un [DatePickerBottomSheetComponent] o cualquier
 * otro picker de fecha. No conoce qué picker abre — solo invoca [onClick].
 *
 * Reutilizable en cualquier formulario de creación (Task, Habit, Workout).
 *
 * @param params Configuración visual del selector.
 * @param onClick Callback al tocar la card.
 * @param modifier Modificador de Compose.
 */
@Composable
fun DateSelectorComponent(
    params: DateSelectorParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(params.icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = params.displayText.ifEmpty { params.placeholderText },
                color = if (params.displayText.isEmpty()) TextTertiary else TextPrimary,
            )
        }
    }
}
