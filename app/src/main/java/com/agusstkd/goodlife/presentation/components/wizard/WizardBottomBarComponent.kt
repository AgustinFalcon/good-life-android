package com.agusstkd.goodlife.presentation.components.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme

/**
 * Bottom bar reutilizable para wizards de creación de items.
 *
 * Muestra un único botón primario cuyo texto cambia según el paso
 * (ej: "Siguiente" en pasos intermedios, "Crear plan" en el último paso).
 *
 * Usado por [CreateMealPlanScreen] y [CreateRoutineScreen] (o cualquier wizard
 * que necesite un bottom bar consistente). Parametrizar [WizardBottomBarParams]
 * para controlar el texto, el estado habilitado y el estado de carga.
 *
 * @param params Configuración visual y funcional del bottom bar.
 * @param onPrimary Callback del botón principal (avanzar al siguiente paso o crear).
 * @param modifier Modificador externo.
 */
@Composable
fun WizardBottomBarComponent(
    params: WizardBottomBarParams,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        ButtonComponent(
            params = ButtonParams(
                text = params.primaryText,
                enabled = params.primaryEnabled && !params.isLoading,
                isLoading = params.isLoading,
                variant = ButtonVariant.PRIMARY,
            ),
            onClick = onPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Parámetros de configuración para [WizardBottomBarComponent].
 *
 * @param primaryText Texto del botón principal (ej: "Siguiente", "Crear plan").
 * @param primaryEnabled Si el botón principal está habilitado.
 * @param isLoading Si se muestra el estado de carga (spinner en el botón).
 */
data class WizardBottomBarParams(
    val primaryText: String,
    val primaryEnabled: Boolean,
    val isLoading: Boolean = false,
)

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "WizardBottomBar — Siguiente habilitado", showBackground = true)
@Composable
private fun PreviewBottomBarEnabled() {
    GoodLifeTheme {
        WizardBottomBarComponent(
            params = WizardBottomBarParams(
                primaryText = "Siguiente",
                primaryEnabled = true,
            ),
            onPrimary = {},
        )
    }
}

@Preview(name = "WizardBottomBar — Crear plan deshabilitado", showBackground = true)
@Composable
private fun PreviewBottomBarDisabled() {
    GoodLifeTheme {
        WizardBottomBarComponent(
            params = WizardBottomBarParams(
                primaryText = "Crear plan",
                primaryEnabled = false,
            ),
            onPrimary = {},
        )
    }
}

@Preview(name = "WizardBottomBar — Cargando", showBackground = true)
@Composable
private fun PreviewBottomBarLoading() {
    GoodLifeTheme {
        WizardBottomBarComponent(
            params = WizardBottomBarParams(
                primaryText = "Crear plan",
                primaryEnabled = false,
                isLoading = true,
            ),
            onPrimary = {},
        )
    }
}
