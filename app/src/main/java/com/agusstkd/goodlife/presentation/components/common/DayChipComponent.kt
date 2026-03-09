package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.BorderDefault
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.TextPrimary

/**
 * @property label Texto corto del día (ej: "lun", "mar").
 * @property selected Si el día está seleccionado.
 * @property selectedColor Color de fondo cuando está seleccionado.
 * @property chipSize Diámetro del chip circular.
 */
data class DayChipParams(
    val label: String,
    val selected: Boolean,
    val selectedColor: Color = LightGreen,
    val chipSize: Dp = 42.dp,
)

/**
 * Chip circular para selección de días de la semana.
 *
 * Selected: fondo verde relleno con texto blanco bold.
 * Unselected: borde gris con fondo transparente y texto oscuro.
 */
@Composable
fun DayChipComponent(
    params: DayChipParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(params.chipSize),
        shape = CircleShape,
        color = if (params.selected) params.selectedColor else Color.Transparent,
        border = if (params.selected) null else BorderStroke(1.dp, BorderDefault),
    ) {
        Text(
            text = params.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (params.selected) FontWeight.Bold else FontWeight.Normal,
            color = if (params.selected) Color.White else TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(2.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun DayChipComponentPreview() {
    GoodLifeTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf("lun", "mar", "mié", "jue", "vie", "sáb", "dom").forEachIndexed { i, name ->
                DayChipComponent(
                    params = DayChipParams(label = name, selected = i == 0 || i == 2 || i == 4),
                    onClick = {},
                )
            }
        }
    }
}
