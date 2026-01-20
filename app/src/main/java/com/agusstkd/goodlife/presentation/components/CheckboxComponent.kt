package com.agusstkd.goodlife.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DegradeBackground5
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 1. VISUAL STATE - Estado visual del checkbox
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Estado visual del checkbox.
 *
 * @property backgroundColors Colores del degradado de fondo
 * @property textColor Color del texto
 */
data class CheckboxVisualState(
    val backgroundColors: List<Color>,
    val textColor: Color = Color.Black
) {
    companion object {
        /**
         * Estado ENABLED (checkbox activo/checked).
         * Degradado verde vibrante.
         */
        val Enabled = CheckboxVisualState(
            backgroundColors = listOf(DarkGreen, LightGreen, DegradeBackground5),
            textColor = DarkGreen
        )

        /**
         * Estado DISABLED (checkbox inactivo/unchecked).
         * Degradado gris.
         */
        val Disabled = CheckboxVisualState(
            backgroundColors = listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E)),
            textColor = Color.Gray
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 2. PARAMS ID - Identificador del checkbox
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Identificadores para diferentes tipos de checkbox.
 * Útil para identificar qué checkbox se clickeó en callbacks.
 */
enum class CheckboxParamsId {
    ENABLE_FINGER_PRINT,
    ACCEPT_TERMS_AND_CONDITIONS,
    REMEMBER_USER,
    OTHER
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 3. PARAMS - Parámetros del checkbox
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Parámetros de configuración del checkbox.
 *
 * @property text Texto principal
 * @property checked Estado del checkbox (true = checked)
 * @property id Identificador del checkbox (opcional, para callbacks)
 * @property linkText Texto opcional para link clickeable
 * @property endIcon Icono opcional al final (ej: huella digital)
 * @property enabledState Estado visual cuando está checked
 * @property disabledState Estado visual cuando NO está checked
 */
data class CheckboxParams(
    val text: String,
    val checked: Boolean,
    val id: CheckboxParamsId = CheckboxParamsId.OTHER,
    val linkText: String? = null,
    val endIcon: ImageVector? = null,
    val enabledState: CheckboxVisualState = CheckboxVisualState.Enabled,
    val disabledState: CheckboxVisualState = CheckboxVisualState.Disabled
)

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 4. COMPONENTE - El Checkbox
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Componente Checkbox con gradiente y texto.
 *
 * ## Ejemplo de uso:
 * ```kotlin
 * CheckboxComponent(
 *     params = CheckboxParams(
 *         text = "Activar login con huella",
 *         checked = isBiometricEnabled,
 *         id = CheckboxParamsId.ENABLE_FINGER_PRINT,
 *         endIcon = Icons.Default.Fingerprint
 *     ),
 *     onClick = { onAction(LoginUiAction.OnBiometricToggle) },
 *     onEndIconClick = { onAction(LoginUiAction.OnBiometricIconClick) }
 * )
 * ```
 *
 * @param params Configuración del checkbox
 * @param onClick Callback cuando se hace click en el checkbox
 * @param modifier Modificador de Compose
 * @param onLinkClick Callback opcional para click en el link
 * @param onEndIconClick Callback opcional para click en el ícono final
 */
@Composable
fun CheckboxComponent(
    params: CheckboxParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLinkClick: (() -> Unit)? = null,
    onEndIconClick: (() -> Unit)? = null
) {
    val isChecked = params.checked
    val colors = if (isChecked) params.enabledState else params.disabledState
    val rowSize = 22.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(
                // Solo hacer clickeable la row si NO hay linkText
                if (params.linkText == null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(4.dp)
    ) {
        // Contenedor con degradado detrás del checkbox
        Box(
            modifier = Modifier
                .size(rowSize)
                .clip(RoundedCornerShape(4.dp))
                .then(
                    if (isChecked) {
                        Modifier.background(Brush.linearGradient(colors.backgroundColors))
                    } else {
                        Modifier
                            .background(Color.Transparent)
                            .border(2.dp, Color.Gray, RoundedCornerShape(4.dp))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Transparent,
                    uncheckedColor = Color.Transparent,
                    checkmarkColor = if (isChecked) Color.White else Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Si hay linkText, mostrar texto principal + link clickeable
        if (params.linkText != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Texto principal
                Text(
                    text = params.text,
                    color = if (isChecked) DarkGreen else Color.Gray,
                    fontSize = 14.sp
                )

                // Texto del link clickeable
                Text(
                    text = params.linkText,
                    color = DarkGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onLinkClick?.invoke()
                    }
                )
            }
        } else {
            // Texto simple sin link
            Text(
                text = params.text,
                color = colors.textColor,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Icono opcional al final (clickeable si hay callback y está checked)
        params.endIcon?.let { icon ->
            val iconModifier = Modifier
                .size(rowSize + 4.dp)
                .then(
                    if (onEndIconClick != null && isChecked) {
                        Modifier.clickable { onEndIconClick() }
                    } else {
                        Modifier
                    }
                )

            GradientIcon(
                imageVector = icon,
                colors = colors.backgroundColors,
                modifier = iconModifier,
                contentDescription = "Activar con huella"
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 5. GRADIENT ICON - Icono con degradado
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Icono con degradado aplicado.
 *
 * @param imageVector Icono a mostrar
 * @param colors Colores del degradado
 * @param modifier Modificador de Compose
 * @param contentDescription Descripción para accesibilidad
 */
@Composable
fun GradientIcon(
    imageVector: ImageVector,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                val brush = Brush.linearGradient(colors)
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = brush,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            },
        tint = Color.White
    )
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CheckboxComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Checkbox unchecked - Recordar usuario
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Recordar usuario",
                    checked = false,
                    id = CheckboxParamsId.REMEMBER_USER
                ),
                onClick = {}
            )

            // Checkbox checked - Recordar usuario
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Recordar usuario",
                    checked = true,
                    id = CheckboxParamsId.REMEMBER_USER
                ),
                onClick = {}
            )

            // Checkbox con icono de huella (checked - ícono clickeable)
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Activar login con huella",
                    checked = true,
                    id = CheckboxParamsId.ENABLE_FINGER_PRINT,
                    endIcon = Icons.Default.Fingerprint
                ),
                onClick = {},
                onEndIconClick = { /* Abre BiometricPrompt */ }
            )

            // Checkbox con icono de huella (unchecked - ícono gris)
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Activar login con huella",
                    checked = false,
                    id = CheckboxParamsId.ENABLE_FINGER_PRINT,
                    endIcon = Icons.Default.Fingerprint
                ),
                onClick = {}
            )

            // Checkbox con link (términos y condiciones)
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Acepto los ",
                    checked = false,
                    id = CheckboxParamsId.ACCEPT_TERMS_AND_CONDITIONS,
                    linkText = "términos y condiciones"
                ),
                onClick = {},
                onLinkClick = { /* Navega a términos */ }
            )

            // Checkbox con link checked
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Acepto los ",
                    checked = true,
                    id = CheckboxParamsId.ACCEPT_TERMS_AND_CONDITIONS,
                    linkText = "términos y condiciones"
                ),
                onClick = {},
                onLinkClick = { /* Navega a términos */ }
            )
        }
    }
}
