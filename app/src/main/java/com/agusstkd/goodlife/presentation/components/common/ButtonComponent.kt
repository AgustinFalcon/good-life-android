package com.agusstkd.goodlife.presentation.components.common

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
 * Variante visual del botón.
 */
enum class ButtonVariant {
    /** Fondo con gradiente verde */
    PRIMARY,
    /** Borde con gradiente, fondo blanco */
    OUTLINE
}

/**
 * Estado visual del botón según enabled/disabled.
 *
 * @property backgroundColors Colores del gradiente
 * @property textColor Color del texto
 */
data class ButtonVisualState(
    val backgroundColors: List<Color>,
    val textColor: Color = TextOnPrimary
) {
    companion object {
        val EnabledPrimary = ButtonVisualState(AuthButtonColors, TextOnPrimary)
        val EnabledOutline = ButtonVisualState(listOf(Color.White, Color.White), DarkGreen)
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
 * @property showTrailingIcon Mostrar icono al final
 * @property trailingIcon Icono a mostrar (default: flecha)
 */
data class ButtonParams(
    val text: String,
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
    val variant: ButtonVariant = ButtonVariant.PRIMARY,
    val showTrailingIcon: Boolean = false,
    val trailingIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
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
        params.variant == ButtonVariant.PRIMARY -> ButtonVisualState.EnabledPrimary
        else -> ButtonVisualState.EnabledOutline
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
    Button(
        onClick = onClick,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.extraLarge,
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
                    shape = MaterialTheme.shapes.extraLarge
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
    Button(
        onClick = onClick,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = if (params.enabled) ButtonColorsEnabled else ButtonColorsDisabled
                ),
                shape = MaterialTheme.shapes.extraLarge
            ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.5f),
            contentColor = visualState.textColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = params.text,
            color = if (params.enabled) DarkGreen else DarkGreen.copy(alpha = 0.5f),
            style = ButtonTextStyle
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE8FFF8)
@Composable
private fun ButtonComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ButtonComponent(
                params = ButtonParams(text = "Iniciar Sesión", showTrailingIcon = true),
                onClick = {}
            )
            ButtonComponent(
                params = ButtonParams(text = "Iniciar Sesión", enabled = false),
                onClick = {}
            )
            ButtonComponent(
                params = ButtonParams(text = "Crear Cuenta", variant = ButtonVariant.OUTLINE),
                onClick = {}
            )
        }
    }
}
