package com.agusstkd.goodlife.presentation.components.common

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.components.GradientIcon
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DegradeBackground5
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen

/**
 * Estado visual del checkbox según checked/unchecked.
 *
 * @property backgroundColors Colores del gradiente cuando está checked
 * @property textColor Color del texto
 */
data class CheckboxVisualState(
    val backgroundColors: List<Color>,
    val textColor: Color = Color.Black
) {
    companion object {
        val Enabled = CheckboxVisualState(
            backgroundColors = listOf(DarkGreen, LightGreen, DegradeBackground5),
            textColor = DarkGreen
        )
        val Disabled = CheckboxVisualState(
            backgroundColors = listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E)),
            textColor = Color.Gray
        )
    }
}

/**
 * Identificadores para checkboxes específicos.
 */
enum class CheckboxParamsId {
    ENABLE_FINGER_PRINT,
    ACCEPT_TERMS_AND_CONDITIONS,
    REMEMBER_USER,
    OTHER
}

/**
 * Parámetros del checkbox.
 *
 * @property text Texto principal
 * @property checked Estado checked/unchecked
 * @property id Identificador del checkbox
 * @property linkText Texto clickeable adicional (ej: "términos y condiciones")
 * @property endIcon Icono al final (ej: huella digital)
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

/**
 * Checkbox con gradiente y soporte para texto con link e icono final.
 *
 * @param params Configuración del checkbox
 * @param onClick Callback al cambiar estado
 * @param modifier Modificador de Compose
 * @param onLinkClick Callback al tocar el link (opcional)
 * @param onEndIconClick Callback al tocar el icono final (opcional)
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(if (params.linkText == null) Modifier.clickable { onClick() } else Modifier)
            .padding(4.dp)
    ) {
        // Custom checkbox con gradiente
        Box(
            modifier = Modifier
                .size(22.dp)
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

        // Texto con link opcional
        if (params.linkText != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = params.text,
                    color = if (isChecked) DarkGreen else Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = params.linkText,
                    color = DarkGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLinkClick?.invoke() }
                )
            }
        } else {
            Text(
                text = params.text,
                color = colors.textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Icono final opcional
        params.endIcon?.let { icon ->
            GradientIcon(
                imageVector = icon,
                colors = colors.backgroundColors,
                modifier = Modifier
                    .size(26.dp)
                    .then(
                        if (onEndIconClick != null && isChecked) {
                            Modifier.clickable { onEndIconClick() }
                        } else Modifier
                    ),
                contentDescription = "Activar con huella"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CheckboxComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CheckboxComponent(
                params = CheckboxParams(text = "Recordar usuario", checked = false),
                onClick = {}
            )
            CheckboxComponent(
                params = CheckboxParams(text = "Recordar usuario", checked = true),
                onClick = {}
            )
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Activar login con huella",
                    checked = true,
                    endIcon = Icons.Default.Fingerprint
                ),
                onClick = {},
                onEndIconClick = {}
            )
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
