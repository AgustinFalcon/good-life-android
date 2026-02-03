package com.agusstkd.goodlife.presentation.components.bottom.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.agusstkd.goodlife.R

/**
 * Modelo de datos para un item del Bottom Navigation.
 *
 * @property label Texto que se muestra debajo del ícono (resource ID)
 * @property icon Ícono cuando NO está seleccionado (outlined)
 * @property selectedIcon Ícono cuando está seleccionado (filled)
 * @property option Opción del menú que representa
 * @property contentDescription Descripción para accesibilidad
 */
@Stable
data class BottomNavItemModel(
    val label: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val option: BottomMenuOption,
    val contentDescription: String
)

/**
 * Items por defecto del Bottom Navigation de GoodLife.
 *
 * ## Orden:
 * 1. Diario (Home) - Tareas del día
 * 2. Ejercicios - Rutinas y workouts
 * 3. [FAB Central] - Agregar nueva entrada
 * 4. Comida - Registro de alimentación
 * 5. Más - Configuración y opciones
 *
 * @return Lista de 4 items (el FAB central no es un item)
 */
fun getDefaultBottomNavItems(): List<BottomNavItemModel> = listOf(
    BottomNavItemModel(
        label = R.string.tab_title_daily,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        option = BottomMenuOption.HOME,
        contentDescription = "Ir a diario de tareas"
    ),
    BottomNavItemModel(
        label = R.string.tab_title_workout,
        icon = Icons.Outlined.FitnessCenter,
        selectedIcon = Icons.Filled.FitnessCenter,
        option = BottomMenuOption.EXERCISES,
        contentDescription = "Ir a ejercicios y rutinas"
    ),
    BottomNavItemModel(
        label = R.string.tab_title_food,
        icon = Icons.Outlined.Restaurant,
        selectedIcon = Icons.Filled.Restaurant,
        option = BottomMenuOption.FOOD,
        contentDescription = "Ir a registro de comidas"
    ),
    BottomNavItemModel(
        label = R.string.tab_title_more,
        icon = Icons.Outlined.MoreHoriz,
        selectedIcon = Icons.Filled.MoreHoriz,
        option = BottomMenuOption.SETTINGS,
        contentDescription = "Más opciones y configuración"
    )
)
