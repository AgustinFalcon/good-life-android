package com.agusstkd.goodlife.presentation.components.common

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
 * Tipo de campo de texto que determina el icono y comportamiento.
 *
 * - [USER], [PASSWORD], [EMAIL], [TEXT]: campo de una sola línea con icono leading.
 * - [MULTILINE]: área de texto sin icono leading, altura configurable via [TextFieldParams.minLines].
 */
enum class TextFieldType {
    USER,
    PASSWORD,
    EMAIL,
    TEXT,
    MULTILINE,
}

/** Devuelve el icono leading, o null si el tipo no usa icono (ej: [TextFieldType.MULTILINE]). */
private fun TextFieldType.getIcon(): ImageVector? = when (this) {
    TextFieldType.USER -> Icons.Default.Person
    TextFieldType.PASSWORD -> Icons.Default.Lock
    TextFieldType.EMAIL -> Icons.Default.Email
    TextFieldType.TEXT -> Icons.Default.Edit
    TextFieldType.MULTILINE -> null
}

private fun TextFieldType.needsPasswordToggle(): Boolean = this == TextFieldType.PASSWORD

/**
 * Estado visual del campo según focus/error.
 *
 * @property borderColors Colores del borde (gradiente)
 * @property iconColor Color del icono leading
 * @property textColor Color del texto
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
        val Normal = TextFieldVisualState(
            borderColors = listOf(DarkGreen, LightGreen, DegradeBackground5),
            iconColor = DarkGreen,
            textColor = Color.Black,
            placeholderColor = TextTertiary,
            cursorColor = DarkGreen
        )
        val Focused = TextFieldVisualState(
            borderColors = listOf(LightGreen, LightGreen.copy(alpha = 0.9f), LightGreen.copy(alpha = 0.7f)),
            iconColor = LightGreen,
            textColor = Color.Black,
            placeholderColor = LightGreen.copy(alpha = 0.7f),
            cursorColor = LightGreen
        )
        val Disabled = TextFieldVisualState(
            borderColors = listOf(Color.Gray.copy(alpha = 0.45f), Color.Gray.copy(alpha = 0.35f)),
            iconColor = TextTertiary,
            textColor = TextTertiary,
            placeholderColor = TextTertiary,
            cursorColor = Color.Transparent
        )
        val Error = TextFieldVisualState(
            borderColors = listOf(ErrorRed, ErrorRed.copy(alpha = 0.9f), ErrorRed.copy(alpha = 0.7f)),
            iconColor = ErrorRed,
            textColor = Color.Black,
            placeholderColor = ErrorRed.copy(alpha = 0.7f),
            cursorColor = ErrorRed
        )
    }
}

/**
 * Parámetros del campo de texto.
 *
 * @property value Valor actual del campo.
 * @property placeholder Texto placeholder.
 * @property type Tipo de campo [TextFieldType].
 * @property isError Indica si hay error de validación.
 * @property enabled Define si el campo acepta interacción.
 * @property minLines Número mínimo de líneas visibles. Solo aplica cuando [type] es [TextFieldType.MULTILINE].
 * @property maxLines Número máximo de líneas antes de hacer scroll. Solo aplica cuando [type] es [TextFieldType.MULTILINE].
 */
data class TextFieldParams(
    val value: String,
    val placeholder: String,
    val type: TextFieldType,
    val isError: Boolean = false,
    val enabled: Boolean = true,
    val minLines: Int = 1,
    val maxLines: Int = Int.MAX_VALUE,
    val passwordToggleHide: String = "",
    val passwordToggleShow: String = "",
)

/**
 * Campo de texto con borde gradiente y estados visuales.
 *
 * Soporta diferentes tipos de input (usuario, password, email)
 * con iconos automáticos y toggle de visibilidad para passwords.
 *
 * @param params Configuración del campo
 * @param onValueChange Callback cuando el valor cambia
 * @param modifier Modificador de Compose
 */
@Composable
fun TextFieldComponent(
    params: TextFieldParams,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }

    val visualState = when {
        !params.enabled -> TextFieldVisualState.Disabled
        params.isError -> TextFieldVisualState.Error
        isFocused -> TextFieldVisualState.Focused
        else -> TextFieldVisualState.Normal
    }

    val visualTransformation = if (params.type == TextFieldType.PASSWORD && !isPasswordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    val isMultiline = params.type == TextFieldType.MULTILINE
    val leadingIcon = params.type.getIcon()

    BasicTextField(
        value = params.value,
        onValueChange = onValueChange,
        enabled = params.enabled,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(colors = visualState.borderColors),
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(visualState.cursorColor),
        singleLine = !isMultiline,
        minLines = if (isMultiline) params.minLines else 1,
        maxLines = if (isMultiline) params.maxLines else 1,
        visualTransformation = visualTransformation,
        textStyle = PlaceholderStyle.copy(color = visualState.textColor),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = if (isMultiline) Alignment.Top else Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = visualState.iconColor
                    )
                    Spacer(Modifier.width(12.dp))
                }

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

                if (params.type.needsPasswordToggle()) {
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) params.passwordToggleHide else params.passwordToggleShow,
                        tint = visualState.iconColor,
                        modifier = Modifier.clickable(enabled = params.enabled) {
                            isPasswordVisible = !isPasswordVisible
                        }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true, name = "TextFieldComponent — todos los tipos")
@Composable
private fun TextFieldComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextFieldComponent(
                params = TextFieldParams(
                    value = "",
                    placeholder = "Correo electrónico",
                    type = TextFieldType.USER
                ),
                onValueChange = {}
            )
            TextFieldComponent(
                params = TextFieldParams(
                    value = "agustin@email.com",
                    placeholder = "Correo electrónico",
                    type = TextFieldType.EMAIL
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
            TextFieldComponent(
                params = TextFieldParams(
                    value = "agustin@email.com",
                    placeholder = "Correo electrónico",
                    type = TextFieldType.EMAIL,
                    enabled = false
                ),
                onValueChange = {}
            )
            TextFieldComponent(
                params = TextFieldParams(
                    value = "",
                    placeholder = "Añade detalles sobre la preparación...",
                    type = TextFieldType.MULTILINE,
                    minLines = 4,
                    maxLines = 6,
                ),
                onValueChange = {}
            )
        }
    }
}
