package com.agusstkd.goodlife.presentation.components.bottom.datepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.core.datetime.compose.rememberDatePickerStateFor
import com.agusstkd.goodlife.core.datetime.compose.toLocalDate
import com.agusstkd.goodlife.core.datetime.language.DatePickerTexts
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerBottomSheetComponent(
    modifier: Modifier = Modifier,
    texts: DatePickerTexts,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate? = null,
) {
    val pickerState = rememberDatePickerStateFor(date = initialDate)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = texts.cancel)
                }

                Text(text = texts.title)

                TextButton(
                    onClick = {
                        pickerState.toLocalDate()?.let { date ->
                            onDateSelected(date)
                            onDismiss()
                        }
                    }
                ) {
                    Text(text = texts.confirm)
                }
            }

            DatePicker(
                state = pickerState,
                showModeToggle = false,
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = {
                    pickerState.toLocalDate()?.let { date ->
                        onDateSelected(date)
                        onDismiss()
                    }
                }
            ) {
                Text(text = texts.confirm)
            }
        }
    }
}
