package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.presentation.theme.BorderDefault
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.HabitAccent
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextTertiary

data class CategorySelectorParams(
    val categories: List<Pair<HabitCategory, String>>,
    val selectedCategory: HabitCategory?,
    val placeholder: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectorComponent(
    params: CategorySelectorParams,
    onCategorySelected: (HabitCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = params.categories
        .firstOrNull { it.first == params.selectedCategory }
        ?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(text = params.placeholder, color = TextTertiary)
            },
            leadingIcon = params.selectedCategory?.let { category ->
                {
                    Icon(
                        imageVector = category.icon(),
                        contentDescription = null,
                        tint = HabitAccent,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HabitAccent,
                unfocusedBorderColor = BorderDefault,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
            ),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            params.categories.forEach { (category, label) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = category.icon(),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (category == params.selectedCategory) HabitAccent else TextPrimary,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                            )
                        }
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                )
            }
        }
    }
}

fun HabitCategory.icon(): ImageVector = when (this) {
    HabitCategory.HYDRATION -> Icons.Default.WaterDrop
    HabitCategory.MEDITATION -> Icons.Default.SelfImprovement
    HabitCategory.READING -> Icons.Default.Book
    HabitCategory.EXERCISE -> Icons.Default.FitnessCenter
    HabitCategory.SLEEP -> Icons.Default.Bedtime
    HabitCategory.NUTRITION -> Icons.Default.LocalDining
    HabitCategory.LEARNING -> Icons.Default.Lightbulb
    HabitCategory.MINDFULNESS -> Icons.Default.AutoAwesome
    HabitCategory.SOCIAL -> Icons.Default.Groups
    HabitCategory.CREATIVITY -> Icons.Default.Brush
    HabitCategory.PRODUCTIVITY -> Icons.Default.Speed
    HabitCategory.HEALTH -> Icons.Default.HealthAndSafety
    HabitCategory.CUSTOM -> Icons.Default.Tune
}

@Preview(showBackground = true)
@Composable
private fun CategorySelectorPreview() {
    val categories = listOf(
        HabitCategory.HYDRATION to "Hidratación",
        HabitCategory.MEDITATION to "Meditación",
        HabitCategory.READING to "Lectura",
        HabitCategory.EXERCISE to "Ejercicio",
        HabitCategory.SLEEP to "Sueño",
    )
    GoodLifeTheme {
        CategorySelectorComponent(
            params = CategorySelectorParams(
                categories = categories,
                selectedCategory = HabitCategory.HYDRATION,
                placeholder = "Seleccionar categoría",
            ),
            onCategorySelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySelectorEmptyPreview() {
    GoodLifeTheme {
        CategorySelectorComponent(
            params = CategorySelectorParams(
                categories = listOf(
                    HabitCategory.HYDRATION to "Hidratación",
                    HabitCategory.EXERCISE to "Ejercicio",
                ),
                selectedCategory = null,
                placeholder = "Seleccionar categoría",
            ),
            onCategorySelected = {},
        )
    }
}
