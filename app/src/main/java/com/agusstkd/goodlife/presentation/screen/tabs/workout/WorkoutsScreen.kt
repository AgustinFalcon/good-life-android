package com.agusstkd.goodlife.presentation.screen.tabs.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiState
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.WorkoutsTabAccent

/**
 * Pantalla Workouts (tab de rutinas y entrenamientos).
 *
 * UI pura: solo renderiza, sin lógica de negocio.
 * Recibe el [WorkoutsUiState.Success] y los callbacks de [WorkoutsUiAction] desde el Owner.
 *
 * ## Estructura visual:
 * - Card de resumen de la rutina activa (nombre, dificultad, objetivo)
 * - Lista de workouts con ejercicios y series
 */
@Composable
fun WorkoutsScreen(
    uiState: WorkoutsUiState.Success,
    onAction: (WorkoutsUiAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            ActiveRoutineCard(uiState = uiState)
        }

        items(
            items = uiState.workouts,
            key = { it.id }
        ) { workout ->
            WorkoutCard(
                workout = workout,
                onClick = { onAction(WorkoutsUiAction.OnWorkoutClick(workout.id)) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        if (uiState.workouts.isEmpty()) {
            item {
                Text(
                    text = "Sin workouts en esta rutina",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * Card con el resumen de la rutina activa.
 *
 * Muestra nombre, descripción opcional, dificultad y objetivo.
 * Usa [WorkoutBackground] y [WorkoutsTabAccent] como colores del tab de Workouts.
 */
@Composable
private fun ActiveRoutineCard(
    uiState: WorkoutsUiState.Success
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.routineName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = uiState.difficultyLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = WorkoutsTabAccent
                )
            }

            uiState.routineDescription?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = uiState.goalLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Card de un workout individual de la rutina.
 *
 * Muestra nombre, cantidad de ejercicios y series totales.
 */
@Composable
private fun WorkoutCard(
    workout: WorkoutUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${workout.exerciseCount} ejercicios • ${workout.totalSets} series",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            workout.dayOfWeek?.let { day ->
                Text(
                    text = "Día $day",
                    style = MaterialTheme.typography.labelSmall,
                    color = WorkoutsTabAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "WorkoutsScreen - con rutina activa")
@Composable
private fun WorkoutsScreenPreview() {
    GoodLifeTheme {
        WorkoutsScreen(
            uiState = WorkoutsUiState.Success(
                routineName = "Rutina Fuerza 4x",
                routineDescription = "Programa de fuerza para ganar masa muscular",
                difficultyLabel = "Intermedio",
                goalLabel = "Ganancia muscular",
                daysOfWeek = emptySet(),
                workouts = listOf(
                    WorkoutUiModel(
                        id = 1L,
                        name = "Pecho + Tríceps",
                        dayOfWeek = 1,
                        exerciseCount = 5,
                        totalSets = 20,
                    ),
                    WorkoutUiModel(
                        id = 2L,
                        name = "Espalda + Bíceps",
                        dayOfWeek = 3,
                        exerciseCount = 6,
                        totalSets = 24,
                    ),
                    WorkoutUiModel(
                        id = 3L,
                        name = "Piernas",
                        dayOfWeek = 5,
                        exerciseCount = 4,
                        totalSets = 16,
                    ),
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "WorkoutsScreen - sin workouts")
@Composable
private fun WorkoutsScreenEmptyPreview() {
    GoodLifeTheme {
        WorkoutsScreen(
            uiState = WorkoutsUiState.Success(
                routineName = "Mi primera rutina",
                routineDescription = null,
                difficultyLabel = "Principiante",
                goalLabel = "Flexibilidad",
                daysOfWeek = emptySet(),
                workouts = emptyList(),
            ),
            onAction = {}
        )
    }
}
