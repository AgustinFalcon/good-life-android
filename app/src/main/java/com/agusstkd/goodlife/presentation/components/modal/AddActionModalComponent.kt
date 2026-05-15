package com.agusstkd.goodlife.presentation.components.modal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.presentation.components.modal.model.MealOptionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionItem
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionType
import com.agusstkd.goodlife.presentation.theme.DividerColor
import com.agusstkd.goodlife.presentation.theme.LightGreen

/**
 * Modal fullscreen de acciones rápidas.
 *
 * Se muestra al tocar el FAB central con animaciones de fade y slide.
 * Contiene un grid de acciones rápidas y una lista de opciones de comida.
 *
 * @param isVisible Controla la visibilidad del modal
 * @param dateFormatted Fecha formateada para mostrar en el header
 * @param quickActions Lista de acciones rápidas
 * @param mealOptions Lista de opciones de comida
 * @param onDismiss Callback al cerrar el modal
 * @param onQuickActionClick Callback al seleccionar una acción rápida
 * @param onMealOptionClick Callback al seleccionar una opción de comida
 * @param modifier Modificador de Compose
 */
@Composable
fun AddActionModalComponent(
    isVisible: Boolean,
    dateFormatted: String,
    quickActions: List<QuickActionItem>,
    mealOptions: List<MealOptionItem>,
    onDismiss: () -> Unit,
    onQuickActionClick: (QuickActionType) -> Unit,
    onMealOptionClick: (MealType?) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
            ) {
                ModalContent(
                    dateFormatted = dateFormatted,
                    quickActions = quickActions,
                    mealOptions = mealOptions,
                    onQuickActionClick = onQuickActionClick,
                    onMealOptionClick = onMealOptionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { }
                        )
                )
            }
        }
    }
}

@Composable
private fun ModalContent(
    dateFormatted: String,
    quickActions: List<QuickActionItem>,
    mealOptions: List<MealOptionItem>,
    onQuickActionClick: (QuickActionType) -> Unit,
    onMealOptionClick: (MealType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = dateFormatted,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuickActionsGrid(actions = quickActions, onActionClick = onQuickActionClick)

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(color = DividerColor)

            Spacer(modifier = Modifier.height(16.dp))

            MealOptionsList(options = mealOptions, onOptionClick = onMealOptionClick)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickActionsGrid(
    actions: List<QuickActionItem>,
    onActionClick: (QuickActionType) -> Unit
) {
    val rows = actions.chunked(3)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { item ->
                    QuickActionItemView(item = item, onClick = { onActionClick(item.type) })
                }
            }
        }
    }
}

@Composable
private fun QuickActionItemView(
    item: QuickActionItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color = item.backgroundColor.copy(alpha = 0.15f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.contentDescription,
                modifier = Modifier.size(28.dp),
                tint = item.backgroundColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MealOptionsList(
    options: List<MealOptionItem>,
    onOptionClick: (MealType?) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options) { option ->
            MealOptionItemView(item = option, onClick = { onOptionClick(option.mealType) })
        }
    }
}

@Composable
private fun MealOptionItemView(
    item: MealOptionItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            tint = LightGreen
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AddActionModalPreview() {
    AddActionModalComponent(
        isVisible = true,
        dateFormatted = "Hoy, 28 de enero de 2026",
        quickActions = listOf(
            QuickActionItem(QuickActionType.TASK, "Tarea", Icons.Default.FitnessCenter, Color(0xFF4CAF50)),
            QuickActionItem(QuickActionType.HABIT, "Hábito", Icons.Default.LocalDining, Color(0xFF2196F3)),
            QuickActionItem(QuickActionType.WORKOUT, "Rutina", Icons.Default.FitnessCenter, Color(0xFF4CAF50)),
            QuickActionItem(QuickActionType.MEAL, "Comida", Icons.Default.LocalDining, Color(0xFFFF9800)),
            QuickActionItem(QuickActionType.OTHER, "Otros", Icons.Default.MoreHoriz, Color(0xFF607D8B)),
        ),
        mealOptions = listOf(
            MealOptionItem(null, "Resumen diario", Icons.Default.WbSunny),
            MealOptionItem(MealType.BREAKFAST, "Desayuno", Icons.Default.LocalDining),
            MealOptionItem(MealType.LUNCH, "Almuerzo", Icons.Default.Dining),
            MealOptionItem(MealType.DINNER, "Cena", Icons.Default.Dining)
        ),
        onDismiss = {},
        onQuickActionClick = {},
        onMealOptionClick = {}
    )
}
