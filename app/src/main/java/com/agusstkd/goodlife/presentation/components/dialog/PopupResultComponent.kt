package com.agusstkd.goodlife.presentation.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.agusstkd.goodlife.R
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.HabitAccent
import com.agusstkd.goodlife.presentation.theme.NutritionAccent
import com.agusstkd.goodlife.presentation.theme.SuccessGreen
import com.agusstkd.goodlife.presentation.theme.TaskAccent
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextSecondary
import com.agusstkd.goodlife.presentation.theme.WorkoutAccent
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// Enums y data class
// ─────────────────────────────────────────────────────────────────────────────

/** Estado visual del popup de resultado. */
enum class PopupResultState { LOADING, SUCCESS, ERROR }

/**
 * Tipo de ítem que se está creando/procesando.
 *
 * Define el ícono central y el color de acento usados en el estado de carga:
 * el ícono aparece centrado dentro del [CircularProgressIndicator].
 * El mismo [accentColor] debe usarse en los inputs y el stepper del wizard
 * correspondiente para mantener consistencia visual por entidad.
 *
 * @property icon Ícono representativo del tipo de ítem.
 * @property accentColor Color del [CircularProgressIndicator] y del ícono.
 */
enum class CreationItemType(
    val icon: ImageVector,
    val accentColor: Color,
) {
    WORKOUT(Icons.Default.FitnessCenter, WorkoutAccent),
    HABIT(Icons.Default.Psychology, HabitAccent),
    TASK(Icons.Default.CheckCircle, TaskAccent),
    MEAL(Icons.Default.Restaurant, NutritionAccent),
}

/**
 * Parámetros de configuración del [PopupResultComponent].
 *
 * @property state Estado actual del popup: [PopupResultState.LOADING],
 *   [PopupResultState.SUCCESS] o [PopupResultState.ERROR].
 * @property itemType Tipo de ítem que se está creando; determina ícono y color de acento.
 * @property title Título principal mostrado en el popup.
 * @property message Mensaje descriptivo debajo del título.
 * @property detail Información extra opcional (p.ej. fecha en success). Default: null.
 * @property loadingProgress Progreso determinado entre 0f y 1f; null = indeterminado. Default: null.
 * @property retryLabel Texto del botón primario en estado de error. Default: "Reintentar".
 * @property cancelLabel Texto del botón secundario en estado de error. Default: "Cancelar".
 */
data class PopupResultParams(
    val state: PopupResultState,
    val itemType: CreationItemType,
    val title: String,
    val message: String,
    val detail: String? = null,
    val loadingProgress: Float? = null,
    val retryLabel: String = "Reintentar",
    val cancelLabel: String = "Cancelar",
)

// ─────────────────────────────────────────────────────────────────────────────
// Composable principal
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Popup reutilizable de resultado para operaciones asíncronas.
 *
 * Unifica tres estados en un único componente configurable via [PopupResultParams]:
 * - **LOADING**: ícono del tipo centrado dentro de un [CircularProgressIndicator] +
 *   animación Lottie en loop. No se puede cerrar con back press ni toque exterior.
 * - **SUCCESS**: animación Lottie una sola vez → auto-cierre via [onSuccess] con
 *   un delay de 600ms. No se puede cerrar manualmente.
 * - **ERROR**: animación Lottie una sola vez + botones configurables para reintentar
 *   o cancelar. Se puede cerrar con back press o toque exterior (llama [onCancel]).
 *
 * @param params Configuración visual y textual del popup.
 * @param modifier Modificador aplicado al [Column] interior del [Surface].
 * @param onSuccess Llamado automáticamente al finalizar la animación de éxito (+ 600ms).
 * @param onRetry Llamado al presionar el botón primario en estado [PopupResultState.ERROR].
 * @param onCancel Llamado al presionar el botón secundario o al cerrar en estado [PopupResultState.ERROR].
 */
@Composable
fun PopupResultComponent(
    modifier: Modifier = Modifier,
    params: PopupResultParams,
    onSuccess: () -> Unit = {},
    onRetry: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    val dismissible = params.state == PopupResultState.ERROR

    Dialog(
        onDismissRequest = { if (dismissible) onCancel() },
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible,
        ),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (params.state) {
                    PopupResultState.LOADING -> LoadingContent(params = params)
                    PopupResultState.SUCCESS -> SuccessContent(params = params, onSuccess = onSuccess)
                    PopupResultState.ERROR   -> ErrorContent(params = params, onRetry = onRetry, onCancel = onCancel)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Composables internos
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Contenido del estado [PopupResultState.LOADING].
 *
 * Muestra el ícono de [PopupResultParams.itemType] centrado dentro de un
 * [CircularProgressIndicator] del mismo [CreationItemType.accentColor],
 * seguido de una animación Lottie en loop y el título/mensaje descriptivo.
 *
 * No expone callbacks — el owner controla cuándo cambiar el estado a SUCCESS o ERROR.
 *
 * @param params Configuración del popup; se usan [PopupResultParams.itemType],
 *   [PopupResultParams.loadingProgress], [PopupResultParams.title] y [PopupResultParams.message].
 */
@Composable
private fun LoadingContent(params: PopupResultParams) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = params.itemType.icon,
            contentDescription = null,
            tint = params.itemType.accentColor,
        )

        if (params.loadingProgress != null) {
            CircularProgressIndicator(
                modifier = Modifier.size(96.dp),
                progress = { params.loadingProgress },
                color = params.itemType.accentColor,
                strokeWidth = 4.dp,
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(96.dp),
                color = params.itemType.accentColor,
                strokeWidth = 4.dp,
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(80.dp),
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = params.title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = params.message,
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        textAlign = TextAlign.Center,
    )
}

/**
 * Contenido del estado [PopupResultState.SUCCESS].
 *
 * Reproduce la animación Lottie de éxito una sola vez. Al llegar al 100%,
 * espera 600ms y luego invoca [onSuccess] para que el owner navegue de vuelta.
 * No tiene botones — la transición es automática.
 *
 * @param params Configuración del popup; se usan [PopupResultParams.title],
 *   [PopupResultParams.message] y [PopupResultParams.detail].
 * @param onSuccess Callback invocado automáticamente al finalizar la animación.
 */
@Composable
private fun SuccessContent(
    params: PopupResultParams,
    onSuccess: () -> Unit,
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
            onSuccess()
        }
    }

    LottieAnimation(
        modifier = Modifier.size(120.dp),
        composition = composition,
        progress = { progress },
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = params.title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = SuccessGreen,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = params.message,
        style = MaterialTheme.typography.bodyLarge,
        color = TextPrimary,
        textAlign = TextAlign.Center,
    )

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

/**
 * Contenido del estado [PopupResultState.ERROR].
 *
 * Reproduce la animación Lottie de error una sola vez, muestra el título en
 * [ErrorRed] y expone dos botones configurables para que el usuario decida
 * si reintenta la operación o cancela.
 *
 * @param params Configuración del popup; se usan [PopupResultParams.title],
 *   [PopupResultParams.message], [PopupResultParams.detail],
 *   [PopupResultParams.retryLabel] y [PopupResultParams.cancelLabel].
 * @param onRetry Callback del botón primario ([ButtonVariant.PRIMARY]).
 * @param onCancel Callback del botón secundario ([ButtonVariant.OUTLINE]).
 */
@Composable
private fun ErrorContent(
    params: PopupResultParams,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.error_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
    )

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
        color = ErrorRed,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = params.message,
        style = MaterialTheme.typography.bodyLarge,
        color = TextPrimary,
        textAlign = TextAlign.Center,
    )

    params.detail?.let { detail ->
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = detail,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ButtonComponent(
            params = ButtonParams(text = params.retryLabel, variant = ButtonVariant.PRIMARY),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        )
        ButtonComponent(
            params = ButtonParams(text = params.cancelLabel, variant = ButtonVariant.OUTLINE),
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "PopupResult — Loading (MEAL)", showBackground = true)
@Composable
private fun PreviewLoading() {
    GoodLifeTheme {
        PopupResultComponent(
            params = PopupResultParams(
                state = PopupResultState.LOADING,
                itemType = CreationItemType.MEAL,
                title = "Creando plan...",
                message = "Estamos preparando tu plan de comidas",
                loadingProgress = 0.65f,
            ),
        )
    }
}

@Preview(name = "PopupResult — Success (WORKOUT)", showBackground = true)
@Composable
private fun PreviewSuccess() {
    GoodLifeTheme {
        PopupResultComponent(
            params = PopupResultParams(
                state = PopupResultState.SUCCESS,
                itemType = CreationItemType.WORKOUT,
                title = "¡Rutina Creada!",
                message = "Tu rutina de entrenamiento fue guardada",
                detail = "Viernes 21 de Marzo, 2025",
            ),
        )
    }
}

@Preview(name = "PopupResult — Error (TASK)", showBackground = true)
@Composable
private fun PreviewError() {
    GoodLifeTheme {
        PopupResultComponent(
            params = PopupResultParams(
                state = PopupResultState.ERROR,
                itemType = CreationItemType.TASK,
                title = "Error de conexión",
                message = "No se pudo crear la tarea. Verificá tu conexión a internet.",
            ),
            onRetry = {},
            onCancel = {},
        )
    }
}
