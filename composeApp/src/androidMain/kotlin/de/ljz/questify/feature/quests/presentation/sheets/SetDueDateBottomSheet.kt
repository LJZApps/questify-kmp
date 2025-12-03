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
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDueDateBottomSheet(
    selectedCombinedDueDate: Long,
    selectedDate: Long,
    selectedTime: Long,
    onShowSubDialog: (CreateQuestSubDialogState) -> Unit,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val dateFormat = SimpleDateFormat("dd. MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

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
                    onClick = {},
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fälligkeit entfernen")
                }

                Button(
                    onClick = {
                        // TODO onConfirm()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Speichern")
                }
            }

        }
    }
}