package de.ljz.questify.feature.quests.presentation.screens.quest_overview

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import de.ljz.questify.R
import de.ljz.questify.core.presentation.components.chips.ListChip
import de.ljz.questify.core.presentation.components.tooltips.BasicPlainTooltip
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.presentation.dialogs.CreateCategoryDialog
import de.ljz.questify.feature.quests.presentation.dialogs.DeleteQuestCategoryDialog
import de.ljz.questify.feature.quests.presentation.dialogs.QuestDoneDialog
import de.ljz.questify.feature.quests.presentation.dialogs.RenameCategoryDialog
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.sub_pages.all_quests_page.AllQuestsPage
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.sub_pages.quest_for_category_page.QuestsForCategoryPage
import de.ljz.questify.feature.quests.presentation.sheets.QuestSortingBottomSheet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun QuestOverviewScreen(
    onNavigateToQuestDetailScreen: (Int) -> Unit,
    onNavigateToCreateQuestScreen: (Int?) -> Unit,
    onNavigateToEditQuestScreen: (Int) -> Unit,
    onToggleDrawer: () -> Unit,
    viewModel: QuestOverviewViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    QuestOverviewScreen(
        uiState = uiState,
        categories = categories.toImmutableList(),
        effectFlow = effectFlow,
        onUiEvent = { event ->
            when (event) {
                is QuestOverviewUiEvent.ToggleDrawer -> {
                    onToggleDrawer()
                }

                is QuestOverviewUiEvent.OnNavigateToQuestDetailScreen -> {
                    onNavigateToQuestDetailScreen(event.entryId)
                }

                is QuestOverviewUiEvent.OnNavigateToCreateQuestScreen -> {
                    onNavigateToCreateQuestScreen(event.categoryId)
                }

                is QuestOverviewUiEvent.OnNavigateToEditQuestScreen -> {
                    onNavigateToEditQuestScreen(event.id)
                }

                else -> {
                    viewModel.onUiEvent(event = event)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun QuestOverviewScreen(
    uiState: QuestOverviewUIState,
    categories: ImmutableList<QuestCategoryEntity>,
    effectFlow: Flow<QuestOverviewUiEffect>,
    onUiEvent: (QuestOverviewUiEvent) -> Unit
) {
    val allQuestPageState = uiState.allQuestPageState
    val dialogState = uiState.dialogState

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val staticAllTab = QuestCategoryEntity(
        uuid = "static_all_tab",
        id = -1,
        text = stringResource(R.string.quest_overview_screen_tab_default_text)
    )

    val allTabs = remember(categories) {
        listOf(staticAllTab) + categories
    }

    var desiredPageIndex by rememberSaveable { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()
    val initialPage = desiredPageIndex.coerceIn(0, (allTabs.size - 1).coerceAtLeast(0))
    val refreshState = rememberPullToRefreshState()

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { allTabs.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        desiredPageIndex = pagerState.currentPage
    }

    val requesters = remember(allTabs.size) {
        List(allTabs.size) {
            BringIntoViewRequester()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        scope.launch {
            requesters[pagerState.currentPage].bringIntoView()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit, lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effectFlow.collect { effect ->
                when (effect) {
                    is QuestOverviewUiEffect.ShowDeleteSuccessfulSnackBar -> {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.delete_successful_snackbar, effect.text)
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    BasicPlainTooltip(
                        text = stringResource(R.string.quest_overview_nav_drawer_tooltip),
                        position = TooltipAnchorPosition.Below
                    ) {
                        IconButton(
                            onClick = {
                                onUiEvent(QuestOverviewUiEvent.ToggleDrawer)
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = stringResource(R.string.quest_overview_nav_drawer_tooltip)
                            )
                        }
                    }
                },
                actions = {
                    BasicPlainTooltip(
                        text = stringResource(R.string.quest_overview_more_options_tooltip),
                        position = TooltipAnchorPosition.Below
                    ) {
                        IconButton(
                            onClick = { dropdownExpanded = true }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more_vert),
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenuPopup(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuGroup(
                            shapes = MenuDefaults.groupShapes()
                        ) {
                            DropdownMenuItem(
                                text = {
                                    MenuDefaults.Label(
                                        contentAlignment = Alignment.CenterStart,
                                        padding = PaddingValues(start = 4.dp, end = 4.dp)
                                    ) {
                                        Text(stringResource(R.string.quest_overview_screen_dropdown_sort_title))
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_filter_alt_filled),
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    dropdownExpanded = false
                                    onUiEvent(QuestOverviewUiEvent.ShowDialog(QuestOverviewDialogState.SortingBottomSheet))
                                }
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.quest_overview_title),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            BasicPlainTooltip(
                text = stringResource(R.string.quest_overview_create_quest_tooltip),
                position = TooltipAnchorPosition.Above
            ) {
                FloatingActionButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)

                        onUiEvent(
                            QuestOverviewUiEvent.OnNavigateToCreateQuestScreen(
                                categoryId = if ((desiredPageIndex - 1) < 0) null else (desiredPageIndex - 1)
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null,
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allTabs.forEachIndexed { index, tab ->
                        var chipDropdownExpanded by remember { mutableStateOf(false) }
                        val interactionSource = remember { MutableInteractionSource() }

                        Box(
                            modifier = Modifier
                                .bringIntoViewRequester(requesters[index]),
                        ) {
                            ListChip(
                                selected = pagerState.currentPage == index,
                                interactionSource = interactionSource,
                                onClick = {
                                    scope.launch {
                                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)

                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                onLongClick = {
                                    if (tab.id != -1) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                        chipDropdownExpanded = true
                                    } else {
                                        null
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.text
                                    )
                                }
                            )

                            DropdownMenuPopup(
                                expanded = chipDropdownExpanded,
                                onDismissRequest = { chipDropdownExpanded = false },
                                offset = DpOffset(x = 0.dp, y = 8.dp)
                            ) {
                                DropdownMenuGroup(
                                    shapes = MenuDefaults.groupShapes()
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            MenuDefaults.Label(
                                                contentAlignment = Alignment.CenterStart,
                                                padding = PaddingValues(start = 4.dp, end = 4.dp)
                                            ) {
                                                Text(stringResource(R.string.manage_categories_bottom_sheet_dropdown_rename_title))
                                            }
                                        },
                                        shapes = MenuDefaults.itemShape(0, 2),
                                        selected = false,
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_edit_filled),
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            chipDropdownExpanded = false

                                            onUiEvent(
                                                QuestOverviewUiEvent.ShowUpdateCategoryDialog(
                                                    questCategoryEntity = tab
                                                )
                                            )
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            MenuDefaults.Label(
                                                contentAlignment = Alignment.CenterStart,
                                                padding = PaddingValues(start = 4.dp, end = 4.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.manage_categories_bottom_sheet_dropdown_delete_title)
                                                )
                                            }
                                        },
                                        shapes = MenuDefaults.itemShape(1, 2),
                                        selected = false,
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_delete_filled),
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            chipDropdownExpanded = false
                                            onUiEvent(
                                                QuestOverviewUiEvent.ShowDialog(
                                                    QuestOverviewDialogState.DeleteCategory(tab)
                                                )
                                            )
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = MaterialTheme.colorScheme.error,
                                            trailingIconColor = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            }
                        }
                    }

                    BasicPlainTooltip(
                        text = stringResource(R.string.quest_overview_create_list_tooltip),
                        position = TooltipAnchorPosition.Below
                    ) {
                        ListChip(
                            selected = false,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                onUiEvent(QuestOverviewUiEvent.ShowDialog(QuestOverviewDialogState.CreateCategory))
                            },
                            label = {},
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add),
                                    contentDescription = null,
                                )
                            }
                        )
                    }
                }

                PullToRefreshBox(
                    state = refreshState,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = {
                        onUiEvent(QuestOverviewUiEvent.Refresh)
                    },
                    indicator = {
                        PullToRefreshDefaults.LoadingIndicator(
                            isRefreshing = uiState.isRefreshing,
                            state = refreshState,
                            modifier = Modifier.align(Alignment.TopCenter),
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        key = { pageIndex ->
                            allTabs.getOrNull(pageIndex)?.uuid ?: "temp_page_$pageIndex"
                        }
                    ) { pageIndex ->
                        if (pageIndex == 0) {
                            AllQuestsPage(
                                state = uiState.allQuestPageState,
                                onEditQuestClick = {
                                    onUiEvent(QuestOverviewUiEvent.OnNavigateToEditQuestScreen(it))
                                },
                                onQuestCheck = { quest ->
                                    onUiEvent(
                                        QuestOverviewUiEvent.OnQuestChecked(
                                            questEntity = quest
                                        )
                                    )
                                },
                                onQuestClick = { id ->
                                    onUiEvent(
                                        QuestOverviewUiEvent.OnNavigateToQuestDetailScreen(entryId = id)
                                    )
                                },
                                onCreateNewQuestButtonClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)

                                    onUiEvent(
                                        QuestOverviewUiEvent.OnNavigateToCreateQuestScreen(
                                            categoryId = if ((desiredPageIndex - 1) < 0) null else (desiredPageIndex - 1)
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val categoryIndex = pageIndex - 1
                            if (categoryIndex < categories.size) {
                                val category = categories[categoryIndex]

                                QuestsForCategoryPage(
                                    categoryId = category.id,
                                    onQuestClicked = {
                                        onUiEvent(QuestOverviewUiEvent.OnNavigateToQuestDetailScreen(it))
                                    },
                                    onQuestChecked = {
                                        onUiEvent(QuestOverviewUiEvent.OnQuestChecked(it))
                                    },
                                    onEditQuestClicked = {
                                        onUiEvent(QuestOverviewUiEvent.OnNavigateToEditQuestScreen(it))
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            if (dialogState is QuestOverviewDialogState.QuestDone) {
                val questDoneDialogState = dialogState.questDoneDialogState
                QuestDoneDialog(
                    state = questDoneDialogState,
                    onDismiss = {
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    }
                )
            }

            if (uiState.dialogState is QuestOverviewDialogState.SortingBottomSheet) {
                QuestSortingBottomSheet(
                    onDismiss = {
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    },
                    sortingDirection = allQuestPageState.sortingDirections,
                    showCompletedQuests = allQuestPageState.showCompleted,
                    onSortingDirectionChange = { sortingDirection ->
                        onUiEvent(QuestOverviewUiEvent.UpdateQuestSortingDirection(sortingDirection))
                    },
                    onShowCompletedQuestsChange = { showCompletedQuests ->
                        onUiEvent(QuestOverviewUiEvent.UpdateShowCompletedQuests(value = showCompletedQuests))
                    }
                )
            }

            if (dialogState is QuestOverviewDialogState.CreateCategory) {
                CreateCategoryDialog(
                    onConfirm = { value ->
                        onUiEvent(QuestOverviewUiEvent.AddQuestCategory(value = value))
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    },
                    onDismiss = {
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    },
                    initialInputFocussed = true
                )
            }

            if (dialogState is QuestOverviewDialogState.UpdateCategory) {
                RenameCategoryDialog(
                    onDismiss = {
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    },
                    onConfirm = { value ->
                        onUiEvent(
                            QuestOverviewUiEvent.UpdateQuestCategory(
                                questCategoryEntity = dialogState.questCategoryEntity,
                                value = value
                            )
                        )
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    },
                    initialInputFocussed = true,
                    initialValue = dialogState.questCategoryEntity.text
                )
            }

            if (dialogState is QuestOverviewDialogState.DeleteCategory) {
                DeleteQuestCategoryDialog(
                    onConfirm = {
                        onUiEvent(
                            QuestOverviewUiEvent.DeleteQuestCategory(
                                questCategoryEntity = dialogState.questCategoryEntity
                            )
                        )
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    },
                    onDismiss = {
                        onUiEvent(QuestOverviewUiEvent.CloseDialog)
                    },
                    questCategoryEntity = dialogState.questCategoryEntity
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
    )
}
