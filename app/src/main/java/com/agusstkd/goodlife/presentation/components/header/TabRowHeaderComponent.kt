package com.agusstkd.goodlife.presentation.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.MainScaffoldTexts
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.presentation.theme.GreenSelected

/**
 * Opciones de tabs para la sección de Workouts.
 */
enum class WorkoutTabOption {
    ALL_EXERCISES,
    MY_EXERCISES,
    STATISTICS
}

/**
 * Modelo de un tab individual.
 *
 * @property label Texto visible del tab
 * @property icon Icono del tab
 * @property option Opción que representa [WorkoutTabOption]
 * @property contentDescription Descripción para accesibilidad
 */
@Stable
data class TabItemModel(
    val label: String,
    val icon: ImageVector,
    val option: WorkoutTabOption,
    val contentDescription: String = label
)

/**
 * Parámetros del componente WorkoutTabHeader.
 *
 * @property tabs Lista de tabs a mostrar
 * @property selectedTab Tab actualmente seleccionado
 * @property filterContentDescription Descripción accesibilidad del botón filtro
 */
data class WorkoutTabHeaderParams(
    val tabs: List<TabItemModel>,
    val selectedTab: WorkoutTabOption,
    val filterContentDescription: String = ""
)

/**
 * Header con tabs personalizados para la sección de Workouts.
 *
 * Incluye un botón de filtro y tabs con iconos e indicador de selección.
 */
@Composable
fun WorkoutTabHeaderComponent(
    params: WorkoutTabHeaderParams,
    onTabSelected: (WorkoutTabOption) -> Unit,
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = params.filterContentDescription,
                modifier = Modifier
                    .clickable(onClick = onFilterClick)
                    .padding(4.dp)
            )
        }

        // Fila de tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            params.tabs.forEachIndexed { index, tab ->
                WorkoutTabItem(
                    item = tab,
                    isSelected = tab.option == params.selectedTab,
                    onClick = { onTabSelected(tab.option) },
                    modifier = Modifier.weight(1f)
                )

                if (index < params.tabs.lastIndex) {
                    VerticalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .padding(vertical = 8.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutTabItem(
    item: TabItemModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isSelected) GreenSelected else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.contentDescription,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = item.label,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Indicador de selección
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    color = if (isSelected) GreenSelected else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

/**
 * Retorna los tabs por defecto para la sección de Workouts.
 */
fun getDefaultWorkoutTabs(scaffoldTexts: MainScaffoldTexts): List<TabItemModel> = listOf(
    TabItemModel(
        label = scaffoldTexts.tabExercises,
        icon = Icons.Default.Person,
        option = WorkoutTabOption.ALL_EXERCISES
    ),
    TabItemModel(
        label = scaffoldTexts.tabMyExercises,
        icon = Icons.Default.HowToReg,
        option = WorkoutTabOption.MY_EXERCISES
    ),
    TabItemModel(
        label = scaffoldTexts.tabStatistics,
        icon = Icons.Default.QueryStats,
        option = WorkoutTabOption.STATISTICS
    )
)

@Preview(showBackground = true)
@Composable
private fun WorkoutTabHeaderPreview() {
    val lang = Spanish
    WorkoutTabHeaderComponent(
        params = WorkoutTabHeaderParams(
            tabs = getDefaultWorkoutTabs(lang.mainScaffoldTexts),
            selectedTab = WorkoutTabOption.ALL_EXERCISES,
            filterContentDescription = lang.accessibilityTexts.filter
        ),
        onTabSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun WorkoutTabHeaderMyExercisesPreview() {
    val lang = Spanish
    WorkoutTabHeaderComponent(
        params = WorkoutTabHeaderParams(
            tabs = getDefaultWorkoutTabs(lang.mainScaffoldTexts),
            selectedTab = WorkoutTabOption.MY_EXERCISES,
            filterContentDescription = lang.accessibilityTexts.filter
        ),
        onTabSelected = {}
    )
}
