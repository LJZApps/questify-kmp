package de.ljz.questify.feature.main.presentation.screens.main

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import de.ljz.questify.core.presentation.navigation.ScaleTransitionDirection
import de.ljz.questify.core.presentation.navigation.scaleContentTransform
import de.ljz.questify.core.presentation.theme.QuestifyTheme
import de.ljz.questify.feature.habis.presentation.screens.overview.HabitOverviewRoute
import de.ljz.questify.feature.habis.presentation.screens.overview.HabitOverviewScreen
import de.ljz.questify.feature.main.presentation.components.DrawerContent
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.QuestOverviewScreen
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.QuestsRoute
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreen(
    onNavigateToSettingsPermissionScreen: (Boolean) -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToCreateQuestScreen: (Int?) -> Unit,
    onNavigateToQuestDetailScreen: (Int) -> Unit,
    onNavigateToEditQuestScreen: (Int) -> Unit,
    onNavigateToCreateHabitScreen: () -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    /*val allPermissionsGranted = remember {
        isNotificationPermissionGranted(context) &&
                isOverlayPermissionGranted(context) &&
                isAlarmPermissionGranted(context)
    }*/

    /*LaunchedEffect(allPermissionsGranted) {
        if (!allPermissionsGranted) {
            onNavigateToSettingsPermissionScreen(false)
        }
    }

    if (!allPermissionsGranted) return*/

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val backStack = rememberNavBackStack(QuestsRoute)

    QuestifyTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    uiState = uiState,
                    backStack = backStack,
                    drawerState = drawerState,
                    onNavigateToSettingsScreen = onNavigateToSettingsScreen
                )
            }
        ) {
            NavDisplay(
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                backStack = backStack,
                entryProvider = entryProvider {
                    entry<QuestsRoute> {
                        QuestOverviewScreen(
                            onNavigateToQuestDetailScreen = { id ->
                                onNavigateToQuestDetailScreen(id)
                            },
                            onNavigateToCreateQuestScreen = onNavigateToCreateQuestScreen,
                            onNavigateToEditQuestScreen = { id ->
                                onNavigateToEditQuestScreen(id)
                            },
                            onToggleDrawer = {
                                scope.launch {
                                    drawerState.apply {
                                        if (drawerState.currentValue == DrawerValue.Closed) open() else close()
                                    }
                                }
                            }
                        )
                    }

                    entry<HabitOverviewRoute> {
                        HabitOverviewScreen(
                            onNavigateUp = {
                                if (backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            },
                            onToggleDrawer = {
                                scope.launch {
                                    drawerState.apply {
                                        if (drawerState.currentValue == DrawerValue.Closed) open() else close()
                                    }
                                }
                            }
                        )
                    }

                    /*entry<HabitsRoute> {
                        HabitsScreen(
                            onNavigateToCreateHabitScreen = {
                                onNavigateToCreateHabitScreen()
                            },
                            onToggleDrawer = {
                                scope.launch {
                                    drawerState.apply {
                                        if (drawerState.currentValue == DrawerValue.Closed) open() else close()
                                    }
                                }
                            }
                        )
                    }

                    entry<RoutinesOverviewRoute> {
                        RoutinesOverviewScreen(
                            onToggleDrawer = {
                                scope.launch {
                                    drawerState.apply {
                                        if (drawerState.currentValue == DrawerValue.Closed) open() else close()
                                    }
                                }
                            }
                        )
                    }

                    entry<StatsRoute> {
                        *//*StatsScreen(
                            drawerState = drawerState,
                            navController = mainNavController
                        )*//*
                    }*/
                },
                transitionSpec = {
                    scaleContentTransform(ScaleTransitionDirection.INWARDS)
                },
                popTransitionSpec = {
                    scaleContentTransform(ScaleTransitionDirection.OUTWARDS)
                },
                predictivePopTransitionSpec = {
                    scaleContentTransform(ScaleTransitionDirection.OUTWARDS)
                }
            )
        }
    }
}
