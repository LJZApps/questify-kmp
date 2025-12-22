package de.ljz.questify.feature.settings.presentation.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ljz.questify.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsMainScreen(
    onNavigateUp: () -> Unit,
    onNavigateToViewProfileScreen: () -> Unit,
    onNavigateToSettingsAppearanceScreen: () -> Unit,
    onNavigateToSettingsHelpScreen: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_screen_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigateUp() },
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                SegmentedListItem(
                    onClick = {
                        onNavigateToViewProfileScreen()
                    },
                    shapes = ListItemDefaults.segmentedShapes(0, 4),
                    supportingContent = {
                        Text("Mein Profil bei Questify")
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    leadingContent = {
                        Icon(Icons.Outlined.PersonOutline, contentDescription = null)
                    }
                ) {
                    Text("Mein Profil")
                }

                SegmentedListItem(
                    onClick = {
                        // TODO
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shapes = ListItemDefaults.segmentedShapes(1, 4),
                    supportingContent = {
                        Text("Benachrichtigungen für diese App")
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Notifications, contentDescription = null)
                    }
                ) {
                    Text(
                        text = "Benachrichtigungen",
                    )
                }

                SegmentedListItem(
                    onClick = {
                        onNavigateToSettingsAppearanceScreen()
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shapes = ListItemDefaults.segmentedShapes(2, 4),
                    supportingContent = {
                        Text(stringResource(R.string.settings_main_screen_appearance_description))
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.ColorLens, contentDescription = null)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.settings_main_screen_appearance_title),
                    )
                }

                SegmentedListItem(
                    onClick = {
                        onNavigateToSettingsHelpScreen()
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shapes = ListItemDefaults.segmentedShapes(3, 4),
                    supportingContent = {
                        Text(stringResource(R.string.settings_main_screen_help_description))
                    },
                    leadingContent = {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.settings_main_screen_help_title)
                    )
                }
            }
            /*ExpressiveSettingsMenuLink(
                title =  stringResource(R.string.settings_main_screen_features_title) ,
                subtitle =  stringResource(R.string.settings_main_screen_features_description),
                icon = { Icon(Icons.Outlined.Extension, contentDescription = null) },
                onClick = {
                    mainNavController.navigate(SettingsFeaturesRoute)
                }
            )*/

            /*ExpressiveSettingsMenuLink(
                title = "Experimente",
                subtitle = "Teste neue Funktionen vor allen anderen",
                icon = { Icon(painter = painterResource(R.drawable.ic_experiment_outlined), contentDescription = null) },
                onClick = {
//                        mainNavController.navigate(SettingsAppearanceRoute)
                }
            )*/
        }
    }
}