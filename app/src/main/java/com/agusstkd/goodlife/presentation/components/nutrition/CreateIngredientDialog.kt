package com.agusstkd.goodlife.presentation.components.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateMealPlanTexts
import com.agusstkd.goodlife.domain.model.nutrition.PortionUnit
import com.agusstkd.goodlife.presentation.components.common.ButtonColorScheme
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.theme.CaloriesColor
import com.agusstkd.goodlife.presentation.theme.CarbsColor
import com.agusstkd.goodlife.presentation.theme.FatColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.NutritionBackground
import com.agusstkd.goodlife.presentation.theme.ProteinColor

/**
 * Bottom sheet para crear un ingrediente custom dentro del wizard de meal plan.
 *
 * Presenta un formulario scrollable con los campos de nombre, marca, porción,
 * unidad y los cuatro macronutrientes. El botón Guardar permanece deshabilitado
 * hasta que [name] no esté vacío y [servingSize] sea mayor que cero.
 *
 * Todo el estado del formulario es interno; los valores finales se entregan
 * al caller únicamente a través de [onConfirm].
 *
 * @param texts Textos localizados de la pantalla de creación de meal plan.
 * @param onDismiss Callback invocado cuando el usuario cierra el sheet sin confirmar.
 * @param onConfirm Callback invocado con los valores del formulario al confirmar.
 *   [brand] es `null` si el usuario dejó el campo vacío.
 * @param modifier Modifier aplicado al [ModalBottomSheet].
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateIngredientDialog(
    texts: CreateMealPlanTexts,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        brand: String?,
        servingSize: Double,
        servingUnit: String,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf(PortionUnit.GRAM) }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    val isFormValid = name.isNotBlank() && (servingSize.toDoubleOrNull() ?: 0.0) > 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text = texts.createIngredientDialogTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            // ── Nombre ────────────────────────────────────────────────────────
            IngredientTextField(
                value = name,
                onValueChange = { name = it },
                label = texts.ingredientNameLabel,
                placeholder = texts.ingredientNamePlaceholder,
            )

            // ── Marca (opcional) ──────────────────────────────────────────────
            IngredientTextField(
                value = brand,
                onValueChange = { brand = it },
                label = texts.ingredientBrandLabel,
                placeholder = texts.ingredientBrandPlaceholder,
            )

            // ── Porción + selector de unidad ──────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IngredientTextField(
                    value = servingSize,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) servingSize = it },
                    label = texts.ingredientServingSizeLabel,
                    placeholder = "100",
                    keyboardType = KeyboardType.Decimal,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PortionUnit.entries.forEach { unit ->
                        FilterChip(
                            selected = selectedUnit == unit,
                            onClick = { selectedUnit = unit },
                            label = {
                                Text(
                                    text = unit.label,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NutritionAccent,
                                selectedLabelColor = Color.White,
                            ),
                        )
                    }
                }
            }

            // ── Macros 2×2 ────────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MacroCard(
                        value = calories,
                        onValueChange = { calories = it },
                        label = texts.ingredientCaloriesLabel,
                        icon = Icons.Filled.LocalFireDepartment,
                        accentColor = CaloriesColor,
                        modifier = Modifier.weight(1f),
                    )
                    MacroCard(
                        value = protein,
                        onValueChange = { protein = it },
                        label = texts.ingredientProteinLabel,
                        icon = Icons.Default.FitnessCenter,
                        accentColor = ProteinColor,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MacroCard(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = texts.ingredientCarbsLabel,
                        icon = Icons.Default.BakeryDining,
                        accentColor = CarbsColor,
                        modifier = Modifier.weight(1f),
                    )
                    MacroCard(
                        value = fat,
                        onValueChange = { fat = it },
                        label = texts.ingredientFatLabel,
                        icon = Icons.Default.Opacity,
                        accentColor = FatColor,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Botones ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ButtonComponent(
                    params = ButtonParams(
                        text = texts.quantitySheetCancelButton,
                        variant = ButtonVariant.OUTLINE,
                        colorScheme = ButtonColorScheme.Meal,
                    ),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                ButtonComponent(
                    params = ButtonParams(
                        text = texts.saveIngredientButton,
                        variant = ButtonVariant.PRIMARY,
                        colorScheme = ButtonColorScheme.Meal,
                        enabled = isFormValid,
                    ),
                    onClick = {
                        onConfirm(
                            name,
                            brand.takeIf { it.isNotBlank() },
                            servingSize.toDoubleOrNull() ?: 0.0,
                            selectedUnit.label,
                            calories.toDoubleOrNull() ?: 0.0,
                            protein.toDoubleOrNull() ?: 0.0,
                            carbs.toDoubleOrNull() ?: 0.0,
                            fat.toDoubleOrNull() ?: 0.0,
                        )
                    },
                    modifier = Modifier.weight(1.5f),
                )
            }
        }
    }
}

// ── Componentes privados ──────────────────────────────────────────────────────

/**
 * Campo de texto con fondo [NutritionBackground] y sin borde visible en reposo.
 * Al enfocar aparece un borde sutil con el color de acento de nutrición.
 */
@Composable
private fun IngredientTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.outline) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = NutritionBackground,
            focusedContainerColor = NutritionBackground,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = NutritionAccent.copy(alpha = 0.3f),
        ),
    )
}

/**
 * Tarjeta de macro con fondo [NutritionBackground], ícono coloreado, etiqueta
 * en mayúsculas y un campo numérico grande y transparente al centro.
 *
 * El placeholder "0" se muestra mediante un [Text] superpuesto cuando [value]
 * está vacío, porque [BasicTextField] no tiene API de placeholder directa.
 */
@Composable
private fun MacroCard(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(color = NutritionBackground, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor,
            )
        }

        val numberStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
        )
        Box {
            if (value.isEmpty()) {
                Text(
                    text = "0",
                    style = numberStyle.copy(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    ),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) onValueChange(it) },
                textStyle = numberStyle.copy(color = MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFFFF8F6)
@Composable
private fun CreateIngredientDialogPreview() {
    GoodLifeTheme {
        CreateIngredientDialog(
            texts = previewTexts(),
            onDismiss = {},
            onConfirm = { _, _, _, _, _, _, _, _ -> },
        )
    }
}

/** Textos mínimos necesarios para renderizar el dialog en preview. */
private fun previewTexts() = CreateMealPlanTexts(
    screenTitle = "", typeBadge = "", stepOf = "", nextButton = "",
    backButton = "", createPlanButton = "", successTitle = "",
    searchMealPlaceholder = "", filterAll = "", chooseMealButton = "",
    createNewMealButton = "", newMealNameLabel = "", newMealNamePlaceholder = "",
    newMealDescriptionLabel = "", newMealDescriptionPlaceholder = "",
    newMealTypeLabel = "", macrosSummaryTitle = "", macroProteinLabel = "",
    macroCarbsLabel = "", macroFatLabel = "", myIngredientsSectionTitle = "",
    ingredientCatalogSectionTitle = "", searchIngredientPlaceholder = "",
    addIngredientButton = "", createCustomIngredientButton = "",
    quantitySheetQuantityLabel = "", quantitySheetUnitLabel = "",
    quantitySheetConfirmButton = "CONFIRMAR",
    quantitySheetCancelButton = "CANCELAR",
    quantitySheetMacroTooltip = "",
    createIngredientDialogTitle = "Nuevo Ingrediente",
    ingredientNameLabel = "Nombre *",
    ingredientNamePlaceholder = "Ej: Avena instantánea",
    ingredientBrandLabel = "Marca (opcional)",
    ingredientBrandPlaceholder = "Ej: Quaker",
    ingredientServingSizeLabel = "Porción *",
    ingredientCaloriesLabel = "Calorías (kcal)",
    ingredientProteinLabel = "Proteínas (g)",
    ingredientCarbsLabel = "Carbohidratos (g)",
    ingredientFatLabel = "Grasas (g)",
    saveIngredientButton = "GUARDAR",
    mealSummaryTitle = "", mealTypeSectionTitle = "",
    errorNoMealSelected = "", errorMealNameEmpty = "", errorNoIngredients = "",
    errorNoMealType = "", errorNoDays = "", errorEndBeforeStart = "",
    errorQuantityZero = "", errorIngredientNameEmpty = "", errorServer = "",
    errorNetwork = "", loadingTitle = "",
)
