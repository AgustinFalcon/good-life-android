package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.ButtonColorsDisabled
import com.agusstkd.goodlife.presentation.theme.ButtonTextStyle
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DegradeBackground5
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.GreenSelected
import com.agusstkd.goodlife.presentation.theme.HabitAccent
import com.agusstkd.goodlife.presentation.theme.HabitAccentLight
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.NutritionAccentLight

import com.agusstkd.goodlife.presentation.theme.TaskAccent
import com.agusstkd.goodlife.presentation.theme.TaskAccentLight
import com.agusstkd.goodlife.presentation.theme.TextOnPrimary
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import com.agusstkd.goodlife.presentation.theme.WorkoutAccentLight

/**
 * Variante visual del botón.
 */
enum class ButtonVariant {
    /** Fondo con gradiente */
    PRIMARY,
    /** Borde con gradiente, fondo blanco */
    OUTLINE,
    /** Borde sólido sutil con ícono líder — para acciones de creación */
    CREATE,
}

/**
 * Esquema de colores del botón según el módulo o feature.
 *
 * @property primaryGradientColors Degradado del fondo en variante PRIMARY.
 * @property outlineBorderColors Degradado del borde en variante OUTLINE.
 *   Por defecto es igual a [primaryGradientColors] — solo [Default] los diferencia
 *   para preservar el comportamiento original de la app.
 * @property outlineContentColor Color del texto en variante OUTLINE. PRIMARY siempre usa blanco.
 */
data class ButtonColorScheme(
    val primaryGradientColors: List<Color>,
    val outlineBorderColors: List<Color> = primaryGradientColors,
    val outlineContentColor: Color,
) {
    companion object {
        /**
         * Comportamiento original: PRIMARY usa [AuthButtonColors], OUTLINE usa [ButtonColorsEnabled].
         * Usado en Auth screens y cualquier pantalla sin feature-color específico.
         */
        val Default = ButtonColorScheme(
            primaryGradientColors = listOf(LightGreen, GreenSelected),
            outlineBorderColors = listOf(DarkGreen, LightGreen, DegradeBackground5),
            outlineContentColor = DarkGreen,
        )

        /** Verde — módulo de Hábitos. Usa [HabitAccentLight] → [HabitAccent]. */
        val Habit = ButtonColorScheme(
            primaryGradientColors = listOf(HabitAccentLight, HabitAccent),
            outlineContentColor = HabitAccent,
        )

        /** Naranja — módulo de Nutrición / Meal Plans. Usa [NutritionAccentLight] → [NutritionAccent]. */
        val Meal = ButtonColorScheme(
            primaryGradientColors = listOf(NutritionAccentLight, NutritionAccent),
            outlineContentColor = NutritionAccent,
        )

        /** Azul — módulo de Tareas. Usa [TaskAccentLight] → [TaskAccent]. */
        val Task = ButtonColorScheme(
            primaryGradientColors = listOf(TaskAccentLight, TaskAccent),
            outlineContentColor = TaskAccent,
        )

        /** Rosa/Rojo — módulo de Rutinas de Entrenamiento. Usa [WorkoutAccentLight] → [WorkoutAccent]. */
        val Workout = ButtonColorScheme(
            primaryGradientColors = listOf(WorkoutAccentLight, WorkoutAccent),
            outlineContentColor = WorkoutAccent,
        )
    }
}

/**
 * Estado visual del botón según enabled/disabled.
 *
 * @property backgroundColors Colores del gradiente
 * @property textColor Color del texto
 */
private data class ButtonVisualState(
    val backgroundColors: List<Color>,
    val textColor: Color,
) {
    companion object {
        val Disabled = ButtonVisualState(ButtonColorsDisabled, TextOnPrimary.copy(alpha = 0.7f))
    }
}

/**
 * Parámetros de configuración del botón.
 *
 * @property text Texto a mostrar
 * @property enabled Estado habilitado/deshabilitado
 * @property isLoading Mostrar spinner de carga
 * @property variant Variante visual [ButtonVariant]
 * @property colorScheme Esquema de colores según el módulo (default: [ButtonColorScheme.Habit])
 * @property showTrailingIcon Mostrar icono al final
 * @property trailingIcon Icono a mostrar (default: flecha)
 */
data class ButtonParams(
    val text: String,
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
    val variant: ButtonVariant = ButtonVariant.PRIMARY,
    val colorScheme: ButtonColorScheme = ButtonColorScheme.Default,
    val showTrailingIcon: Boolean = false,
    val trailingIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward,
    val showLeadingIcon: Boolean = false,
    val leadingIcon: ImageVector = Icons.Default.Add,
    val shape: Shape? = null,   // null → usa MaterialTheme.shapes.extraLarge (pill)
)

/**
 * Botón reutilizable con gradiente.
 *
 * Soporta dos variantes: [ButtonVariant.PRIMARY] con fondo gradiente
 * y [ButtonVariant.OUTLINE] con borde gradiente.
 *
 * @param params Configuración del botón
 * @param onClick Callback al hacer click
 * @param modifier Modificador de Compose
 */
@Composable
fun ButtonComponent(
    params: ButtonParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEffectivelyEnabled = params.enabled && !params.isLoading

    val visualState = when {
        !params.enabled -> ButtonVisualState.Disabled
        params.variant == ButtonVariant.PRIMARY -> ButtonVisualState(
            backgroundColors = params.colorScheme.primaryGradientColors,
            textColor = TextOnPrimary,
        )
        else -> ButtonVisualState(
            backgroundColors = listOf(Color.White, Color.White),
            textColor = params.colorScheme.outlineContentColor,
        )
    }

    when (params.variant) {
        ButtonVariant.PRIMARY -> PrimaryButton(
            params = params.copy(enabled = isEffectivelyEnabled),
            visualState = visualState,
            isLoading = params.isLoading,
            onClick = onClick,
            modifier = modifier
        )
        ButtonVariant.OUTLINE -> OutlineButton(
            params = params.copy(enabled = isEffectivelyEnabled),
            visualState = visualState,
            onClick = onClick,
            modifier = modifier
        )
        ButtonVariant.CREATE -> CreateButton(
            params = params.copy(enabled = isEffectivelyEnabled),
            visualState = visualState,
            onClick = onClick,
            modifier = modifier
        )
    }
}

@Composable
private fun PrimaryButton(
    params: ButtonParams,
    visualState: ButtonVisualState,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resolvedShape = params.shape ?: MaterialTheme.shapes.extraLarge
    Button(
        onClick = onClick,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = resolvedShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = visualState.textColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(colors = visualState.backgroundColors),
                    shape = resolvedShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = visualState.textColor,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = params.text,
                        color = visualState.textColor,
                        style = ButtonTextStyle
                    )
                    if (params.showTrailingIcon) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = params.trailingIcon,
                            contentDescription = null,
                            tint = visualState.textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OutlineButton(
    params: ButtonParams,
    visualState: ButtonVisualState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resolvedShape = params.shape ?: MaterialTheme.shapes.extraLarge
    Button(
        onClick = onClick,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = if (params.enabled) params.colorScheme.outlineBorderColors else ButtonColorsDisabled
                ),
                shape = resolvedShape
            ),
        shape = resolvedShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.5f),
            contentColor = visualState.textColor,
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = params.text,
            color = if (params.enabled) visualState.textColor else visualState.textColor.copy(alpha = 0.5f),
            style = ButtonTextStyle,
        )
    }
}

@Composable
private fun CreateButton(
    params: ButtonParams,
    visualState: ButtonVisualState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resolvedShape = params.shape ?: MaterialTheme.shapes.medium
    val contentColor = if (params.enabled) visualState.textColor else visualState.textColor.copy(alpha = 0.4f)
    Button(
        onClick = onClick,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = resolvedShape,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (params.enabled)
                params.colorScheme.outlineContentColor.copy(alpha = 0.5f)
            else
                params.colorScheme.outlineContentColor.copy(alpha = 0.2f),
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = contentColor,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        if (params.showLeadingIcon) {
            Icon(
                imageVector = params.leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = params.text.uppercase(),
            color = contentColor,
            style = ButtonTextStyle,
        )
        if (params.showTrailingIcon) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = params.trailingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun ButtonComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Default (Auth / general)
            ButtonComponent(params = ButtonParams(text = "Default — PRIMARY"), onClick = {})
            ButtonComponent(params = ButtonParams(text = "Default — OUTLINE", variant = ButtonVariant.OUTLINE), onClick = {})
            // Habit
            ButtonComponent(params = ButtonParams(text = "Hábito — PRIMARY", colorScheme = ButtonColorScheme.Habit), onClick = {})
            ButtonComponent(params = ButtonParams(text = "Hábito — OUTLINE", variant = ButtonVariant.OUTLINE, colorScheme = ButtonColorScheme.Habit), onClick = {})
            // Meal
            ButtonComponent(params = ButtonParams(text = "Comida — PRIMARY", colorScheme = ButtonColorScheme.Meal), onClick = {})
            ButtonComponent(params = ButtonParams(text = "Comida — OUTLINE", variant = ButtonVariant.OUTLINE, colorScheme = ButtonColorScheme.Meal), onClick = {})
            // Task
            ButtonComponent(params = ButtonParams(text = "Tarea — PRIMARY", colorScheme = ButtonColorScheme.Task), onClick = {})
            ButtonComponent(params = ButtonParams(text = "Tarea — OUTLINE", variant = ButtonVariant.OUTLINE, colorScheme = ButtonColorScheme.Task), onClick = {})
            // Workout
            ButtonComponent(params = ButtonParams(text = "Rutina — PRIMARY", colorScheme = ButtonColorScheme.Workout), onClick = {})
            ButtonComponent(params = ButtonParams(text = "Rutina — OUTLINE", variant = ButtonVariant.OUTLINE, colorScheme = ButtonColorScheme.Workout), onClick = {})
            // CREATE
            ButtonComponent(params = ButtonParams(text = "Crear comida", variant = ButtonVariant.CREATE, colorScheme = ButtonColorScheme.Meal, showLeadingIcon = true), onClick = {})
            ButtonComponent(params = ButtonParams(text = "Crear hábito", variant = ButtonVariant.CREATE, colorScheme = ButtonColorScheme.Habit, showLeadingIcon = true), onClick = {})
            // Disabled
            ButtonComponent(params = ButtonParams(text = "Deshabilitado", enabled = false), onClick = {})
        }
    }
}
