package de.ljz.questify.feature.quests.presentation.screens.quest_detail

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ljz.questify.R
import de.ljz.questify.core.notifications.AndroidNotificationScheduler
import de.ljz.questify.core.utils.MaxWidth
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.presentation.components.EasyIcon
import de.ljz.questify.feature.quests.presentation.components.HardIcon
import de.ljz.questify.feature.quests.presentation.components.MediumIcon
import de.ljz.questify.feature.quests.presentation.dialogs.DeleteConfirmationDialog
import de.ljz.questify.feature.quests.presentation.dialogs.QuestDoneDialog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuestDetailScreen(
    questId: Int,
    viewModel: QuestDetailViewModel = koinViewModel(
        parameters = { parametersOf(questId) }
    ),
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is QuestDetailUiEffect.OnNavigateUp -> onNavigateUp()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigateUp() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.showDeleteConfirmationDialog()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete_outlined),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            val subQuests = uiState.questState.subQuests
            val hasSubQuests = subQuests.isNotEmpty()

            val allSubQuestsDone = !hasSubQuests || subQuests.all { it.isDone }

            val isQuestAlreadyDone = uiState.questState.done

            val isButtonEnabled = !isQuestAlreadyDone && allSubQuestsDone

            if (!isQuestAlreadyDone) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                    ) {
                        HorizontalDivider()

                        if (hasSubQuests && !allSubQuestsDone) {
                            Text(
                                text = stringResource(R.string.complete_subtasks_first_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = {
                                val scheduler = AndroidNotificationScheduler(context)
                                scheduler.testLiveNotification()
                            }
                        ) {
                            Text("Test Live Update")
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)

                                uiState.questEntity?.let { questEntity ->
                                    viewModel.completeQuest(questEntity)
                                }
                            },
                            enabled = isButtonEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = stringResource(R.string.quest_detail_complete_quest_button))
                        }

                    }
                }
            }

        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .widthIn(max = MaxWidth),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = uiState.questState.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                                Badge(
                                    containerColor = when (uiState.questState.difficulty) {
                                        Difficulty.EASY -> MaterialTheme.colorScheme.surfaceContainerLow
                                        Difficulty.MEDIUM -> MaterialTheme.colorScheme.surfaceContainerHigh
                                        Difficulty.HARD -> MaterialTheme.colorScheme.primary
                                    },
                                    contentColor = when (uiState.questState.difficulty) {
                                        Difficulty.EASY -> MaterialTheme.colorScheme.onSurfaceVariant
                                        Difficulty.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                        Difficulty.HARD -> MaterialTheme.colorScheme.onPrimary
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        when (uiState.questState.difficulty) {
                                            Difficulty.EASY -> {
                                                EasyIcon(
                                                    tint = when (uiState.questState.difficulty) {
                                                        Difficulty.EASY -> MaterialTheme.colorScheme.onSurfaceVariant
                                                        Difficulty.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                                        Difficulty.HARD -> MaterialTheme.colorScheme.onPrimary
                                                    },
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Difficulty.MEDIUM -> {
                                                MediumIcon(
                                                    tint = when (uiState.questState.difficulty) {
                                                        Difficulty.EASY -> MaterialTheme.colorScheme.onSurfaceVariant
                                                        Difficulty.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                                        Difficulty.HARD -> MaterialTheme.colorScheme.onPrimary
                                                    },
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Difficulty.HARD -> {
                                                HardIcon(
                                                    tint = when (uiState.questState.difficulty) {
                                                        Difficulty.EASY -> MaterialTheme.colorScheme.onSurfaceVariant
                                                        Difficulty.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                                        Difficulty.HARD -> MaterialTheme.colorScheme.onPrimary
                                                    },
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = when (uiState.questState.difficulty) {
                                                Difficulty.EASY -> stringResource(R.string.difficulty_easy)
                                                Difficulty.MEDIUM -> stringResource(R.string.difficulty_medium)
                                                Difficulty.HARD -> stringResource(R.string.difficulty_hard)
                                            }
                                        )
                                    }
                                }
                            }


                            uiState.questState.notes
                                .takeIf { it.isNotEmpty() }
                                ?.let { description ->
                                    Text(
                                        text = description
                                    )
                                }
                        }
                    }

                    uiState.questState.subQuests
                        .takeIf { it.isNotEmpty() }
                        ?.let { subQuestEntities ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val doneCount = subQuestEntities.count { it.isDone }
                                    val totalCount = subQuestEntities.size
                                    val progress = doneCount.toFloat() / totalCount.toFloat()
                                    val percentage = (progress * 100).toInt()

                                    val animatedProgress by animateFloatAsState(
                                        targetValue = progress,
                                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                                        label = "QuestProgressAnimation"
                                    )

                                    val animatedPercentage by animateIntAsState(
                                        targetValue = percentage,
                                        animationSpec = tween(
                                            durationMillis = 500,
                                            easing = FastOutLinearInEasing
                                        ),
                                        label = "QuestProgressAnimation"
                                    )

                                    Text(
                                        text = stringResource(R.string.quest_progress_title),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quest_progress_current_label),
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = "${animatedPercentage}%",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(12.dp)
                                    )

                                    Text(
                                        text = stringResource(R.string.quest_progress_subtasks_count, doneCount, totalCount),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                    uiState.questState.subQuests
                        .takeIf { it.isNotEmpty() }
                        ?.let { subQuestEntities ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.quest_detail_subtasks_title),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Column {
                                        subQuestEntities
                                            .sortedBy { it.orderIndex }
                                            .forEach { subQuestEntity ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        haptic.performHapticFeedback(
                                                            HapticFeedbackType.KeyboardTap
                                                        )

                                                        viewModel.checkSubQuest(
                                                            id = subQuestEntity.id,
                                                            checked = !subQuestEntity.isDone
                                                        )
                                                    }
                                                    .padding(all = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CompositionLocalProvider(
                                                    LocalMinimumInteractiveComponentSize provides 0.dp
                                                ) {
                                                    Checkbox(
                                                        checked = subQuestEntity.isDone,
                                                        onCheckedChange = {
                                                            haptic.performHapticFeedback(
                                                                HapticFeedbackType.KeyboardTap
                                                            )

                                                            viewModel.checkSubQuest(
                                                                id = subQuestEntity.id,
                                                                checked = !subQuestEntity.isDone
                                                            )
                                                        }
                                                    )
                                                }

                                                Text(
                                                    text = subQuestEntity.text,
                                                    modifier = Modifier.weight(1f),
                                                    textDecoration = if (subQuestEntity.isDone) TextDecoration.LineThrough else null
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    uiState.questState
                        .takeIf {
                            it.selectedDueDate != 0L || it.notificationTriggerTimes.isNotEmpty()
                        }
                        ?.let { questState ->
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.quest_detail_screen_due_date_title),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    uiState.questState.selectedDueDate
                                        .takeIf { it != 0L }
                                        ?.let { dueDate ->
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_calendar_month_outlined),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )

                                                Text(
                                                    text = SimpleDateFormat(
                                                        stringResource(R.string.quest_due_date_format_full),
                                                        Locale.getDefault()
                                                    ).format(dueDate),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                }
                            }
                        }

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.quest_detail_rewards_title),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    RewardItem(
                                        icon = painterResource(R.drawable.star),
                                        text = stringResource(
                                            R.string.quest_detail_screen_xp,
                                            uiState.questState.difficulty.xpValue
                                        )
                                    )

                                    RewardItem(
                                        icon = painterResource(R.drawable.coins),
                                        text = stringResource(
                                            R.string.quest_detail_screen_points,
                                            uiState.questState.difficulty.pointsValue
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dialogs
            if (uiState.dialogState == DialogState.DeleteConfirmation) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        viewModel.deleteQuest(uiState.questId)
                    },
                    onDismiss = {
                        viewModel.hideDeleteConfirmationDialog()
                    }
                )
            }

            if (uiState.dialogState ==  DialogState.QuestDone) {
                QuestDoneDialog(
                    state = uiState.questDoneDialogState,
                    onDismiss = {
                        viewModel.hideDeleteConfirmationDialog()
//                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding()
            )
        }
    )
}

@Composable
private fun RewardItem(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}