package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextTertiary

/**
 * @property label Texto que acompaña al switch.
 * @property checked Estado actual del switch.
 * @property enabled Si el switch está habilitado para interacción.
 * @property icon Icono opcional a la izquierda del label.
 * @property checkedTrackColor Color del track cuando está activado.
 * @property uncheckedTrackColor Color del track cuando está desactivado.
 * @property thumbSize Tamaño mínimo del thumb (evita que se achique).
 */
data class SwitchParams(
    val label: String,
    val checked: Boolean,
    val enabled: Boolean = true,
    val icon: ImageVector? = null,
    val checkedTrackColor: Color = LightGreen,
    val uncheckedTrackColor: Color = Color(0xFFE0E0E0),
    val thumbSize: Dp = 24.dp,
)

/**
 * Switch con label integrado, colores del tema GoodLife y thumb de tamaño fijo.
 *
 * Thumb siempre blanco; track verde cuando checked, gris cuando unchecked.
 * El thumb se envuelve en un [Box] con tamaño fijo para evitar
 * que Material3 lo reduzca al cambiar de estado.
 */
@Composable
fun SwitchComponent(
    params: SwitchParams,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (params.icon != null) {
                Icon(
                    imageVector = params.icon,
                    contentDescription = null,
                    tint = if (params.enabled) TextPrimary else TextTertiary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = params.label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (params.enabled) TextPrimary else TextTertiary,
            )
        }

        Switch(
            checked = params.checked,
            onCheckedChange = onCheckedChange,
            enabled = params.enabled,
            thumbContent = {
                Box(modifier = Modifier.size(params.thumbSize)) {
                    Surface(
                        modifier = Modifier.size(params.thumbSize),
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 2.dp,
                    ) {}
                }
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = params.checkedTrackColor,
                uncheckedTrackColor = params.uncheckedTrackColor,
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SwitchComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SwitchComponent(
                params = SwitchParams(label = "Recordatorio", checked = true),
                onCheckedChange = {},
            )
            SwitchComponent(
                params = SwitchParams(label = "Sin hora específica", checked = false),
                onCheckedChange = {},
            )
            SwitchComponent(
                params = SwitchParams(label = "Deshabilitado", checked = false, enabled = false),
                onCheckedChange = {},
            )
        }
    }
}
