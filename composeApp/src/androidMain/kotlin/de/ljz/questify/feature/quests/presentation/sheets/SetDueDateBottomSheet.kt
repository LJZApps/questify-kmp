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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ljz.questify.R
import de.ljz.questify.core.presentation.components.expressive.menu.ExpressiveMenuItem
import de.ljz.questify.core.presentation.components.expressive.settings.ExpressiveSettingsMenuLink
import de.ljz.questify.core.presentation.components.expressive.settings.ExpressiveSettingsSection
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestSubDialogState
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
    onShowSubDialog: (CreateQuestSubDialogState) -> Unit,
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
                text = "Fälligkeit setzen",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            ExpressiveSettingsSection {
                ExpressiveMenuItem(
                    title = if (selectedDate != 0L) dateFormat.format(selectedDate) else "Datum auswählen",
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar_today_outlined),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onShowSubDialog(CreateQuestSubDialogState.DatePicker)
                    }
                )

                ExpressiveSettingsMenuLink(
                    title = if (selectedTime != 0L) timeFormat.format(selectedTime) else "Zeit auswählen",
                    subtitle = if (selectedTime == 0L) "Automatisch: 23:59 Uhr" else null,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_schedule_outlined),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onShowSubDialog(CreateQuestSubDialogState.TimePicker)
                    }
                )
            }

            if (selectedTime == 0L) {
                Text(
                    text = "Wenn keine Zeit ausgewählt wurde, ist diese Quest am ausgewählten Datum um 23:59 Uhr fällig.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
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
                    Text("Fälligkeit entfernen")
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Speichern")
                }
            }

        }
    }
}