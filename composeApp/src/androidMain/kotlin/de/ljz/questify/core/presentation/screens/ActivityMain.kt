package de.ljz.questify.core.presentation.screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import de.ljz.questify.core.presentation.navigation.AppNavKey
import de.ljz.questify.core.presentation.navigation.ScaleTransitionDirection
import de.ljz.questify.core.presentation.navigation.scaleContentTransform
import de.ljz.questify.core.presentation.theme.QuestifyTheme
import de.ljz.questify.feature.main.presentation.screens.main.MainRoute
import de.ljz.questify.feature.main.presentation.screens.main.MainScreen
import de.ljz.questify.feature.onboarding.presentation.screens.onboarding.OnboardingRoute
import de.ljz.questify.feature.onboarding.presentation.screens.onboarding.OnboardingScreen
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestRoute
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestScreen
import de.ljz.questify.feature.quests.presentation.screens.edit_quest.EditQuestRoute
import de.ljz.questify.feature.quests.presentation.screens.edit_quest.EditQuestScreen
import de.ljz.questify.feature.quests.presentation.screens.quest_detail.QuestDetailRoute
import de.ljz.questify.feature.quests.presentation.screens.quest_detail.QuestDetailScreen

class ActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        enableEdgeToEdge()

        setContent {
            splashScreen.setKeepOnScreenCondition { true }
            /*val vm: AppViewModel by viewModels()

            val appUiState by vm.uiState.collectAsState()
            val isSetupDone = appUiState.isSetupDone
            val isAppReadyState by vm.isAppReady.collectAsState()*/

            if (true) {
                splashScreen.setKeepOnScreenCondition { false }

                QuestifyTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
//                        val startKey: AppNavKey = if (isSetupDone) MainRoute else OnboardingRoute
                        val startKey: AppNavKey = MainRoute
                        val backStack = rememberNavBackStack(startKey)

                        NavDisplay(
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            backStack = backStack,
                            entryProvider = entryProvider {
                                entry<MainRoute> {
                                    MainScreen(
                                        onNavigateToSettingsPermissionScreen = { backNavigationEnabled ->
                                            backStack.clear()
                                            /*backStack.add(
                                                SettingsPermissionRoute(
                                                    backNavigationEnabled = backNavigationEnabled
                                                )
                                            )*/
                                        },
                                        onNavigateToSettingsScreen = {
//                                            backStack.add(SettingsMainRoute)
                                        },
                                        onNavigateToCreateQuestScreen = { selectedList ->
                                            backStack.add(
                                                CreateQuestRoute(
                                                    selectedCategoryIndex = selectedList
                                                )
                                            )
                                        },
                                        onNavigateToEditQuestScreen = { id ->
                                            backStack.add(
                                                EditQuestRoute(
                                                    id = id
                                                )
                                            )
                                        },
                                        onNavigateToQuestDetailScreen = { id ->
                                            backStack.add(
                                                QuestDetailRoute(
                                                    id = id
                                                )
                                            )
                                        },
                                        onNavigateToCreateHabitScreen = {
//                                            backStack.add(CreateHabitRoute)
                                        }
                                    )
                                }

                                entry<OnboardingRoute> {
                                    OnboardingScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        onNavigateToMainScreen = {
                                            backStack.clear()
//                                            backStack.add(MainRoute)
                                        }
                                    )
                                }

                                entry<CreateQuestRoute> { key ->
                                    CreateQuestScreen(
                                        selectedCategoryIndex = key.selectedCategoryIndex,
                                        onNavigateBack = {
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                }

                                entry<EditQuestRoute> { key ->
                                    EditQuestScreen(
                                        id = key.id,
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                }

                                entry<QuestDetailRoute> { key ->
                                    QuestDetailScreen(
                                        questId = key.id,
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                }

                                /*entry<MainRoute> {
                                    MainScreen(
                                        onNavigateToSettingsPermissionScreen = { backNavigationEnabled ->
                                            backStack.clear()
                                            backStack.add(
                                                SettingsPermissionRoute(
                                                    backNavigationEnabled = backNavigationEnabled
                                                )
                                            )
                                        },
                                        onNavigateToSettingsScreen = {
                                            backStack.add(SettingsMainRoute)
                                        },
                                        onNavigateToCreateQuestScreen = { selectedList ->
                                            backStack.add(
                                                CreateQuestRoute(
                                                    selectedCategoryIndex = selectedList
                                                )
                                            )
                                        },
                                        onNavigateToEditQuestScreen = { id ->
                                            backStack.add(
                                                EditQuestRoute(
                                                    id = id
                                                )
                                            )
                                        },
                                        onNavigateToQuestDetailScreen = { id ->
                                            backStack.add(
                                                QuestDetailRoute(
                                                    id = id
                                                )
                                            )
                                        },
                                        onNavigateToCreateHabitScreen = {
                                            backStack.add(CreateHabitRoute)
                                        }
                                    )
                                }

                                entry<OnboardingRoute> {
                                    OnboardingScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        onNavigateToMainScreen = {
                                            backStack.clear()
                                            backStack.add(MainRoute)
                                        }
                                    )
                                }



                                entry<SettingsMainRoute> {
                                    SettingsMainScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        onNavigateToViewProfileScreen = {
                                            backStack.add(ViewProfileRoute)
                                        },
                                        onNavigateToSettingsAppearanceScreen = {
                                            backStack.add(SettingsAppearanceRoute)
                                        },
                                        onNavigateToSettingsHelpScreen = {
                                            backStack.add(SettingsHelpRoute)
                                        }
                                    )
                                }

                                entry<SettingsAppearanceRoute> {
                                    SettingsAppearanceScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                }

                                entry<SettingsHelpRoute> {
                                    SettingsHelpScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        onNavigateToOnboardingScreen = {
                                            backStack.add(OnboardingRoute)
                                        },
                                        onNavigateToSettingsPermissionScreen = {
                                            backStack.add(SettingsPermissionRoute())
                                        }
                                    )
                                }

                                entry<SettingsPermissionRoute> { key ->
                                    PermissionsScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        canNavigateBack = key.backNavigationEnabled
                                    )
                                }

                                entry<ViewProfileRoute> {
                                    ViewProfileScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        onNavigateToEditProfileScreen = {
                                            backStack.add(EditProfileRoute)
                                        }
                                    )
                                }

                                entry<EditProfileRoute> {
                                    EditProfileScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        }
                                    )
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
        }
    }
}