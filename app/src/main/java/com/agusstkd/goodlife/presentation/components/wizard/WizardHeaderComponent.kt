package com.agusstkd.goodlife.presentation.components.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import com.agusstkd.goodlife.presentation.theme.WorkoutBackground

/**
 * Header reutilizable para wizards de creación de items.
 *
 * Muestra:
 * - Fila superior: botón Atrás (opcional) + título + badge de tipo + botón Cerrar.
 * - Fila inferior: label "Paso X de Y" + barra de progreso lineal.
 *
 * Usado por [CreateRoutineScreen] y [CreateMealPlanScreen]. Parametrizar
 * [accentColor], [badgeContainerColor] y [badgeContentColor] para mantener
 * la identidad visual de cada entidad (verde/workout, naranja/nutrition).
 *
 * @param title Título principal del wizard (ej: "Nueva rutina").
 * @param badgeText Texto del badge de tipo (ej: "RUTINA", "COMIDA").
 * @param badgeContainerColor Color de fondo del badge.
 * @param badgeContentColor Color del texto del badge.
 * @param stepLabel Texto del indicador de paso (ej: "Paso 1 de 3").
 * @param progress Progreso entre 0f y 1f para la barra lineal.
 * @param accentColor Color del step label y de la barra de progreso.
 * @param onBack Callback del botón Atrás. Si es null, el botón no se muestra (primer paso).
 * @param onClose Callback del botón Cerrar (X).
 * @param closeContentDescription Content description del botón Cerrar para accesibilidad.
 * @param modifier Modificador externo.
 */
@Composable
fun WizardHeaderComponent(
    title: String,
    badgeText: String,
    badgeContainerColor: Color,
    badgeContentColor: Color,
    stepLabel: String,
    progress: Float,
    accentColor: Color,
    onBack: (() -> Unit)?,
    onClose: () -> Unit,
    closeContentDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ── Fila: Atrás | Título | Badge | Cerrar ────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )

            Badge(
                containerColor = badgeContainerColor,
                contentColor = badgeContentColor,
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = closeContentDescription,
                )
            }
        }

        // ── Indicador de paso + barra de progreso ────────────────────────
        Text(
            text = stepLabel,
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
        )

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = accentColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "WizardHeader — Workout paso 2", showBackground = true)
@Composable
private fun PreviewWorkoutHeader() {
    GoodLifeTheme {
        WizardHeaderComponent(
            title = "Nueva rutina",
            badgeText = "RUTINA",
            badgeContainerColor = WorkoutBackground,
            badgeContentColor = WorkoutAccent,
            stepLabel = "Paso 2 de 4",
            progress = 0.5f,
            accentColor = WorkoutAccent,
            onBack = {},
            onClose = {},
            closeContentDescription = "Cerrar",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "WizardHeader — Meal paso 1 (sin atrás)", showBackground = true)
@Composable
private fun PreviewMealHeader() {
    GoodLifeTheme {
        WizardHeaderComponent(
            title = "Nueva comida",
            badgeText = "COMIDA",
            badgeContainerColor = NutritionAccent.copy(alpha = 0.15f),
            badgeContentColor = NutritionAccent,
            stepLabel = "Paso 1 de 3",
            progress = 0.33f,
            accentColor = NutritionAccent,
            onBack = null,
            onClose = {},
            closeContentDescription = "Cerrar",
            modifier = Modifier.padding(16.dp),
        )
    }
}
