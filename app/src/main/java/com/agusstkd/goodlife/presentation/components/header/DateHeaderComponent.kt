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
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.presentation.components.GradientIcon
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DegradeBackground5
import com.agusstkd.goodlife.presentation.theme.LightGreen

/**
 * Parámetros del componente DateHeader.
 *
 * @property dayNumber Número del día (1-31) para el icono de calendario
 * @property headerText Texto principal: "Hoy" | "Ayer" | "Mañana" | "Lun, 08 feb"
 * @property monthYear Mes y año completos (ej: "Febrero 2026"), null para fechas relativas
 * @property canNavigatePrevious Si permite navegar al día anterior
 * @property openCalendarLabel Descripción accesibilidad del botón calendario
 * @property previousDayLabel Descripción accesibilidad del botón día anterior
 * @property nextDayLabel Descripción accesibilidad del botón día siguiente
 * @property notificationsLabel Descripción accesibilidad del botón notificaciones
 */
data class DateHeaderParams(
    val dayNumber: Int,
    val headerText: String,
    val monthYear: String?,
    val canNavigatePrevious: Boolean = true,
    val openCalendarLabel: String = "",
    val previousDayLabel: String = "",
    val nextDayLabel: String = "",
    val notificationsLabel: String = ""
)

/**
 * Header con navegación de fechas para tabs como Daily y Meals.
 *
 * Componente **100% puro**: solo renderiza strings ya formateados.
 * No conoce Clock, Language, ni lógica de fechas.
 */
private val ENABLED_GRADIENT_COLORS = listOf(DarkGreen, LightGreen, DegradeBackground5)
private val DISABLED_GRADIENT_COLORS = listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E))

@Composable
fun DateHeaderComponent(
    params: DateHeaderParams,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier,
    onCalendarClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val previousArrowColors = if (params.canNavigatePrevious) ENABLED_GRADIENT_COLORS else DISABLED_GRADIENT_COLORS

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButtonWithBorder(
            icon = Icons.Default.CalendarMonth,
            colors = ENABLED_GRADIENT_COLORS,
            contentDescription = params.openCalendarLabel,
            onClick = onCalendarClick,
            iconSize = 22.dp
        )

        Spacer(modifier = Modifier.width(4.dp))

        IconButtonWithBorder(
            icon = Icons.Default.ArrowBackIosNew,
            colors = previousArrowColors,
            contentDescription = params.previousDayLabel,
            onClick = { if (params.canNavigatePrevious) onPreviousDay() },
            iconSize = 18.dp
        )

        DateHeaderWithIcon(
            dayNumber = params.dayNumber,
            headerText = params.headerText,
            monthYear = params.monthYear,
            modifier = Modifier.weight(1f)
        )

        IconButtonWithBorder(
            icon = Icons.Default.ArrowForwardIos,
            colors = ENABLED_GRADIENT_COLORS,
            contentDescription = params.nextDayLabel,
            onClick = onNextDay,
            iconSize = 18.dp
        )

        Spacer(modifier = Modifier.width(4.dp))

        IconButtonWithBorder(
            icon = Icons.Default.Notifications,
            colors = ENABLED_GRADIENT_COLORS,
            contentDescription = params.notificationsLabel,
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
    val a = Spanish.accessibilityTexts
    DateHeaderComponent(
        params = DateHeaderParams(
            dayNumber = 3, headerText = "Hoy", monthYear = null,
            openCalendarLabel = a.openCalendar, previousDayLabel = a.previousDay,
            nextDayLabel = a.nextDay, notificationsLabel = a.notifications
        ),
        onPreviousDay = {},
        onNextDay = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderYesterdayPreview() {
    val a = Spanish.accessibilityTexts
    DateHeaderComponent(
        params = DateHeaderParams(
            dayNumber = 2, headerText = "Ayer", monthYear = null,
            openCalendarLabel = a.openCalendar, previousDayLabel = a.previousDay,
            nextDayLabel = a.nextDay, notificationsLabel = a.notifications
        ),
        onPreviousDay = {},
        onNextDay = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderOtherDayPreview() {
    val a = Spanish.accessibilityTexts
    DateHeaderComponent(
        params = DateHeaderParams(
            dayNumber = 23, headerText = "Lun, 23 oct", monthYear = "Octubre 2025",
            openCalendarLabel = a.openCalendar, previousDayLabel = a.previousDay,
            nextDayLabel = a.nextDay, notificationsLabel = a.notifications
        ),
        onPreviousDay = {},
        onNextDay = {}
    )
}
