package com.agusstkd.goodlife.presentation.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.InfoBlue
import com.agusstkd.goodlife.presentation.theme.SuccessGreen
import kotlinx.coroutines.delay

/**
 * Variante visual del snackbar.
 *
 * Cada variante define colores de fondo, texto e icono
 * predeterminados para comunicar el tipo de mensaje al usuario.
 *
 * @property backgroundColor Color de fondo del snackbar.
 * @property contentColor Color del texto y del icono.
 * @property icon Icono predeterminado para la variante.
 */
enum class SnackbarVariant(
    val backgroundColor: Color,
    val contentColor: Color,
    val icon: ImageVector,
) {
    SUCCESS(
        backgroundColor = SuccessGreen,
        contentColor = Color.White,
        icon = Icons.Default.CheckCircle,
    ),
    ERROR(
        backgroundColor = ErrorRed,
        contentColor = Color.White,
        icon = Icons.Default.Error,
    ),
    INFO(
        backgroundColor = InfoBlue,
        contentColor = Color.White,
        icon = Icons.Default.Info,
    ),
}

/**
 * Parámetros de configuración del snackbar.
 *
 * @property message Texto principal del mensaje.
 * @property variant Variante visual ([SnackbarVariant.SUCCESS], [ERROR], [INFO]).
 * @property actionLabel Texto del botón de acción (ej: "Reintentar"). `null` para ocultar.
 * @property autoDismissMs Tiempo en ms antes del dismiss automático. `null` para desactivar.
 */
data class SnackbarParams(
    val message: String,
    val variant: SnackbarVariant = SnackbarVariant.INFO,
    val actionLabel: String? = null,
    val autoDismissMs: Long? = 4_000L,
)

/**
 * Snackbar reutilizable con variantes de color, icono y acción opcional.
 *
 * Aparece desde la parte inferior con animación de slide + fade.
 * Si [SnackbarParams.autoDismissMs] no es `null`, se oculta automáticamente.
 * Si [SnackbarParams.actionLabel] no es `null`, muestra un botón
 * (ej: "Reintentar" en errores de red).
 *
 * Se renderiza en un [Box] que ocupa [Modifier.fillMaxSize] para posicionarse
 * en la parte inferior — debe usarse dentro de un contenedor padre adecuado.
 *
 * @param params Configuración del snackbar.
 * @param isVisible Controla la visibilidad con animación.
 * @param onDismiss Callback cuando el snackbar se oculta (por timeout o acción).
 * @param onActionClick Callback del botón de acción. Se invoca antes de [onDismiss].
 * @param modifier Modificador de Compose.
 */
@Composable
fun SnackbarComponent(
    params: SnackbarParams,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (isVisible && params.autoDismissMs != null) {
        LaunchedEffect(params.message) {
            delay(params.autoDismissMs)
            onDismiss()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = params.variant.backgroundColor,
                shadowElevation = 6.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = params.variant.icon,
                        contentDescription = null,
                        tint = params.variant.contentColor,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = params.message,
                        color = params.variant.contentColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    if (params.actionLabel != null && onActionClick != null) {
                        TextButton(
                            onClick = {
                                onActionClick()
                                onDismiss()
                            },
                        ) {
                            Text(
                                text = params.actionLabel,
                                color = params.variant.contentColor,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun SnackbarComponentPreview() {
    GoodLifeTheme {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            var showSuccess by remember { mutableStateOf(true) }
            var showError by remember { mutableStateOf(true) }
            var showInfo by remember { mutableStateOf(true) }

            Box(modifier = Modifier.fillMaxWidth().size(80.dp)) {
                SnackbarComponent(
                    params = SnackbarParams(
                        message = "Tarea creada con éxito",
                        variant = SnackbarVariant.SUCCESS,
                    ),
                    isVisible = showSuccess,
                    onDismiss = { showSuccess = false },
                )
            }

            Box(modifier = Modifier.fillMaxWidth().size(80.dp)) {
                SnackbarComponent(
                    params = SnackbarParams(
                        message = "Error de conexión",
                        variant = SnackbarVariant.ERROR,
                        actionLabel = "Reintentar",
                    ),
                    isVisible = showError,
                    onDismiss = { showError = false },
                    onActionClick = {},
                )
            }

            Box(modifier = Modifier.fillMaxWidth().size(80.dp)) {
                SnackbarComponent(
                    params = SnackbarParams(
                        message = "Nueva versión disponible",
                        variant = SnackbarVariant.INFO,
                    ),
                    isVisible = showInfo,
                    onDismiss = { showInfo = false },
                )
            }
        }
    }
}
