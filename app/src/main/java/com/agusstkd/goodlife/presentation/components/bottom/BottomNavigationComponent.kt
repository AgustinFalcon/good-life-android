package com.agusstkd.goodlife.presentation.components.bottom

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomMenuOption
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomNavItemModel
import com.agusstkd.goodlife.presentation.components.bottom.model.getDefaultBottomNavItems
import com.agusstkd.goodlife.presentation.theme.GreenSelected
import com.agusstkd.goodlife.presentation.theme.LightGreen

/**
 * Parámetros de la barra de navegación inferior.
 *
 * @property items Lista de items de navegación
 * @property selectedTab Tab actualmente seleccionado
 * @property isFabRotated Si el FAB está rotado (modal abierto)
 */
data class BottomNavigationParams(
    val items: List<BottomNavItemModel>,
    val selectedTab: BottomMenuOption = BottomMenuOption.HOME,
    val isFabRotated: Boolean = false,
    val fabContentDescription: String = ""
)

/**
 * Barra de navegación inferior con FAB central.
 *
 * Contiene 4 tabs (2 a cada lado del FAB) con iconos y labels.
 * El FAB central tiene animación de rotación al abrir/cerrar el modal.
 *
 * @param params Configuración de la barra
 * @param onTabClick Callback al seleccionar un tab
 * @param onFabClick Callback al tocar el FAB
 * @param modifier Modificador de Compose
 * @param fabSize Tamaño del FAB (default: 56dp)
 */
@Composable
fun BottomNavigationComponent(
    params: BottomNavigationParams,
    onTabClick: (BottomMenuOption) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier,
    fabSize: Dp = 56.dp
) {
    val fabRotation by animateFloatAsState(
        targetValue = if (params.isFabRotated) 45f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "fab_rotation"
    )

    val middleIndex = params.items.size / 2
    val leftItems = params.items.take(middleIndex)
    val rightItems = params.items.drop(middleIndex)
    val fabSpace = fabSize + 32.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Items izquierdos
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    leftItems.forEach { item ->
                        BottomNavigationItem(
                            item = item,
                            isSelected = params.selectedTab == item.option,
                            onClick = { onTabClick(item.option) }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(fabSpace))

                // Items derechos
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rightItems.forEach { item ->
                        BottomNavigationItem(
                            item = item,
                            isSelected = params.selectedTab == item.option,
                            onClick = { onTabClick(item.option) }
                        )
                    }
                }
            }
        }

        // FAB central
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
                .size(fabSize),
            containerColor = if (params.isFabRotated) Color.Black else LightGreen,
            contentColor = Color.White,
            shape = CircleShape,
            onClick = onFabClick
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = params.fabContentDescription,
                modifier = Modifier
                    .size(fabSize * 0.5f)
                    .rotate(fabRotation)
            )
        }
    }
}

@Composable
private fun BottomNavigationItem(
    item: BottomNavItemModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tintColor = if (isSelected) GreenSelected else MaterialTheme.colorScheme.onSurface
    val icon = if (isSelected) item.selectedIcon else item.icon

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(color = GreenSelected, shape = RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.height(4.dp))
        }

        Icon(
            imageVector = icon,
            contentDescription = item.contentDescription,
            modifier = Modifier.size(24.dp),
            tint = tintColor
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = item.label,
            color = tintColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationPreview() {
    val lang = AppLanguage.Spanish
    BottomNavigationComponent(
        params = BottomNavigationParams(
            items = getDefaultBottomNavItems(lang.mainScaffoldTexts, lang.accessibilityTexts),
            selectedTab = BottomMenuOption.HOME,
            isFabRotated = false
        ),
        onTabClick = { },
        onFabClick = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationFabOpenPreview() {
    val lang = AppLanguage.Spanish
    BottomNavigationComponent(
        params = BottomNavigationParams(
            items = getDefaultBottomNavItems(lang.mainScaffoldTexts, lang.accessibilityTexts),
            selectedTab = BottomMenuOption.EXERCISES,
            isFabRotated = true,
            fabContentDescription = lang.accessibilityTexts.add
        ),
        onTabClick = { },
        onFabClick = { }
    )
}
