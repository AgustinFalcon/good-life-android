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
import com.agusstkd.goodlife.presentation.theme.TextSecondary

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 * CHECKBOX COMPONENT - Checkbox con texto y opción de icono
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 *
 * Componente reutilizable para checkboxes con:
 * - Fondo con degradado cuando está activo
 * - Icono opcional al final (ej: huella digital)
 * - Soporte para link clickeable
 *
 * ## Ejemplo de uso:
 * ```kotlin
 * CheckboxComponent(
 *     params = CheckboxParams(
 *         text = "Activar login con huella",
 *         checked = fingerPrint,
 *         endIcon = Icons.Default.Fingerprint
 *     ),
 *     onClick = { onAction(LoginUiAction.OnCheckFingerPrint) }
 * )
 * ```
 */

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 1. VISUAL STATE - Estado visual del checkbox
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Estado visual del checkbox.
 */
data class CheckboxVisualState(
    val backgroundColors: List<Color>,
    val textColor: Color = Color.Black
) {
    companion object {
        /**
         * Estado CHECKED (activo).
         * Degradado verde vibrante.
         */
        val Checked = CheckboxVisualState(
            backgroundColors = listOf(DarkGreen, LightGreen, DegradeBackground5),
            textColor = DarkGreen
        )

        /**
         * Estado UNCHECKED (inactivo).
         * Sin fondo, borde gris.
         */
        val Unchecked = CheckboxVisualState(
            backgroundColors = listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E)),
            textColor = TextSecondary
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 2. PARAMS - Parámetros del checkbox
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Parámetros de configuración del checkbox.
 *
 * @property text Texto principal
 * @property checked Estado del checkbox
 * @property linkText Texto opcional para link clickeable
 * @property endIcon Icono opcional al final (ej: huella digital)
 */
data class CheckboxParams(
    val text: String,
    val checked: Boolean,
    val linkText: String? = null,
    val endIcon: ImageVector? = null
)

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 3. COMPONENTE - El Checkbox
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Componente Checkbox con gradiente y texto.
 *
 * @param params Configuración del checkbox
 * @param onClick Callback cuando se hace click en el checkbox
 * @param onLinkClick Callback opcional para click en el link
 * @param modifier Modificador de Compose
 */
@Composable
fun CheckboxComponent(
    params: CheckboxParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLinkClick: (() -> Unit)? = null
) {
    val isChecked = params.checked
    val visualState = if (isChecked) CheckboxVisualState.Checked else CheckboxVisualState.Unchecked
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
                        Modifier.background(Brush.linearGradient(visualState.backgroundColors))
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
                Text(
                    text = params.text,
                    color = if (isChecked) DarkGreen else TextSecondary,
                    fontSize = 14.sp
                )
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
                color = visualState.textColor,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Icono opcional al final
        params.endIcon?.let {
            GradientIcon(
                imageVector = it,
                colors = visualState.backgroundColors,
                modifier = Modifier.size(rowSize + 4.dp)
            )
        }
    }
}

/**
 * Icono con degradado.
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
            // Checkbox unchecked
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Recordar usuario",
                    checked = false
                ),
                onClick = {}
            )

            // Checkbox checked
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Recordar usuario",
                    checked = true
                ),
                onClick = {}
            )

            // Checkbox con icono de huella
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Activar login con huella",
                    checked = true,
                    endIcon = Icons.Default.Fingerprint
                ),
                onClick = {}
            )

            // Checkbox con link
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Acepto los ",
                    checked = false,
                    linkText = "términos y condiciones"
                ),
                onClick = {},
                onLinkClick = {}
            )
        }
    }
}
