package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.DailyTexts
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemUiModel
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.toDailyItemStyle

/**
 * Card de un item dentro del listado Daily.
 *
 * Componente puro de presentación (KMP Ready):
 * recibe datos listos para renderizar, emite callbacks.
 */
@Composable
fun DailyItemCard(
    item: DailyItemUiModel,
    dailyTexts: DailyTexts,
    onClick: (Long) -> Unit,
    onStatusChange: (Long, DailyItemStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val style = item.type.toDailyItemStyle()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(item.id) },
        colors = CardDefaults.cardColors(containerColor = style.background)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Categoria
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = item.typeLabel,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    //containerColor = style.badgeBackground,
                    containerColor = Color.White,
                    labelColor = style.badgeText
                ),
                modifier = Modifier.height(24.dp),
                border = null,
                shape = RoundedCornerShape(50.dp)
            )

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = style.titleColor
            )

            item.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = style.descriptionColor
                )
            }

            item.scheduledTime?.let { scheduled ->
                Text(
                    text = "${dailyTexts.schedulePrefix}: $scheduled",
                    style = MaterialTheme.typography.bodySmall,
                    color = style.timeColor
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "DailyItemCard - Task")
@Composable
private fun DailyItemCardTaskPreview() {
    GoodLifeTheme {
        DailyItemCard(
            item = DailyItemUiModel(
                id = 1L,
                type = DailyItemType.TASK,
                typeLabel = "Tarea",
                title = "Leer 20 páginas",
                description = "Capítulo 4 de Clean Architecture",
                scheduledTime = "08:30",
                status = DailyItemStatus.PENDING
            ),
            dailyTexts = AppLanguage.Spanish.dailyTexts,
            onClick = {},
            onStatusChange = { _, _ -> },
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true, name = "DailyItemCard - Workout")
@Composable
private fun DailyItemCardWorkoutPreview() {
    GoodLifeTheme {
        DailyItemCard(
            item = DailyItemUiModel(
                id = 2L,
                type = DailyItemType.WORKOUT,
                typeLabel = "Entrenamiento",
                title = "Espalda y bíceps",
                description = "Rutina fuerza • 6 ejercicios",
                scheduledTime = "18:00",
                status = DailyItemStatus.COMPLETED
            ),
            dailyTexts = AppLanguage.Spanish.dailyTexts,
            onClick = {},
            onStatusChange = { _, _ -> },
            modifier = Modifier.padding(12.dp)
        )
    }
}
