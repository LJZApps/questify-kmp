package de.ljz.questify.feature.quests.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ljz.questify.R
import de.ljz.questify.core.presentation.components.tooltips.BasicPlainTooltip
import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun QuestItem(
    questWithDetails: QuestWithDetails,
    onEditButtonClick: () -> Unit,
    onCheckButtonClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showListBadge: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val completionDate: Instant = Clock.System.now()
    val isOverdue = questWithDetails.quest.dueDate?.let { it < completionDate } ?: false
    val doneCount = questWithDetails.subTasks.count { it.isDone }
    val totalCount = questWithDetails.subTasks.size
    val progress = doneCount.toFloat() / totalCount.toFloat()

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = if (totalCount > 0) 8.dp else 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = questWithDetails.quest.title,
                    style = MaterialTheme.typography.titleMedium
                        .copy(
                            fontWeight = FontWeight.Bold
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                questWithDetails.quest.notes?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                questWithDetails.quest.dueDate?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar_month_outlined),
                            contentDescription = null
                        )

                        Text(
                            text = SimpleDateFormat(
                                "dd. MMM 'um' HH:mm",
                                Locale.getDefault()
                            ).format(Date(it.toEpochMilliseconds()))
                        )
                    }
                }

                questWithDetails.subTasks.let { subTasks ->
                    if (subTasks.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_checklist),
                                contentDescription = null
                            )

                            Text(
                                text = pluralStringResource(
                                    R.plurals.quest_item_subtasks,
                                    subTasks.count(),
                                    subTasks.count()
                                )
                            )
                        }
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(
                        containerColor = when (questWithDetails.quest.difficulty) {
                            Difficulty.EASY -> MaterialTheme.colorScheme.surfaceContainerLow
                            Difficulty.MEDIUM -> MaterialTheme.colorScheme.surfaceContainerHigh
                            Difficulty.HARD -> MaterialTheme.colorScheme.primary
                        },
                        contentColor = when (questWithDetails.quest.difficulty) {
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
                            when (questWithDetails.quest.difficulty) {
                                Difficulty.EASY -> {
                                    EasyIcon(
                                        tint = when (questWithDetails.quest.difficulty) {
                                            Difficulty.EASY -> MaterialTheme.colorScheme.onSurfaceVariant
                                            Difficulty.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                            Difficulty.HARD -> MaterialTheme.colorScheme.onPrimary
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Difficulty.MEDIUM -> {
                                    MediumIcon(
                                        tint = when (questWithDetails.quest.difficulty) {
                                            Difficulty.EASY -> MaterialTheme.colorScheme.onSurfaceVariant
                                            Difficulty.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                            Difficulty.HARD -> MaterialTheme.colorScheme.onPrimary
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Difficulty.HARD -> {
                                    HardIcon(
                                        tint = when (questWithDetails.quest.difficulty) {
                                            Difficulty.EASY -> MaterialTheme.colorScheme.onSurfaceVariant
                                            Difficulty.MEDIUM -> MaterialTheme.colorScheme.onSurfaceVariant
                                            Difficulty.HARD -> MaterialTheme.colorScheme.onPrimary
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = when (questWithDetails.quest.difficulty) {
                                    Difficulty.EASY -> stringResource(R.string.difficulty_easy)
                                    Difficulty.MEDIUM -> stringResource(R.string.difficulty_medium)
                                    Difficulty.HARD -> stringResource(R.string.difficulty_hard)
                                }
                            )
                        }
                    }

                    if (isOverdue && !questWithDetails.quest.done)
                        Badge(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_event_busy_outlined),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(text = stringResource(R.string.quest_item_overdue_badge))
                            }
                        }

                    if (showListBadge)
                        questWithDetails.questCategory?.let { questCategoryEntity ->
                            Badge(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_label_outlined),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Text(text = questCategoryEntity.text)
                                }
                            }
                        }
                }
            }

            when (questWithDetails.quest.done) {
                false -> {
                    BasicPlainTooltip(
                        text = stringResource(R.string.quest_item_tooltip_edit),
                        position = TooltipAnchorPosition.Above
                    ) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)

                                onEditButtonClick()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit_outlined),
                                contentDescription = null
                            )
                        }
                    }

                    BasicPlainTooltip(
                        text = stringResource(R.string.quest_item_tooltip_done),
                        position = TooltipAnchorPosition.Above
                    ) {
                        FilledIconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                onCheckButtonClick()
                            },
                            enabled = doneCount == totalCount
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_circle_outlined),
                                contentDescription = null,
                            )
                        }
                    }
                }

                true -> {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = stringResource(R.string.quest_item_done)
                            )
                        }
                    }
                }
            }
        }

        if (totalCount > 0 && !questWithDetails.quest.done)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                drawStopIndicator = {},
                gapSize = 0.dp,
                strokeCap = StrokeCap.Square
            )
    }
}