package de.ljz.questify.feature.quests.presentation.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.ljz.questify.R
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetDueDateDialog(
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedDateTimeMillis: Long?
) {
    val initialMillis = initialSelectedDateTimeMillis ?: System.currentTimeMillis()

    val initialDateMillis = remember(initialMillis) {
        val localCal = Calendar.getInstance().apply { timeInMillis = initialMillis }
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                localCal.get(Calendar.YEAR),
                localCal.get(Calendar.MONTH),
                localCal.get(Calendar.DAY_OF_MONTH)
            )
        }
        utcCal.timeInMillis
    }

    val todayUtcMillis = remember {
        val nowLocal = Calendar.getInstance()
        val todayUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                nowLocal.get(Calendar.YEAR),
                nowLocal.get(Calendar.MONTH),
                nowLocal.get(Calendar.DAY_OF_MONTH)
            )
        }
        todayUtc.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = remember(todayUtcMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Vergleiche UTC-Zeitstempel. Erlaube alles ab heute.
                    return utcTimeMillis >= todayUtcMillis
                }
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                ) {
                    Text(stringResource(R.string.cancel))
                }

                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            val initialCal = Calendar.getInstance().apply {
                                timeInMillis = initialMillis
                            }

                            val selectedCal = Calendar.getInstance().apply {
                                timeInMillis = selectedMillis

                                set(Calendar.HOUR_OF_DAY, initialCal.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, initialCal.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            onConfirm(selectedCal.timeInMillis)
                        } ?: onDismiss()
                    },
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }


}