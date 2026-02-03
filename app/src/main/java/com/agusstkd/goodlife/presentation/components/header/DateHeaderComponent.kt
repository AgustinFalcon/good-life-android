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
import com.agusstkd.goodlife.core.datetime.isToday
import com.agusstkd.goodlife.core.datetime.isYesterday
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.presentation.components.GradientIcon
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DegradeBackground5
import com.agusstkd.goodlife.presentation.theme.LightGreen
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Header con navegación de fechas para tabs como Diary y Meals.
 *
 * Muestra la fecha actual con navegación día anterior/siguiente,
 * icono de calendario y notificaciones. Soporta textos relativos
 * ("Hoy", "Ayer") según el idioma.
 *
 * @param date Fecha a mostrar
 * @param clock Clock inyectado para testabilidad
 * @param language Idioma para textos y formato
 * @param canNavigatePrevious Si permite navegar al día anterior
 * @param onCalendarClick Callback al abrir calendario
 * @param onPreviousDay Callback al ir al día anterior
 * @param onNextDay Callback al ir al día siguiente
 * @param onNotificationsClick Callback al abrir notificaciones
 * @param modifier Modificador de Compose
 */
@Composable
fun DateHeaderComponent(
    date: LocalDate,
    clock: Clock,
    language: AppLanguage,
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
            date = date,
            clock = clock,
            language = language,
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
    date: LocalDate,
    clock: Clock,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isToday = date.isToday(clock)
    val isYesterday = date.isYesterday(clock)

    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CalendarDayIcon(day = date.dayOfMonth, modifier = Modifier.size(42.dp))

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            if (isToday || isYesterday) {
                Text(
                    text = if (isToday) language.relativeTexts.today else language.relativeTexts.yesterday,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = getDayOfWeekName(date, language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = getMonthYearText(date, language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getDayOfWeekName(date: LocalDate, language: AppLanguage): String {
    val dayIndex = date.dayOfWeek.ordinal
    return language.dayNamesShort.names[dayIndex].replaceFirstChar { it.uppercase() }
}

private fun getMonthYearText(date: LocalDate, language: AppLanguage): String {
    val monthName = language.monthNames.names[date.monthNumber - 1].replaceFirstChar { it.uppercase() }
    return "$monthName ${date.year}"
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderTodayPreview() {
    DateHeaderComponent(
        date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        clock = Clock.System,
        language = AppLanguage.Spanish,
        onPreviousDay = {},
        onNextDay = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DateHeaderOtherDayPreview() {
    DateHeaderComponent(
        date = LocalDate(2025, 10, 23),
        clock = Clock.System,
        language = AppLanguage.Spanish,
        onPreviousDay = {},
        onNextDay = {}
    )
}
