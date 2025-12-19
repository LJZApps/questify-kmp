package de.ljz.questify.feature.habis.presentation.screens.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ljz.questify.R
import de.ljz.questify.core.presentation.components.tooltips.BasicPlainTooltip
import de.ljz.questify.feature.habits.data.models.descriptors.NavigationBarItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HabitOverviewScreen(
    onNavigateUp: () -> Unit,
    onToggleDrawer: () -> Unit,
    viewModel: HabitOverviewViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HabitOverviewScreen(
        uiState = uiState,
        onUiEvent = { event ->
            when (event) {
                is HabitOverviewUiEvent.OnNavigateUp -> onNavigateUp()
                is HabitOverviewUiEvent.OnToggleDrawer -> onToggleDrawer()

                else -> viewModel.onUiEvent(event)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitOverviewScreen(
    uiState: HabitOverviewUiState,
    onUiEvent: (HabitOverviewUiEvent) -> Unit
) {
    val navigationBarItems = listOf(
        NavigationBarItem(
            label = "Täglich",
            iconRes = R.drawable.ic_today_outlined
        ),
        NavigationBarItem(
            label = "Wöchentlich",
            iconRes = R.drawable.ic_date_range_outlined
        ),
        NavigationBarItem(
            label = "Monatlich",
            iconRes = R.drawable.ic_calendar_month_outlined
        )
    )

    val haptic = LocalHapticFeedback.current
    val pagerState = rememberPagerState { navigationBarItems.count() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Gewohnheiten",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    BasicPlainTooltip(
                        text = stringResource(R.string.quest_overview_nav_drawer_tooltip)
                    ) {
                        IconButton(
                            onClick = {
                                onUiEvent(HabitOverviewUiEvent.OnToggleDrawer)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = null
                            )
                        }
                    }
                },
                actions = {
                    BasicPlainTooltip(
                        text = "Archiv",
                        position = TooltipAnchorPosition.Below
                    ) {
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_inbox_outlined),
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ShortNavigationBar {
                navigationBarItems.forEachIndexed { index, item ->
                    ShortNavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)

                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.iconRes),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = item.label
                            )
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            BasicPlainTooltip(
                text = "Neue Gewohnheit erstellen",
                position = TooltipAnchorPosition.Above
            ) {
                FloatingActionButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null
                    )
                }
            }
        },
        content = { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                key = { it }
            ) { index ->
                when (index) {
                    0 -> Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("1")
                    }

                    1 -> Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("2")
                    }

                    2 -> Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("3")
                    }
                }
            }
        }
    )
}