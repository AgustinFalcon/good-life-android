package com.agusstkd.goodlife.presentation.components.header

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.components.GradientIcon
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DegradeBackground5
import com.agusstkd.goodlife.presentation.theme.LightGreen

/**
 * Header con navegación de fechas para tabs como Daily y Meals.
 *
 * ## Filosofía:
 * Componente **100% puro**: solo renderiza strings ya formateados.
 * No conoce Clock, Language, ni lógica de fechas.
 *
 * ## Uso:
 * ```kotlin
 * DateHeaderComponent(
 *     dayNumber = 15,
 *     headerText = "Hoy",           // o "Lun, 15 feb"
 *     monthYear = null,              // o "Febrero 2026"
 *     onPreviousDay = {},
 *     onNextDay = {}
 * )
 * ```
 *
 * @param dayNumber Número del día (1-31) para el icono de calendario
 * @param headerText Texto principal: "Hoy" | "Ayer" | "Mañana" | "Lun, 08 feb"
 * @param monthYear Mes y año completos (ej: "Febrero 2026"), null para fechas relativas
 * @param canNavigatePrevious Si permite navegar al día anterior
 * @param onCalendarClick Callback al abrir calendario
 * @param onPreviousDay Callback al ir al día anterior
 * @param onNextDay Callback al ir al día siguiente
 * @param onNotificationsClick Callback al abrir notificaciones
 * @param modifier Modificador de Compose
 */
@Composable
fun DateHeaderComponent(
    dayNumber: Int,
    headerText: String,
    monthYear: String?,
    canNavigatePrevious: Boolean = true,
    onCalendarClick: () -> Unit = {},
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val enabledColors = listOf(DarkGreen, LightGreen, DegradeBackground5)
    val disabledColors = listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E))
    val previousArrowColors = if (canNavigatePrevious) enabledColors else disabledColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonWithBorder(
            icon = Icons.Default.CalendarMonth,
            colors = enabledColors,
            contentDescription = "Abrir calendario",
            onClick = onCalendarClick,
            iconSize = 22.dp
        )

        Spacer(modifier = Modifier.width(4.dp))

        IconButtonWithBorder(
            icon = Icons.Default.ArrowBackIosNew,
            colors = previousArrowColors,
            contentDescription = "Día anterior",
            onClick = { if (canNavigatePrevious) onPreviousDay() },
            iconSize = 18.dp
        )

        DateHeaderWithIcon(
            dayNumber = dayNumber,
            headerText = headerText,
            monthYear = monthYear,
            modifier = Modifier.weight(1f)
        )

        IconButtonWithBorder(
            icon = Icons.Default.ArrowForwardIos,
            colors = enabledColors,
            contentDescription = "Día siguiente",
            onClick = onNextDay,
            iconSize = 18.dp
        )

        Spacer(modifier = Modifier.width(4.dp))

        IconButtonWithBorder(
            icon = Icons.Default.Notifications,
            colors = enabledColors,
            contentDescription = "Notificaciones",
            onClick = onNotificationsClick,
            iconSize = 18.dp
        )
    }
}

@Composable
private fun IconButtonWithBorder(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: List<Color>,
    contentDescription: String,
    onClick: () -> Unit,
    iconSize: Dp
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(colors = colors),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        GradientIcon(
            imageVector = icon,
            colors = colors,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun DateHeaderWithIcon(
    dayNumber: Int,
    headerText: String,
    monthYear: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CalendarDayIcon(day = dayNumber, modifier = Modifier.size(42.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = headerText,
                style = if (monthYear == null) {
                    // Fecha relativa (Hoy, Ayer, Mañana) → texto grande
                    MaterialTheme.typography.titleLarge
                } else {
                    // Fecha absoluta (Lun, 08 feb) → texto mediano
                    MaterialTheme.typography.titleMedium
                },
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Mostrar mes/año solo si NO es fecha relativa
            if (monthYear != null) {
                Text(
                    text = monthYear,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderTodayPreview() {
    DateHeaderComponent(
        dayNumber = 3,
        headerText = "Hoy",
        monthYear = null,  // No mostrar mes/año para fechas relativas
        onPreviousDay = {},
        onNextDay = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderYesterdayPreview() {
    DateHeaderComponent(
        dayNumber = 2,
        headerText = "Ayer",
        monthYear = null,
        onPreviousDay = {},
        onNextDay = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderOtherDayPreview() {
    DateHeaderComponent(
        dayNumber = 23,
        headerText = "Lun, 23 oct",
        monthYear = "Octubre 2025",
        onPreviousDay = {},
        onNextDay = {}
    )
}
