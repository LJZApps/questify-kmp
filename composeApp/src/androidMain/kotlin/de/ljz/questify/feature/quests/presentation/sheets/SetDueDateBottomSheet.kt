package de.ljz.questify.feature.quests.presentation.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import de.ljz.questify.R
import de.ljz.questify.core.presentation.components.expressive.menu.ExpressiveMenuItem
import de.ljz.questify.core.presentation.components.expressive.settings.ExpressiveSettingsMenuLink
import de.ljz.questify.core.presentation.components.expressive.settings.ExpressiveSettingsSection
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDueDateBottomSheet(
    selectedCombinedDueDate: Long,
    selectedDate: Long,
    selectedTime: Long,
    onShowTimePickerDialog: () -> Unit,
    onShowDatePickerDialog: () -> Unit,
    onUpdateTempDueDate: (date: Long, time: Long) -> Unit,
    onConfirm: (Long) -> Unit,
    onRemoveDueDate: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val dateFormat = SimpleDateFormat("dd. MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    LaunchedEffect(Unit) {
        if (selectedCombinedDueDate != 0L) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedCombinedDueDate

            val timeInMillis = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dateInMillis = calendar.timeInMillis

            onUpdateTempDueDate(dateInMillis, timeInMillis)
        } else {
            onUpdateTempDueDate(0L, 0L)
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.set_due_date_bottom_sheet_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            val dateAnnotatedString = buildAnnotatedString {
                append(stringResource(R.string.set_due_date_bottom_sheet_date_button))

                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                    append("*")
                }
            }

            ExpressiveSettingsSection {
                ExpressiveMenuItem(
                    title = {
                        if (selectedDate != 0L)
                            Text(dateFormat.format(selectedDate))
                        else
                            Text(dateAnnotatedString)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar_today_outlined),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onShowDatePickerDialog()
                    }
                )

                ExpressiveSettingsMenuLink(
                    title = if (selectedTime != 0L) timeFormat.format(selectedTime) else stringResource(
                        R.string.set_due_date_bottom_sheet_time_title
                    ),
                    subtitle = if (selectedTime == 0L) stringResource(R.string.set_due_date_bottom_sheet_time_subtitle) else null,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_schedule_outlined),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onShowTimePickerDialog()
                    }
                )
            }

            if (selectedTime == 0L) {
                Text(
                    text = stringResource(R.string.set_due_date_bottom_sheet_time_hint),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        onRemoveDueDate()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f),
                    enabled = selectedCombinedDueDate != 0L
                ) {
                    Text(stringResource(R.string.set_due_date_bottom_sheet_remove_due_button))
                }

                Button(
                    onClick = {
                        if (selectedDate != 0L) {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = selectedDate

                            if (selectedTime != 0L) {
                                val timeCal = Calendar.getInstance()
                                timeCal.timeInMillis = selectedTime

                                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                            } else {
                                calendar.set(Calendar.HOUR_OF_DAY, 23)
                                calendar.set(Calendar.MINUTE, 59)
                            }

                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            scope.launch {
                                sheetState.hide()
                            }

                            onConfirm(calendar.timeInMillis)
                        } else {
                            scope.launch {
                                sheetState.hide()
                            }

                            onConfirm(0L)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedDate != 0L
                ) {
                    Text(
                        text = stringResource(R.string.save)
                    )
                }
            }

        }
    }
}