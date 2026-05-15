package com.agusstkd.goodlife.core.datetime.compose

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.agusstkd.goodlife.core.datetime.toLocalDateUtc
import com.agusstkd.goodlife.core.datetime.toLongUtc
import kotlinx.datetime.LocalDate

/**
 * Extensiones de [DatePickerState] para integrar Material3 DatePicker
 * con [LocalDate] de kotlinx.datetime.
 *
 * Separado de LocalDateExtensions.kt porque [DatePickerState] es
 * Android/Compose-specific — no es KMP-compatible.
 *
 * @see com.agusstkd.goodlife.core.datetime.LocalDateExtensions
 */

@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerState.toLocalDate(): LocalDate? = selectedDateMillis?.toLocalDateUtc()

/**
 * Crea y recuerda un [DatePickerState] inicializado con [date].
 *
 * Usa [key] para que si [date] cambia desde afuera (ej: distintos campos
 * de fecha abriendo el mismo picker), el estado se recrea con el nuevo
 * valor inicial en lugar de mantener el anterior.
 *
 * @param date Fecha inicial seleccionada, o null para picker vacío.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun rememberDatePickerStateFor(date: LocalDate?): DatePickerState =
    key(date) {
        rememberDatePickerState(
            initialSelectedDateMillis = date?.toLongUtc()
        )
    }
