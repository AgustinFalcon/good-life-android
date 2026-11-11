package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import androidx.compose.runtime.Stable
import com.agusstkd.goodlife.core.datetime.language.WorkoutTexts

/**
 * Campo editable de un set en el detalle. No es un string mágico.
 *
 * La etiqueta vive en el tipo y se resuelve con [WorkoutTexts]; la Screen no arma un `when` de copy.
 */
@Stable
sealed interface WorkoutSetField {
    data object Weight : WorkoutSetField
    data object Reps : WorkoutSetField
    data object Rpe : WorkoutSetField

    /**
     * Copy localizado del campo. Única fuente de label para peso, reps y RPE.
     */
    fun label(texts: WorkoutTexts): String = when (this) {
        Weight -> texts.setWeightLabel
        Reps -> texts.setRepsLabel
        Rpe -> texts.setRpeLabel
    }
}
