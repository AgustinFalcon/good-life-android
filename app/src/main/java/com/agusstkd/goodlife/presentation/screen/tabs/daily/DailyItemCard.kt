package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import com.agusstkd.goodlife.core.datetime.language.DailyTexts
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemHighlight
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemUiModel
import com.agusstkd.goodlife.presentation.theme.BorderDefault
import com.agusstkd.goodlife.presentation.theme.CompletionGreen
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.toDailyItemStyle

// Altura del badge highlight (padding vertical 6dp × 2 + texto ~12dp ≈ 24dp).
// La mitad se usa para desplazar el badge hacia arriba y para reservar
// espacio en el Box, logrando el efecto de badge a caballo del borde.
private val BadgeHalfHeight: Dp = 12.dp

/**
 * Card de un item del Daily Log.
 *
 * Componente puro de presentación (KMP-ready):
 * recibe datos ya formateados del ViewModel y emite callbacks.
 *
 * ## Badge highlight (NEXT UP / IN PROGRESS):
 * El badge flota a caballo del borde superior de la card.
 * Técnica: el [Box] raíz tiene `padding(top = BadgeHalfHeight)` para reservar
 * espacio visible arriba, y el badge usa `offset(y = -BadgeHalfHeight)` para
 * subir hasta cruzar el borde.
 *
 * ## Shapes usados:
 * - Card: 24dp (específico de este diseño, no está en GoodLifeShapes)
 * - Badge tipo: [MaterialTheme.shapes.extraLarge] (50dp pill)
 * - Badge highlight: [MaterialTheme.shapes.medium] (12dp)
 * - Íconos circulares: [CircleShape]
 *
 * ## Colores:
 * Todos los colores de cada tipo vienen de [DailyItemStyle] → [Color.kt].
 * [CompletionGreen] y [BorderDefault] son los únicos colores globales usados directamente.
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
    val hasHighlight = item.highlight != DailyItemHighlight.NONE

    val cardBorder: BorderStroke? = when (item.highlight) {
        DailyItemHighlight.NEXT_UP     -> BorderStroke(2.dp, CompletionGreen)
        DailyItemHighlight.IN_PROGRESS -> BorderStroke(2.dp, style.accentColor)
        DailyItemHighlight.NONE        -> null
    }

    // El padding(top) solo se aplica cuando hay badge, para no alterar
    // el espaciado de las cards sin highlight en el listado.
    Box(modifier = modifier.then(
        if (hasHighlight) Modifier.padding(top = BadgeHalfHeight) else Modifier
    )) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(item.id) },
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = style.background),
            border = cardBorder
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ── Columna izquierda: textos ──────────────────────────────────
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    // Badge de tipo — pill blanca con texto del tipo ("COMIDA", "HÁBITO"…)
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = Color.White
                    ) {
                        Text(
                            text = item.typeLabel.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = style.badgeText
                        )
                    }

                    // Título — con tachado si el item está completado
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = style.titleColor,
                        textDecoration = if (item.status == DailyItemStatus.COMPLETED)
                            TextDecoration.LineThrough else TextDecoration.None
                    )

                    // Descripción — solo si existe
                    item.description?.takeIf { it.isNotBlank() }?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = style.descriptionColor
                        )
                    }

                    // Hora programada — solo si existe (ícono reloj + texto)
                    item.scheduledTime?.let { time ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = style.timeColor
                            )
                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodySmall,
                                color = style.timeColor
                            )
                        }
                    }
                }

                // ── Columna derecha: íconos ────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Ícono decorativo del tipo — círculo con color definido en DailyItemStyle
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = style.iconCircleBackground
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = style.icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = style.accentColor
                            )
                        }
                    }

                    // Botón de status — toggle PENDING ↔ COMPLETED
                    val isCompleted = item.status == DailyItemStatus.COMPLETED
                    val newStatus = if (isCompleted)
                        DailyItemStatus.PENDING else DailyItemStatus.COMPLETED

                    Surface(
                        onClick = { onStatusChange(item.id, newStatus) },
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = if (isCompleted) CompletionGreen else Color.Transparent,
                        border = if (isCompleted) null else BorderStroke(2.dp, BorderDefault)
                    ) {
                        if (isCompleted) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Badge a caballo del borde superior: offset(y = -BadgeHalfHeight) lo sube
        // para que su centro coincida con el borde de la card.
        when (item.highlight) {
            DailyItemHighlight.NEXT_UP -> HighlightBadge(
                text = dailyTexts.nextUp,
                color = CompletionGreen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
                    .offset(y = -BadgeHalfHeight)
            )
            DailyItemHighlight.IN_PROGRESS -> HighlightBadge(
                text = dailyTexts.inProgress,
                color = style.accentColor,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
                    .offset(y = -BadgeHalfHeight)
            )
            DailyItemHighlight.NONE -> Unit
        }
    }
}

/**
 * Badge pequeño que flota en la esquina superior derecha de la card.
 * Usado para "NEXT UP" e "IN PROGRESS".
 *
 * Extraído como función privada para evitar repetir el mismo bloque dos veces.
 * [MaterialTheme.shapes.medium] = 12dp, el shape correcto para badges pequeños.
 */
@Composable
private fun HighlightBadge(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = color
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "Meal - NEXT UP")
@Composable
private fun PreviewMealNextUp() {
    GoodLifeTheme {
        DailyItemCard(
            item = DailyItemUiModel(
                id = 1L, type = DailyItemType.MEAL, typeLabel = "Comida",
                title = "Breakfast", description = "Oatmeal and berries",
                scheduledTime = "08:30 AM", status = DailyItemStatus.PENDING,
                highlight = DailyItemHighlight.NEXT_UP
            ),
            dailyTexts = Spanish.dailyTexts, onClick = {}, onStatusChange = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Habit - Completed")
@Composable
private fun PreviewHabitCompleted() {
    GoodLifeTheme {
        DailyItemCard(
            item = DailyItemUiModel(
                id = 2L, type = DailyItemType.HABIT, typeLabel = "Hábito",
                title = "Drink Water", description = "Start the day hydrated",
                scheduledTime = "07:00 AM", status = DailyItemStatus.COMPLETED,
                highlight = DailyItemHighlight.NONE
            ),
            dailyTexts = Spanish.dailyTexts, onClick = {}, onStatusChange = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Workout - IN PROGRESS")
@Composable
private fun PreviewWorkoutInProgress() {
    GoodLifeTheme {
        DailyItemCard(
            item = DailyItemUiModel(
                id = 3L, type = DailyItemType.WORKOUT, typeLabel = "Entrenamiento",
                title = "Morning Run", description = "5km jog around the park descripcion demsiasdo larga para podre testear como se comporta si pisa los otros iconos o no",
                scheduledTime = "07:30 AM", status = DailyItemStatus.IN_PROGRESS,
                highlight = DailyItemHighlight.IN_PROGRESS
            ),
            dailyTexts = Spanish.dailyTexts, onClick = {}, onStatusChange = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Task - Pending, sin hora")
@Composable
private fun PreviewTaskNoTime() {
    GoodLifeTheme {
        DailyItemCard(
            item = DailyItemUiModel(
                id = 4L, type = DailyItemType.TASK, typeLabel = "Tarea",
                title = "Read Book", description = "Read 10 pages of 'Atomic Habits'",
                scheduledTime = null, status = DailyItemStatus.PENDING,
                highlight = DailyItemHighlight.NONE
            ),
            dailyTexts = Spanish.dailyTexts, onClick = {}, onStatusChange = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}
