package com.agusstkd.goodlife.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DegradeBackground5
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.PlaceholderStyle
import com.agusstkd.goodlife.presentation.theme.TextTertiary

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 * TEXT FIELD COMPONENT - COMPONENTE REUTILIZABLE PARA INPUTS
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 *
 * ## Arquitectura (3 capas):
 * 1. **TextFieldType** → Define QUÉ tipo es (USER, PASSWORD, EMAIL, TEXT)
 * 2. **TextFieldVisualState** → Define CÓMO se ve (colores según estado)
 * 3. **TextFieldParams** → Agrupa toda la configuración
 *
 * ## Estados Visuales:
 * - **NORMAL**: Border degradado (DarkGreen → LightGreen → Aqua)
 * - **FOCUSED**: Border verde sólido (LightGreen)
 * - **ERROR**: Border rojo sólido (ErrorRed)
 *
 * ## Ejemplo de uso:
 * ```kotlin
 * TextFieldComponent(
 *     params = TextFieldParams(
 *         value = userName,
 *         placeholder = "Correo electrónico",
 *         type = TextFieldType.EMAIL,
 *         isError = isUserNameError
 *     ),
 *     onValueChange = { viewModel.updateUserName(it) }
 * )
 * ```
 */

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 1. TYPE - Define el TIPO de campo
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Tipo de TextField.
 * Define el comportamiento y el icono del campo.
 */
enum class TextFieldType {
    /** Usuario: icono Person */
    USER,
    /** Contraseña: icono Lock + toggle visibilidad + mask */
    PASSWORD,
    /** Email: icono Email */
    EMAIL,
    /** Texto genérico: icono Edit */
    TEXT
}

/**
 * Obtiene el icono según el tipo de campo.
 */
private fun TextFieldType.getIcon(): ImageVector {
    return when (this) {
        TextFieldType.USER -> Icons.Default.Person
        TextFieldType.PASSWORD -> Icons.Default.Lock
        TextFieldType.EMAIL -> Icons.Default.Email
        TextFieldType.TEXT -> Icons.Default.Edit
    }
}

/**
 * Indica si el campo necesita trailing icon para toggle de visibilidad.
 * Solo PASSWORD necesita el toggle de visibilidad.
 */
private fun TextFieldType.needsPasswordToggle(): Boolean {
    return this == TextFieldType.PASSWORD
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 2. STATE - Define CÓMO SE VE (colores)
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Estado visual del TextField.
 * Define los colores que se usan en cada estado.
 *
 * @property borderColors Lista de colores para degradado o sólido
 * @property iconColor Color del icono leading/trailing
 * @property textColor Color del texto ingresado
 * @property placeholderColor Color del placeholder
 * @property cursorColor Color del cursor
 */
data class TextFieldVisualState(
    val borderColors: List<Color>,
    val iconColor: Color,
    val textColor: Color,
    val placeholderColor: Color,
    val cursorColor: Color
) {
    companion object {
        /**
         * Estado NORMAL (sin foco, sin error).
         * Border: Degradado de 3 colores.
         */
        val Normal = TextFieldVisualState(
            borderColors = listOf(DarkGreen, LightGreen, DegradeBackground5),
            iconColor = DarkGreen,
            textColor = Color.Black,
            placeholderColor = TextTertiary,
            cursorColor = DarkGreen
        )

        /**
         * Estado FOCUSED (usuario está escribiendo).
         * Border: Verde sólido.
         */
        val Focused = TextFieldVisualState(
            borderColors = listOf(LightGreen, LightGreen, LightGreen),
            iconColor = LightGreen,
            textColor = Color.Black,
            placeholderColor = LightGreen.copy(alpha = 0.7f),
            cursorColor = LightGreen
        )

        /**
         * Estado ERROR (validación falló).
         * Border: Rojo sólido.
         */
        val Error = TextFieldVisualState(
            borderColors = listOf(ErrorRed, ErrorRed, ErrorRed),
            iconColor = ErrorRed,
            textColor = Color.Black,
            placeholderColor = ErrorRed.copy(alpha = 0.7f),
            cursorColor = ErrorRed
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 3. PARAMS - Configuración del TextField
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * Parámetros de configuración del TextField.
 * Agrupa toda la información necesaria para renderizar el componente.
 *
 * @property value Texto actual (del ViewModel)
 * @property placeholder Texto de ayuda cuando está vacío
 * @property type Tipo de campo (USER/PASSWORD/EMAIL/TEXT)
 * @property isError Indica si el campo tiene error de validación
 */
data class TextFieldParams(
    val value: String,
    val placeholder: String,
    val type: TextFieldType,
    val isError: Boolean = false
)

// ═══════════════════════════════════════════════════════════════════════════════════════════
// 4. COMPONENTE - El TextField
// ═══════════════════════════════════════════════════════════════════════════════════════════

/**
 * TextField reutilizable y dinámico con gradiente en borders.
 *
 * ## Responsabilidades del componente:
 * - Renderizar el campo según el tipo
 * - Detectar foco del usuario
 * - Manejar visibilidad de password
 * - Aplicar gradientes según estado
 *
 * ## NO es responsabilidad del componente:
 * - Validar datos (lo hace el ViewModel)
 * - Mantener el estado del texto (lo hace el ViewModel)
 * - Decidir si hay error (lo hace el ViewModel)
 *
 * @param params Configuración del campo (value, placeholder, type, isError)
 * @param onValueChange Callback cuando el texto cambia
 * @param modifier Modificador de Compose
 */
@Composable
fun TextFieldComponent(
    params: TextFieldParams,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Detectar si el campo tiene FOCO
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Manejar visibilidad de PASSWORD (estado local del componente)
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Determinar el ESTADO VISUAL según la situación
    // Prioridad: Error > Focused > Normal
    val visualState = when {
        params.isError -> TextFieldVisualState.Error
        isFocused -> TextFieldVisualState.Focused
        else -> TextFieldVisualState.Normal
    }

    // Obtener el ICONO según el tipo
    val leadingIcon = params.type.getIcon()

    // Configurar VISUAL TRANSFORMATION (para passwords)
    val visualTransformation = if (params.type == TextFieldType.PASSWORD && !isPasswordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    // RENDERIZAR el TextField
    BasicTextField(
        value = params.value,
        onValueChange = { onValueChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(colors = visualState.borderColors),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(visualState.cursorColor),
        singleLine = true,
        visualTransformation = visualTransformation,
        textStyle = PlaceholderStyle.copy(color = visualState.textColor),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // LEADING ICON (icono izquierdo)
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = visualState.iconColor,
                )

                Spacer(Modifier.width(12.dp))

                // CAMPO DE TEXTO con PLACEHOLDER
                Box(modifier = Modifier.weight(1f)) {
                    if (params.value.isEmpty()) {
                        Text(
                            text = params.placeholder,
                            color = visualState.placeholderColor,
                            style = PlaceholderStyle
                        )
                    }
                    innerTextField()
                }

                // TRAILING ICON (solo para PASSWORD)
                if (params.type.needsPasswordToggle()) {
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (isPasswordVisible) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        },
                        tint = visualState.iconColor,
                        modifier = Modifier.clickable {
                            isPasswordVisible = !isPasswordVisible
                        }
                    )
                }
            }
        }
    )
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TextFieldComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "═══ USER FIELD ═══",
                style = MaterialTheme.typography.titleMedium
            )

            TextFieldComponent(
                params = TextFieldParams(
                    value = "",
                    placeholder = "Correo electrónico o usuario",
                    type = TextFieldType.USER,
                    isError = false
                ),
                onValueChange = {}
            )

            TextFieldComponent(
                params = TextFieldParams(
                    value = "agustin",
                    placeholder = "Correo electrónico o usuario",
                    type = TextFieldType.USER,
                    isError = false
                ),
                onValueChange = {}
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "═══ PASSWORD FIELD ═══",
                style = MaterialTheme.typography.titleMedium
            )

            TextFieldComponent(
                params = TextFieldParams(
                    value = "",
                    placeholder = "Contraseña",
                    type = TextFieldType.PASSWORD,
                    isError = false
                ),
                onValueChange = {}
            )

            TextFieldComponent(
                params = TextFieldParams(
                    value = "1234",
                    placeholder = "Contraseña",
                    type = TextFieldType.PASSWORD,
                    isError = true
                ),
                onValueChange = {}
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "═══ EMAIL FIELD ═══",
                style = MaterialTheme.typography.titleMedium
            )

            TextFieldComponent(
                params = TextFieldParams(
                    value = "agustin@gmail.com",
                    placeholder = "Email",
                    type = TextFieldType.EMAIL,
                    isError = false
                ),
                onValueChange = {}
            )
        }
    }
}
