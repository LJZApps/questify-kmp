package de.ljz.questify.core.presentation.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import de.ljz.questify.feature.auth.presentation.screens.login.LoginRoute
import de.ljz.questify.feature.auth.presentation.screens.login.LoginScreen
import de.ljz.questify.feature.auth.presentation.screens.login.LoginUiEvent
import de.ljz.questify.feature.auth.presentation.screens.login.LoginViewModel
import de.ljz.questify.feature.main.presentation.screens.main.MainRoute
import de.ljz.questify.feature.main.presentation.screens.main.MainScreen
import de.ljz.questify.feature.onboarding.presentation.screens.onboarding.OnboardingRoute
import de.ljz.questify.feature.onboarding.presentation.screens.onboarding.OnboardingScreen
import de.ljz.questify.feature.profile.presentation.screens.edit_profile.EditProfileRoute
import de.ljz.questify.feature.profile.presentation.screens.edit_profile.EditProfileScreen
import de.ljz.questify.feature.profile.presentation.screens.view_profile.ViewProfileRoute
import de.ljz.questify.feature.profile.presentation.screens.view_profile.ViewProfileScreen
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestRoute
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestScreen
import de.ljz.questify.feature.quests.presentation.screens.edit_quest.EditQuestRoute
import de.ljz.questify.feature.quests.presentation.screens.edit_quest.EditQuestScreen
import de.ljz.questify.feature.quests.presentation.screens.quest_detail.QuestDetailRoute
import de.ljz.questify.feature.quests.presentation.screens.quest_detail.QuestDetailScreen
import de.ljz.questify.feature.settings.presentation.screens.account.AccountSettingsRoute
import de.ljz.questify.feature.settings.presentation.screens.account.AccountSettingsScreen
import de.ljz.questify.feature.settings.presentation.screens.appearance.SettingsAppearanceRoute
import de.ljz.questify.feature.settings.presentation.screens.appearance.SettingsAppearanceScreen
import de.ljz.questify.feature.settings.presentation.screens.help.SettingsHelpRoute
import de.ljz.questify.feature.settings.presentation.screens.help.SettingsHelpScreen
import de.ljz.questify.feature.settings.presentation.screens.main.SettingsMainRoute
import de.ljz.questify.feature.settings.presentation.screens.main.SettingsMainScreen
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel

class ActivityMain : AppCompatActivity() {

    private val intentFlow = MutableStateFlow<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentFlow.value = intent

        val splashScreen = installSplashScreen()

        enableEdgeToEdge()

        setContent {
            splashScreen.setKeepOnScreenCondition { true }
            val vm: AppViewModel = koinViewModel()

            val appUiState by vm.uiState.collectAsState()
            val isSetupDone = appUiState.isSetupDone
            val isLoggedIn = appUiState.isLoggedIn
            val isAppReadyState by vm.isAppReady.collectAsState()

            if (isAppReadyState) {
                splashScreen.setKeepOnScreenCondition { false }

                QuestifyTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val startKey: AppNavKey = when {
                            !isSetupDone -> OnboardingRoute
                            else -> MainRoute
                        }
                        val backStack = rememberNavBackStack(startKey)

                        // Reagieren auf Login-Status
                        androidx.compose.runtime.LaunchedEffect(isLoggedIn, isSetupDone) {
                            if (isSetupDone && !isLoggedIn) {
                                backStack.clear()
                                backStack.add(LoginRoute())
                            }
                        }

                        // Handle OAuth Redirect
                        val currentIntent by intentFlow.collectAsState()
                        androidx.compose.runtime.LaunchedEffect(currentIntent) {
                            currentIntent?.data?.let { uri ->
                                if (uri.scheme == "questify" && uri.host == "auth" && uri.path == "/redirect") {
                                    val code = uri.getQueryParameter("code")
                                    if (code != null) {
                                        backStack.clear()
                                        backStack.add(LoginRoute(code = code))
                                        intentFlow.value = null // Intent nach Verarbeitung zurücksetzen
                                    }
                                }
                            }
                        }

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
//                                            backStack.add(CreateHabitRoute)
                                        }
                                    )
                                }

                                entry<LoginRoute> { key ->
                                    val loginVm: LoginViewModel = koinViewModel()
                                    
                                    androidx.compose.runtime.LaunchedEffect(key.code) {
                                        key.code?.let { code ->
                                            loginVm.onUiEvent(LoginUiEvent.HandleAuthCode(code))
                                        }
                                    }

                                    LoginScreen(
                                        viewModel = loginVm,
                                        onNavigateToBrowser = { url ->
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
                                            startActivity(intent)
                                        },
                                        onNavigateToOnboarding = {
                                            backStack.clear()
                                            backStack.add(OnboardingRoute)
                                        },
                                        onLoginSuccess = {
                                            backStack.clear()
                                            backStack.add(MainRoute)
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

                                entry<SettingsMainRoute> {
                                    SettingsMainScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        onNavigateToViewProfileScreen = {
                                            backStack.add(ViewProfileRoute)
                                        },
                                        onNavigateToAccountSettingsScreen = {
                                            backStack.add(AccountSettingsRoute)
                                        },
                                        onNavigateToLoginScreen = {
                                            backStack.add(LoginRoute())
                                        },
                                        onNavigateToSettingsAppearanceScreen = {
                                            backStack.add(SettingsAppearanceRoute)
                                        },
                                        onNavigateToSettingsHelpScreen = {
                                            backStack.add(SettingsHelpRoute)
                                        }
                                    )
                                }

                                entry<AccountSettingsRoute> {
                                    AccountSettingsScreen(
                                        onNavigateUp = {
                                            backStack.removeLastOrNull()
                                        },
                                        onLogoutSuccess = {
                                            backStack.clear()
                                            backStack.add(MainRoute)
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
                                        }
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
                                }
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentFlow.value = intent
    }
}

internal class DeepLinkRequest(
    val uri: Uri
) {
    val pathSegments: List<String> = uri.pathSegments

    val queries = buildMap {
        uri.queryParameterNames.forEach { argName ->
            this[argName] = uri.getQueryParameter(argName)
        }
    }
}