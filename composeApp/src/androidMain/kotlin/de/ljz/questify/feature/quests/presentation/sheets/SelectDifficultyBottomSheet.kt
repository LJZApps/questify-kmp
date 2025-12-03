package de.ljz.questify.feature.quests.presentation.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.ljz.questify.R
import de.ljz.questify.feature.quests.presentation.components.EasyIcon
import de.ljz.questify.feature.quests.presentation.components.HardIcon
import de.ljz.questify.feature.quests.presentation.components.MediumIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectDifficultyBottomSheet(
    difficulty: Int,
    onDifficultySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val difficultyOptions = listOf(
        stringResource(R.string.difficulty_easy),
        stringResource(R.string.difficulty_medium),
        stringResource(R.string.difficulty_hard),
    )

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
            scope.launch {
                state.hide()
            }
        },
        sheetState = state,
        dragHandle = null
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier.padding(16.dp)
        ) {
            val modifiers = List(difficultyOptions.size) { Modifier.weight(1f) }

            difficultyOptions.forEachIndexed { index, label ->
                ToggleButton(
                    checked = difficulty == index,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)

                        onDifficultySelected(index)
                    },
                    modifier = modifiers[index].semantics {
                        role = Role.RadioButton
                    },
                    shapes = when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        difficultyOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tint = if (difficulty == index)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant

                        when (index) {
                            0 -> EasyIcon(tint = tint)
                            1 -> MediumIcon(tint = tint)
                            2 -> HardIcon(tint = tint)
                        }

                        Text(label)
                    }
                }
            }
        }
    }
}