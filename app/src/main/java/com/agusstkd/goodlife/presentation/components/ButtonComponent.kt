package com.agusstkd.goodlife.presentation.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.AuthButtonColors
import com.agusstkd.goodlife.presentation.theme.ButtonColorsDisabled
import com.agusstkd.goodlife.presentation.theme.ButtonColorsEnabled
import com.agusstkd.goodlife.presentation.theme.ButtonTextStyle
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TextOnPrimary

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 * BUTTON COMPONENT - BOTÓN REUTILIZABLE CON GRADIENTE
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 *
 * ## Características:
 * - Degradado automático según el estado (enabled/disabled)
 * - Variantes: PRIMARY (relleno con gradiente) y OUTLINE (borde con gradiente)
 * - Soporte para icono trailing (flecha)
 *
 * ## Estados Visuales:
 * - **ENABLED**: Degradado verde vibrante
 * - **DISABLED**: Degradado gris
 *
 * ## Ejemplo de uso:
 * ```kotlin
 * ButtonComponent(
 *     params = ButtonParams(
 *         text = "Iniciar Sesión",
 *         enabled = isFormValid,
 *         variant = ButtonVariant.PRIMARY
 *     ),
 *     onClick = { viewModel.login() }
 * )
 * ```
 */

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 1. VARIANT - Tipo de botón
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Variante visual del botón.
 */
enum class ButtonVariant {
    /** Botón con fondo de gradiente y texto blanco */
    PRIMARY,
    /** Botón con borde de gradiente, fondo blanco y texto oscuro */
    OUTLINE
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 2. VISUAL STATE - Estado visual del botón
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Estado visual del botón.
 * Encapsula los colores según el estado enabled/disabled.
 *
 * @property backgroundColors Lista de colores para el degradado
 * @property textColor Color del texto
 */
data class ButtonVisualState(
    val backgroundColors: List<Color>,
    val textColor: Color = TextOnPrimary
) {
    companion object {
        /**
         * Estado ENABLED para botón PRIMARY.
         * Degradado verde vibrante (Login/Register).
         */
        val EnabledPrimary = ButtonVisualState(
            backgroundColors = AuthButtonColors,
            textColor = TextOnPrimary
        )

        /**
         * Estado ENABLED para botón OUTLINE.
         * Sin fondo, borde con degradado.
         */
        val EnabledOutline = ButtonVisualState(
            backgroundColors = listOf(Color.White, Color.White),
            textColor = DarkGreen
        )

        /**
         * Estado DISABLED (formulario inválido).
         * Degradado gris.
         */
        val Disabled = ButtonVisualState(
            backgroundColors = ButtonColorsDisabled,
            textColor = TextOnPrimary.copy(alpha = 0.7f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 3. PARAMS - Parámetros del botón
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Parámetros de configuración del botón.
 *
 * @property text Texto del botón
 * @property enabled Estado del botón (true = enabled, false = disabled)
 * @property isLoading Mostrar spinner de carga (deshabilita el botón)
 * @property variant Variante visual (PRIMARY/OUTLINE)
 * @property showTrailingIcon Mostrar icono de flecha al final
 */
data class ButtonParams(
    val text: String,
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
    val variant: ButtonVariant = ButtonVariant.PRIMARY,
    val showTrailingIcon: Boolean = false,
    val trailingIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
)

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 4. COMPONENTE - El Button
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Componente Button con gradiente.
 *
 * ## Responsabilidades del componente:
 * - Decidir qué colores usar según enabled/disabled y variant
 * - Aplicar el gradiente
 * - Manejar la visualización
 *
 * ## Responsabilidades de la Screen:
 * - Decidir si el botón está enabled o disabled (lógica de negocio)
 * - Pasar el callback onClick
 * - Pasar el texto
 *
 * @param params Configuración del botón
 * @param onClick Callback cuando se hace click
 * @param modifier Modificador de Compose
 */
@Composable
fun ButtonComponent(
    params: ButtonParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // El botón está efectivamente deshabilitado si está loading o disabled
    val isEffectivelyEnabled = params.enabled && !params.isLoading

    // Seleccionar el estado visual según variant y enabled
    // Nota: Mantenemos los colores enabled durante loading para mejor UX
    val visualState = when {
        !params.enabled -> ButtonVisualState.Disabled
        params.variant == ButtonVariant.PRIMARY -> ButtonVisualState.EnabledPrimary
        else -> ButtonVisualState.EnabledOutline
    }

    when (params.variant) {
        ButtonVariant.PRIMARY -> {
            PrimaryButton(
                params = params.copy(enabled = isEffectivelyEnabled),
                visualState = visualState,
                isLoading = params.isLoading,
                onClick = onClick,
                modifier = modifier
            )
        }
        ButtonVariant.OUTLINE -> {
            OutlineButton(
                params = params.copy(enabled = isEffectivelyEnabled),
                visualState = visualState,
                onClick = onClick,
                modifier = modifier
            )
        }
    }
}

/**
 * Botón PRIMARY con fondo de gradiente.
 */
@Composable
private fun PrimaryButton(
    params: ButtonParams,
    visualState: ButtonVisualState,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50.dp),
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
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                // Mostrar spinner cuando está cargando
                CircularProgressIndicator(
                    color = visualState.textColor,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                // Contenido normal del botón
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

/**
 * Botón OUTLINE con borde de gradiente y fondo blanco.
 */
@Composable
private fun OutlineButton(
    params: ButtonParams,
    visualState: ButtonVisualState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = if (params.enabled) {
                        ButtonColorsEnabled
                    } else {
                        ButtonColorsDisabled
                    }
                ),
                shape = RoundedCornerShape(50.dp)
            ),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.5f),
            contentColor = visualState.textColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = params.text,
                color = if (params.enabled) DarkGreen else DarkGreen.copy(alpha = 0.5f),
                style = ButtonTextStyle
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFFE8FFF8)
@Composable
fun ButtonComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // PRIMARY Enabled
            ButtonComponent(
                params = ButtonParams(
                    text = "Iniciar Sesión",
                    enabled = true,
                    variant = ButtonVariant.PRIMARY,
                    showTrailingIcon = true
                ),
                onClick = {}
            )

            // PRIMARY Disabled
            ButtonComponent(
                params = ButtonParams(
                    text = "Iniciar Sesión",
                    enabled = false,
                    variant = ButtonVariant.PRIMARY
                ),
                onClick = {}
            )

            Spacer(Modifier.height(16.dp))

            // OUTLINE Enabled
            ButtonComponent(
                params = ButtonParams(
                    text = "Crear Cuenta",
                    enabled = true,
                    variant = ButtonVariant.OUTLINE
                ),
                onClick = {}
            )

            // OUTLINE Disabled
            ButtonComponent(
                params = ButtonParams(
                    text = "Crear Cuenta",
                    enabled = false,
                    variant = ButtonVariant.OUTLINE
                ),
                onClick = {}
            )
        }
    }
}
