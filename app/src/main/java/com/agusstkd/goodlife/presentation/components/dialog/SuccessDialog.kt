package com.agusstkd.goodlife.presentation.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.agusstkd.goodlife.R
import com.agusstkd.goodlife.presentation.theme.SuccessGreen
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextSecondary
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

/**
 * Parámetros de configuración del dialog de éxito.
 *
 * @property title Título principal (ej: "Tarea Creada").
 * @property subtitle Subtítulo descriptivo (ej: nombre de la tarea).
 * @property detail Detalle adicional debajo del subtítulo (ej: fecha).
 */
data class SuccessDialogParams(
    val title: String,
    val subtitle: String? = null,
    val detail: String? = null,
)

/**
 * Dialog modal con animación Lottie de éxito.
 *
 * Muestra una animación de check verde que se reproduce una sola vez.
 * Cuando la animación termina, invoca [onAnimationFinished] para
 * que el Owner pueda navegar de vuelta.
 *
 * No tiene botón de cerrar — la transición es automática.
 *
 * @param params Configuración visual del dialog.
 * @param onAnimationFinished Callback al terminar la animación Lottie.
 */
@Composable
fun SuccessDialog(
    params: SuccessDialogParams,
    onAnimationFinished: () -> Unit,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            delay(600)
            onAnimationFinished()
        }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(120.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = params.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen,
                    textAlign = TextAlign.Center,
                )

                params.subtitle?.let { subtitle ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                }

                params.detail?.let { detail ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
