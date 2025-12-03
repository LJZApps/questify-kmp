package de.ljz.questify.feature.quests.presentation.screens.experimental.create_quest

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
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
import de.ljz.questify.feature.quests.presentation.dialogs.SetDueDateDialog
import de.ljz.questify.feature.quests.presentation.dialogs.SetDueTimeDialog
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestDialogState
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestUiEffect
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestUiEvent
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestUiState
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestViewModel
import de.ljz.questify.feature.quests.presentation.sheets.SelectCategoryBottomSheet
import de.ljz.questify.feature.quests.presentation.sheets.SelectDifficultyBottomSheet
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExperimentalCreateQuestScreen(
    selectedCategoryIndex: Int? = null,
    viewModel: CreateQuestViewModel = koinViewModel(
        parameters = { parametersOf(selectedCategoryIndex) }
    ),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is CreateQuestUiEffect.OnNavigateUp -> onNavigateBack()
            }
        }
    }

    ExperimentalCreateQuestScreen(
        uiState = uiState,
        selectedCategory = selectedCategory,
        categories = categories,
        onUiEvent = { event ->
            when (event) {
                is CreateQuestUiEvent.OnNavigateUp -> {
                    onNavigateBack()
                }

                else -> viewModel.onUiEvent(event = event)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExperimentalCreateQuestScreen(
    uiState: CreateQuestUiState,
    selectedCategory: QuestCategoryEntity?,
    categories: List<QuestCategoryEntity>,
    onUiEvent: (CreateQuestUiEvent) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val interactionSource =
        remember { MutableInteractionSource() }

    val dateFormat = SimpleDateFormat("dd. MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateTimeFormat = SimpleDateFormat("dd. MMM yyy HH:mm", Locale.getDefault())
    val difficultyOptions = listOf(
        stringResource(R.string.difficulty_easy),
        stringResource(R.string.difficulty_medium),
        stringResource(R.string.difficulty_hard),
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Crossfade(
                        selectedCategory
                    ) {
                        it?.let {
                            InfoChip(
                                label = {
                                    Text(
                                        text = it.text
                                    )
                                }
                            )
                        }
                    }
                },
                navigationIcon = {
                    BasicPlainTooltip(
                        text = "Zurück",
                    ) {
                        IconButton(
                            onClick = {
                                onUiEvent(CreateQuestUiEvent.OnNavigateUp)
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.back)
                            )
                        }
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
                                onUiEvent(CreateQuestUiEvent.OnCreateSubQuest)
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
                                onUiEvent(CreateQuestUiEvent.OnShowDialog(CreateQuestDialogState.SelectCategorySheet))
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

                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_calendar_today_outlined),
                                    contentDescription = null
                                )
                            },
                            label = "Fälligkeit"
                        )
                    }

                    TextButton(
                        onClick = {
                            onUiEvent(CreateQuestUiEvent.OnCreateQuest)
                        }
                    ) {
                        Text("Speichern")
                    }
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BasicTextField(
                    value = uiState.title,
                    onValueChange = { value ->
                        onUiEvent(CreateQuestUiEvent.OnTitleUpdated(value))
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
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
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            contentPadding = PaddingValues(0.dp),
                            interactionSource = interactionSource
                        )
                    }
                )

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
                                    0 -> "Leicht"
                                    1 -> "Mittel"
                                    2 -> "Schwer"
                                    else -> ""
                                }
                            )
                        },
                        leadingIcon = {
                            when (uiState.difficulty) {
                                0 -> EasyIcon(
                                    tint = LocalContentColor.current
                                )
                                1 -> MediumIcon(
                                    tint = LocalContentColor.current
                                )
                                2 -> HardIcon(
                                    tint = LocalContentColor.current
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                            onUiEvent(CreateQuestUiEvent.OnShowDialog(CreateQuestDialogState.SelectDifficultySheet))
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    if (uiState.selectedDueDate != 0L) {
                        InfoChip(
                            label = {
                                Text("3. Dez 2025 um 15:00 Uhr")
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_event_filled),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }

                BasicTextField(
                    value = uiState.description,
                    onValueChange = { value ->
                        onUiEvent(CreateQuestUiEvent.OnDescriptionUpdated(value))
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    minLines = 2,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                    decorationBox = @Composable { innerTextField ->
                        TextFieldDefaults.DecorationBox(
                            value = uiState.description,
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
                                    text = "Beschreibung",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            contentPadding = PaddingValues(0.dp),
                            interactionSource = interactionSource
                        )
                    }
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uiState.subQuests.forEachIndexed { i, subQuest ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 16.dp),
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
                                    Checkbox(checked = false, onCheckedChange = null)
                                }

                                BasicTextField(
                                    value = subQuest.text,
                                    onValueChange = {
                                        onUiEvent(
                                            CreateQuestUiEvent.OnUpdateSubQuest(
                                                index = i,
                                                value = it
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f),
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                                    singleLine = true,
                                    maxLines = 1,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        capitalization = KeyboardCapitalization.Sentences
                                    ),
                                    keyboardActions = KeyboardActions {
                                        onUiEvent(CreateQuestUiEvent.OnCreateSubQuest)
                                    },
                                    decorationBox = @Composable { innerTextField ->
                                        TextFieldDefaults.DecorationBox(
                                            value = subQuest.text,
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
                                                    text = "Neue Unteraufgabe"
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
                                        onUiEvent(CreateQuestUiEvent.OnRemoveSubQuest(index = i))
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

            // Dialogs
            if (uiState.dialogState is CreateQuestDialogState.AddReminder) {
                CreateReminderDialog(
                    onDismiss = {
                        onUiEvent(CreateQuestUiEvent.OnCloseDialog)
                    },
                    onConfirm = { timestamp ->
                        onUiEvent(CreateQuestUiEvent.OnCreateReminder(timestamp = timestamp))
                        onUiEvent(CreateQuestUiEvent.OnCloseDialog)
                    },
                    addingDateTimeState = uiState.addingDateTimeState,
                    onReminderStateChange = {
                        onUiEvent(CreateQuestUiEvent.OnUpdateReminderState(it))
                    }
                )
            }

            val initialDateTimeMillis = uiState.selectedDueDate.takeIf { it != 0L }

            if (uiState.dialogState is CreateQuestDialogState.DatePicker) {
                SetDueDateDialog(
                    onConfirm = { timestamp ->
                        onUiEvent(CreateQuestUiEvent.OnSetDueDate(timestamp = timestamp))
                        focusManager.clearFocus()
                    },
                    onDismiss = {
                        onUiEvent(CreateQuestUiEvent.OnCloseDialog)
                        focusManager.clearFocus()
                    },
                    onRemoveDueDate = {
                        onUiEvent(CreateQuestUiEvent.OnRemoveDueDate)
                        focusManager.clearFocus()
                    },
                    initialSelectedDateTimeMillis = initialDateTimeMillis
                )
            }

            if (uiState.dialogState is CreateQuestDialogState.TimePicker) {
                SetDueTimeDialog(
                    onConfirm = { timestamp ->
                        onUiEvent(CreateQuestUiEvent.OnSetDueDate(timestamp = timestamp))
                        focusManager.clearFocus()
                    },
                    onDismiss = {
                        onUiEvent(CreateQuestUiEvent.OnCloseDialog)
                        focusManager.clearFocus()
                    },
                    onRemoveDueDate = {
                        onUiEvent(CreateQuestUiEvent.OnRemoveDueDate)
                        focusManager.clearFocus()
                    },
                    initialSelectedDateTimeMillis = initialDateTimeMillis
                )
            }

            if (uiState.dialogState is CreateQuestDialogState.SelectCategorySheet) {
                SelectCategoryBottomSheet(
                    categories = categories,
                    onCategorySelect = { category ->
                        onUiEvent(CreateQuestUiEvent.OnSelectQuestCategory(category))
                        onUiEvent(CreateQuestUiEvent.OnCloseDialog)
                        focusManager.clearFocus()
                    },
                    onDismiss = {
                        onUiEvent(CreateQuestUiEvent.OnCloseDialog)
                        focusManager.clearFocus()
                    },
                    onCreateCategory = { text ->
                        onUiEvent(CreateQuestUiEvent.OnCreateQuestCategory(value = text))
                    }
                )
            }

            if (uiState.dialogState is CreateQuestDialogState.SelectDifficultySheet) {
                SelectDifficultyBottomSheet(
                    difficulty = uiState.difficulty,
                    onDifficultySelected = { value ->
                        onUiEvent(CreateQuestUiEvent.OnDifficultyUpdated(value))
                    },
                    onDismiss = {
                        onUiEvent(CreateQuestUiEvent.OnCloseDialog)
                    }
                )
            }
        }
    )
}
