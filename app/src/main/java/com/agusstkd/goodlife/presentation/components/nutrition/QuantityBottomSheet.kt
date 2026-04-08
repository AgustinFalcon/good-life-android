package com.agusstkd.goodlife.presentation.components.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.PortionUnit
import com.agusstkd.goodlife.presentation.components.common.ButtonColorScheme
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.theme.CaloriesColor
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.FatColor
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.NutritionBackground
import com.agusstkd.goodlife.presentation.theme.ProteinColor


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuantityBottomSheet(
    ingredient: Ingredient,
    texts: CreateMealPlanTexts,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Double, unit: String) -> Unit,
    modifier: Modifier = Modifier
) {

    // Key en remember para resetear los valores del bottom sheet
    var quantity by remember(ingredient.id) { mutableStateOf("") }
    var selectedUnit by remember(ingredient.id) { mutableStateOf(PortionUnit.fromString(ingredient.servingUnit)) }

    val availableUnits = remember(ingredient.servingUnit) {
        PortionUnit.compatibleWith(PortionUnit.fromString(ingredient.servingUnit))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = modifier,
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Título principal
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip(
                            modifier = Modifier.widthIn(max = 220.dp),
                        ) {
                            Text(text = texts.quantitySheetMacroTooltip)
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }


            // Subtítulo: referencia calórica por porción
            Text(
                text = "${ingredient.calories.toInt()} kcal / ${ingredient.servingSize.toInt()}${ingredient.servingUnit}",
                style = MaterialTheme.typography.bodyMedium,
                color = NutritionAccent,
            )

            // Label de sección cantidad
            Text(
                text = texts.quantitySheetQuantityLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = NutritionAccent,
            )

            // Input
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                //label = { Text(texts.quantitySheetQuantityLabel) },
                modifier = Modifier.fillMaxWidth(),
            )


            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                availableUnits.forEach { unit ->
                    FilterChip(
                        selected = selectedUnit == unit,
                        onClick = { selectedUnit = unit },
                        label = { Text(text = unit.label) }
                    )
                }
            }

            val qty = quantity.toDoubleOrNull() ?: 0.0
            val factor = if (ingredient.servingSize > 0) qty / ingredient.servingSize else 0.0

            val previewKcal = ingredient.calories * factor
            val previewProtein = ingredient.protein * factor
            val previewCarbs = ingredient.carbs * factor
            val previewFat = ingredient.fat * factor

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = NutritionBackground, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MacroPreviewItem("${previewKcal.toInt()}", "KCAL", CaloriesColor)
                MacroPreviewItem("${previewProtein.toInt()}", "PROT", ProteinColor)
                MacroPreviewItem("${previewCarbs.toInt()}", "CARB", CarbsColor)
                MacroPreviewItem("${previewFat.toInt()}", texts.macroFatLabel.uppercase(), FatColor)
            }


            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                ButtonComponent(
                    params = ButtonParams(
                        text = texts.quantitySheetCancelButton,
                        variant = ButtonVariant.OUTLINE,
                        colorScheme = ButtonColorScheme.Meal,
                    ),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )

                Spacer(Modifier.width(12.dp))

                ButtonComponent(
                    params = ButtonParams(
                        text = texts.quantitySheetConfirmButton,
                        variant = ButtonVariant.PRIMARY,
                        colorScheme = ButtonColorScheme.Meal,
                        enabled = quantity.isNotEmpty() && quantity != "0",
                    ),
                    onClick = { onConfirm(qty, selectedUnit.label) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MacroPreviewItem(
    value: String,
    label: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
