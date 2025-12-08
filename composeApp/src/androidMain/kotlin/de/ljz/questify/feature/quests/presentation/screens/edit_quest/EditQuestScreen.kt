package de.ljz.questify.feature.quests.presentation.screens.edit_quest

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ljz.questify.R
import de.ljz.questify.core.presentation.components.chips.InfoChip
import de.ljz.questify.core.presentation.components.tooltips.BasicPlainTooltip
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.presentation.components.EasyIcon
import de.ljz.questify.feature.quests.presentation.components.HardIcon
import de.ljz.questify.feature.quests.presentation.components.MediumIcon
import de.ljz.questify.feature.quests.presentation.dialogs.CreateReminderDialog
import de.ljz.questify.feature.quests.presentation.dialogs.DeleteConfirmationDialog
import de.ljz.questify.feature.quests.presentation.dialogs.SetDueDateDialog
import de.ljz.questify.feature.quests.presentation.dialogs.SetDueTimeDialog
import de.ljz.questify.feature.quests.presentation.sheets.SelectCategoryBottomSheet
import de.ljz.questify.feature.quests.presentation.sheets.SelectDifficultyBottomSheet
import de.ljz.questify.feature.quests.presentation.sheets.SetDueDateBottomSheet
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EditQuestScreen(
    id: Int,
    viewModel: EditQuestViewModel = koinViewModel(
        parameters = { parametersOf(id) }
    ),
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is EditQuestUiEffect.OnNavigateUp -> onNavigateUp()
            }
        }
    }

    EditQuestScreen(
        uiState = uiState,
        selectedCategory = selectedCategory,
        categories = categories,
        onUiEvent = { event ->
            when (event) {
                is EditQuestUiEvent.OnNavigateUp -> onNavigateUp()
                else -> viewModel.onUiEvent(event)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EditQuestScreen(
    uiState: EditQuestUiState,
    selectedCategory: QuestCategoryEntity?,
    categories: List<QuestCategoryEntity>,
    onUiEvent: (EditQuestUiEvent) -> Unit
) {
    val dialogState = uiState.dialogState

    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
        onUiEvent(EditQuestUiEvent.OnMoveSubQuest(from.index, to.index))

        haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    val dateTimeFormat = SimpleDateFormat("dd. MMM yyy HH:mm", Locale.getDefault())
    var showDeleteMenu by remember { mutableStateOf(false) }

    var indexToFocus by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(indexToFocus) {
        indexToFocus?.let { index ->
            scope.launch {
                listState.animateScrollToItem(index + 3)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Crossfade(selectedCategory) { category ->
                        category?.let {
                            InfoChip(
                                label = { Text(text = it.text) }
                            )
                        }
                    }
                },
                navigationIcon = {
                    BasicPlainTooltip(text = "Zurück") {
                        IconButton(onClick = { onUiEvent(EditQuestUiEvent.OnNavigateUp) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                },
                actions = {
                    BasicPlainTooltip(
                        text = "Mehr",
                        position = TooltipAnchorPosition.Below
                    ) {
                        IconButton(onClick = { showDeleteMenu = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more_vert),
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showDeleteMenu,
                        onDismissRequest = { showDeleteMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Quest löschen") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete_filled),
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                showDeleteMenu = false
                                onUiEvent(EditQuestUiEvent.OnShowDialog(EditQuestDialogState.DeletionConfirmation))
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error,
                                leadingIconColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            )
        },
        bottomBar = {
            FlexibleBottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppBarRow {
                        clickableItem(
                            onClick = {
                                onUiEvent(EditQuestUiEvent.OnCreateSubQuest)
                                indexToFocus = uiState.subQuests.size
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add_box_outlined),
                                    contentDescription = null
                                )
                            },
                            label = "Unteraufgabe hinzufügen"
                        )

                        clickableItem(
                            onClick = {
                                onUiEvent(EditQuestUiEvent.OnShowDialog(EditQuestDialogState.SelectCategorySheet))
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_label_outlined),
                                    contentDescription = null
                                )
                            },
                            label = "Liste"
                        )

                        clickableItem(
                            onClick = {
                                onUiEvent(EditQuestUiEvent.OnShowDialog(EditQuestDialogState.SetDueDateSheet(uiState.combinedDueDate)))
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_schedule_outlined),
                                    contentDescription = null
                                )
                            },
                            label = "Fälligkeit"
                        )
                    }

                    TextButton(
                        onClick = {
                            onUiEvent(EditQuestUiEvent.OnSaveQuest)
                        }
                    ) {
                        Text("Speichern")
                    }
                }
            }
        },
        content = { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title Input
                item {
                    BasicTextField(
                        value = uiState.title,
                        onValueChange = { value ->
                            onUiEvent(EditQuestUiEvent.OnTitleUpdated(value))
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        decorationBox = @Composable { innerTextField ->
                            TextFieldDefaults.DecorationBox(
                                value = uiState.title,
                                enabled = true,
                                innerTextField = innerTextField,
                                singleLine = false,
                                visualTransformation = VisualTransformation.None,
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                ),
                                placeholder = {
                                    Text(
                                        text = "Titel",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                contentPadding = PaddingValues(0.dp),
                                interactionSource = interactionSource
                            )
                        }
                    )
                }

                // Chips (Difficulty & Date)
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        InfoChip(
                            label = {
                                Text(
                                    text = when (uiState.difficulty) {
                                        0 -> stringResource(R.string.difficulty_easy)
                                        1 -> stringResource(R.string.difficulty_medium)
                                        2 -> stringResource(R.string.difficulty_hard)
                                        else -> ""
                                    }
                                )
                            },
                            leadingIcon = {
                                when (uiState.difficulty) {
                                    0 -> EasyIcon(tint = LocalContentColor.current)
                                    1 -> MediumIcon(tint = LocalContentColor.current)
                                    2 -> HardIcon(tint = LocalContentColor.current)
                                }
                            },
                            modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onUiEvent(EditQuestUiEvent.OnShowDialog(EditQuestDialogState.SelectDifficultySheet))
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        if (uiState.combinedDueDate != 0L) {
                            InfoChip(
                                label = {
                                    Text(dateTimeFormat.format(uiState.combinedDueDate))
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_event_filled),
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onUiEvent(EditQuestUiEvent.OnShowDialog(EditQuestDialogState.SetDueDateSheet(uiState.combinedDueDate)))
                                }
                            )
                        }
                    }
                }

                // Description Input
                item {
                    BasicTextField(
                        value = uiState.notes,
                        onValueChange = { value ->
                            onUiEvent(EditQuestUiEvent.OnDescriptionUpdated(value))
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        minLines = 2,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        decorationBox = @Composable { innerTextField ->
                            TextFieldDefaults.DecorationBox(
                                value = uiState.notes,
                                enabled = true,
                                innerTextField = innerTextField,
                                singleLine = false,
                                visualTransformation = VisualTransformation.None,
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                ),
                                placeholder = {
                                    Text(
                                        text = "Notizen",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                contentPadding = PaddingValues(0.dp),
                                interactionSource = interactionSource
                            )
                        }
                    )
                }

                itemsIndexed(
                    items = uiState.subQuests,
                    key = { i, subTask -> subTask.tempId }
                ) { index, subTask ->
                    val itemFocusRequester = remember { FocusRequester() }
                    val bringIntoViewRequester = remember { BringIntoViewRequester() }

                    LaunchedEffect(indexToFocus) {
                        if (indexToFocus == index) {
                            itemFocusRequester.requestFocus()
                            indexToFocus = null
                        }
                    }

                    ReorderableItem(
                        state = reorderableState,
                        key = subTask.tempId
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp)
                                .bringIntoViewRequester(bringIntoViewRequester = bringIntoViewRequester),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                CompositionLocalProvider(
                                    value = LocalMinimumInteractiveComponentSize provides 0.dp
                                ) {
                                    IconButton(
                                        onClick = {},
                                        modifier = Modifier.draggableHandle(
                                            onDragStarted = {
                                                haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                            },
                                            onDragStopped = {
                                                haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                            },
                                        )
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_drag_indicator),
                                            contentDescription = "Verschieben",
                                        )
                                    }
                                }

                                BasicTextField(
                                    value = subTask.text,
                                    onValueChange = {
                                        onUiEvent(
                                            EditQuestUiEvent.OnUpdateSubQuest(
                                                index = index,
                                                value = it
                                            )
                                        )

                                        scope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(itemFocusRequester),
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                                    singleLine = true,
                                    maxLines = 1,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        capitalization = KeyboardCapitalization.Sentences
                                    ),
                                    keyboardActions = KeyboardActions {
                                        onUiEvent(EditQuestUiEvent.OnCreateSubQuest)

                                        indexToFocus = uiState.subQuests.size
                                    },
                                    decorationBox = @Composable { innerTextField ->
                                        TextFieldDefaults.DecorationBox(
                                            value = subTask.text,
                                            enabled = true,
                                            innerTextField = innerTextField,
                                            singleLine = true,
                                            visualTransformation = VisualTransformation.None,
                                            colors = TextFieldDefaults.colors(
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                disabledIndicatorColor = Color.Transparent,
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                disabledContainerColor = Color.Transparent,
                                            ),
                                            interactionSource = interactionSource,
                                            placeholder = {
                                                Text(
                                                    text = "Unteraufgabe",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        )
                                    }
                                )
                            }

                            CompositionLocalProvider(
                                value = LocalMinimumInteractiveComponentSize provides 0.dp
                            ) {
                                IconButton(
                                    onClick = {
                                        onUiEvent(EditQuestUiEvent.OnRemoveSubQuest(index = index))
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_close),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dialogs & Sheets

            if (uiState.dialogState is EditQuestDialogState.AddReminder) {
                CreateReminderDialog(
                    onDismiss = {
                        onUiEvent(EditQuestUiEvent.OnCloseDialog)
                    },
                    onConfirm = { timestamp ->
                        onUiEvent(EditQuestUiEvent.OnCreateReminder(timestamp = timestamp))
                        onUiEvent(EditQuestUiEvent.OnCloseDialog)
                    },
                    addingDateTimeState = uiState.addingDateTimeState,
                    onReminderStateChange = {
                        onUiEvent(EditQuestUiEvent.OnUpdateReminderState(it))
                    }
                )
            }

            if (uiState.dialogState is EditQuestDialogState.SelectCategorySheet) {
                SelectCategoryBottomSheet(
                    categories = categories,
                    onCategorySelect = { category ->
                        onUiEvent(EditQuestUiEvent.OnSelectQuestCategory(category))
                        onUiEvent(EditQuestUiEvent.OnCloseDialog)
                    },
                    onDismiss = {
                        onUiEvent(EditQuestUiEvent.OnCloseDialog)
                    },
                    onCreateCategory = { text ->
                        onUiEvent(EditQuestUiEvent.OnCreateQuestCategory(value = text))
                    }
                )
            }

            if (uiState.dialogState is EditQuestDialogState.DeletionConfirmation) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        onUiEvent(EditQuestUiEvent.OnDeleteQuest)
                    },
                    onDismiss = {
                        onUiEvent(EditQuestUiEvent.OnCloseDialog)
                    }
                )
            }

            // Local Sheets
            if (uiState.dialogState is EditQuestDialogState.SelectDifficultySheet) {
                SelectDifficultyBottomSheet(
                    difficulty = uiState.difficulty,
                    onDifficultySelected = { value ->
                        onUiEvent(EditQuestUiEvent.OnDifficultyUpdated(value))
                    },
                    onDismiss = {
                        onUiEvent(EditQuestUiEvent.OnCloseDialog)
                    }
                )
            }

            if (dialogState is EditQuestDialogState.SetDueDateSheet) {
                SetDueDateBottomSheet(
                    selectedCombinedDueDate = uiState.combinedDueDate,
                    selectedDate = uiState.combinedDueDate,
                    selectedTime = uiState.combinedDueDate,
                    onShowSubDialog = {
//                        onUiEvent(EditQuestUiEvent.OnShowSubDialog(it))
                    },
                    onUpdateTempDueDate = { _, _ -> /* Optional */ },
                    onConfirm = { timestamp ->
                        onUiEvent(EditQuestUiEvent.OnSetDueDate(timestamp))
                    },
                    onRemoveDueDate = {
                        onUiEvent(EditQuestUiEvent.OnRemoveDueDate)
                    },
                    onDismiss = {
                        onUiEvent(EditQuestUiEvent.OnCloseDialog)
                    }
                )
            }

            val initialDateMillis = uiState.selectedDueDate.takeIf { it != 0L }
            val initialTimeMillis = uiState.selectedDueTime.takeIf { it != 0L }

            if (uiState.subDialogState is EditQuestSubDialogState.DatePicker) {
                SetDueDateDialog(
                    onConfirm = { timestamp ->
                        onUiEvent(EditQuestUiEvent.OnUpdateDueDate(value = timestamp))
                    },
                    onDismiss = {
                        onUiEvent(EditQuestUiEvent.OnCloseSubDialog)
                    },
                    initialSelectedDateTimeMillis = initialDateMillis
                )
            }

            if (uiState.subDialogState is EditQuestSubDialogState.TimePicker) {
                SetDueTimeDialog(
                    onConfirm = { timestamp ->
                        onUiEvent(EditQuestUiEvent.OnUpdateDueTime(value = timestamp))
                    },
                    onDismiss = {
                        onUiEvent(EditQuestUiEvent.OnCloseSubDialog)
                    },
                    initialSelectedDateTimeMillis = initialTimeMillis
                )
            }
        }
    )
}