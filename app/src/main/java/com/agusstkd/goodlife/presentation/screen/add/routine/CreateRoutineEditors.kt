package com.agusstkd.goodlife.presentation.screen.add.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.language.CreateItemSharedTexts
import com.agusstkd.goodlife.core.datetime.language.CreateRoutineTexts
import com.agusstkd.goodlife.domain.model.training.SetDraft
import com.agusstkd.goodlife.presentation.theme.ErrorRed
import com.agusstkd.goodlife.presentation.theme.TextPrimary
import com.agusstkd.goodlife.presentation.theme.TextSecondary

/**
 * Dialog para nombrar un workout nuevo en el wizard de rutina.
 *
 * El estado local del nombre vive acá: el Owner solo lo muestra y reenvía confirm/dismiss.
 *
 * @param placeholder Placeholder del campo de nombre.
 * @param confirmLabel Label del botón confirmar.
 * @param cancelLabel Label del botón cancelar.
 * @param onConfirm Nombre ingresado (ya recortado por el caller si hace falta).
 * @param onDismiss Cierre sin confirmar.
 */
@Composable
internal fun AddWorkoutDialog(
    placeholder: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelLabel)
            }
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(placeholder) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}

/**
 * Bottom sheet para editar sets y notas de un ejercicio del wizard de rutina.
 *
 * El estado local de sets/notas vive acá. El Owner solo lo muestra y reenvía confirm/dismiss.
 *
 * @param exerciseName Nombre del ejercicio.
 * @param muscleGroupName Grupo muscular mostrado como subtítulo.
 * @param initialSets Sets iniciales del ejercicio.
 * @param initialNotes Notas iniciales, o null.
 * @param routineTexts Copy del wizard de rutina.
 * @param sharedTexts Copy compartido de creación (cancel/confirm).
 * @param onConfirm Sets numerados y notas opcionales.
 * @param onDismiss Cierre sin guardar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SetEditorBottomSheet(
    exerciseName: String,
    muscleGroupName: String,
    initialSets: List<SetDraft>,
    initialNotes: String?,
    routineTexts: CreateRoutineTexts,
    sharedTexts: CreateItemSharedTexts,
    onConfirm: (List<SetDraft>, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sets = remember { mutableStateListOf(*initialSets.toTypedArray()) }
    var notes by remember { mutableStateOf(initialNotes ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = exerciseName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
            Text(
                text = muscleGroupName,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("#", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelMedium)
                Text(routineTexts.repsLabel, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text(routineTexts.weightLabel, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.width(32.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    OutlinedTextField(
                        value = set.targetReps.toString(),
                        onValueChange = { value ->
                            val reps = value.toIntOrNull() ?: 0
                            sets[index] = set.copy(targetReps = reps)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = set.targetWeight?.toString() ?: "",
                        onValueChange = { value ->
                            val weight = value.toDoubleOrNull()
                            sets[index] = set.copy(targetWeight = weight)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )
                    if (sets.size > 1) {
                        IconButton(onClick = { sets.removeAt(index) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = ErrorRed,
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val nextNumber = sets.size + 1
                    sets.add(SetDraft(setNumber = nextNumber))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(routineTexts.addSetButton)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(routineTexts.notesPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(sharedTexts.cancelLabel)
                }
                TextButton(
                    onClick = {
                        val numberedSets = sets.mapIndexed { i, s ->
                            s.copy(setNumber = i + 1)
                        }
                        onConfirm(numberedSets, notes.takeIf { it.isNotBlank() })
                    },
                    modifier = Modifier.weight(1f),
                    enabled = sets.isNotEmpty() && sets.all { it.targetReps > 0 },
                ) {
                    Text(sharedTexts.confirmLabel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
