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
 */
enum class TextFieldType {
    USER,
    PASSWORD,
    EMAIL,
    TEXT
}

private fun TextFieldType.getIcon(): ImageVector = when (this) {
    TextFieldType.USER -> Icons.Default.Person
    TextFieldType.PASSWORD -> Icons.Default.Lock
    TextFieldType.EMAIL -> Icons.Default.Email
    TextFieldType.TEXT -> Icons.Default.Edit
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
            borderColors = listOf(LightGreen, LightGreen, LightGreen),
            iconColor = LightGreen,
            textColor = Color.Black,
            placeholderColor = LightGreen.copy(alpha = 0.7f),
            cursorColor = LightGreen
        )
        val Error = TextFieldVisualState(
            borderColors = listOf(ErrorRed, ErrorRed, ErrorRed),
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
 * @property value Valor actual del campo
 * @property placeholder Texto placeholder
 * @property type Tipo de campo [TextFieldType]
 * @property isError Indica si hay error de validación
 */
data class TextFieldParams(
    val value: String,
    val placeholder: String,
    val type: TextFieldType,
    val isError: Boolean = false
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
        params.isError -> TextFieldVisualState.Error
        isFocused -> TextFieldVisualState.Focused
        else -> TextFieldVisualState.Normal
    }

    val visualTransformation = if (params.type == TextFieldType.PASSWORD && !isPasswordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    BasicTextField(
        value = params.value,
        onValueChange = onValueChange,
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
        singleLine = true,
        visualTransformation = visualTransformation,
        textStyle = PlaceholderStyle.copy(color = visualState.textColor),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = params.type.getIcon(),
                    contentDescription = null,
                    tint = visualState.iconColor
                )

                Spacer(Modifier.width(12.dp))

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
                        contentDescription = if (isPasswordVisible) "Ocultar" else "Mostrar",
                        tint = visualState.iconColor,
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
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
        }
    }
}
