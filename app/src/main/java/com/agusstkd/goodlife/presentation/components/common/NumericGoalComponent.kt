package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.BorderDefault
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.TextTertiary

/**
 * Parámetros para el componente de meta numérica.
 *
 * @property targetValue Valor numérico actual (como String para el TextField).
 * @property unit Unidad de medida actual.
 * @property targetValuePlaceholder Placeholder del campo numérico.
 * @property unitPlaceholder Placeholder del campo de unidad.
 */
data class NumericGoalParams(
    val targetValue: String,
    val unit: String,
    val targetValuePlaceholder: String,
    val unitPlaceholder: String,
)

/**
 * Row con dos campos: uno numérico para la meta y otro de texto para la unidad.
 *
 * Ejemplo visual: [ 8 ] [ vasos ]
 *
 * @param params Configuración del componente.
 * @param onTargetValueChange Callback cuando cambia el valor numérico.
 * @param onUnitChange Callback cuando cambia la unidad.
 * @param modifier Modifier opcional.
 */
@Composable
fun NumericGoalComponent(
    params: NumericGoalParams,
    onTargetValueChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LightGreen,
        unfocusedBorderColor = BorderDefault,
        cursorColor = LightGreen,
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = params.targetValue,
            onValueChange = { value ->
                if (value.all { it.isDigit() }) onTargetValueChange(value)
            },
            placeholder = {
                Text(
                    text = params.targetValuePlaceholder,
                    color = TextTertiary,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            modifier = Modifier.weight(1f),
        )

        OutlinedTextField(
            value = params.unit,
            onValueChange = onUnitChange,
            placeholder = {
                Text(
                    text = params.unitPlaceholder,
                    color = TextTertiary,
                )
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            modifier = Modifier.weight(1.5f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NumericGoalFilledPreview() {
    GoodLifeTheme {
        NumericGoalComponent(
            params = NumericGoalParams(
                targetValue = "8",
                unit = "vasos",
                targetValuePlaceholder = "Ej: 8",
                unitPlaceholder = "Ej: vasos",
            ),
            onTargetValueChange = {},
            onUnitChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NumericGoalEmptyPreview() {
    GoodLifeTheme {
        NumericGoalComponent(
            params = NumericGoalParams(
                targetValue = "",
                unit = "",
                targetValuePlaceholder = "Ej: 8",
                unitPlaceholder = "Ej: vasos",
            ),
            onTargetValueChange = {},
            onUnitChange = {},
        )
    }
}
