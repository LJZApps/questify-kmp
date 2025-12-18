package de.ljz.questify.feature.settings.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import de.ljz.questify.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsMainScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateUp: () -> Unit,
    onNavigateToViewProfileScreen: () -> Unit,
    onNavigateToSettingsAppearanceScreen: () -> Unit,
    onNavigateToSettingsHelpScreen: () -> Unit
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
            SegmentedListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    onNavigateToViewProfileScreen()
                },
                shapes = ListItemShapes(
                    shape = MaterialTheme.shapes.large,
                    selectedShape = MaterialTheme.shapes.large,
                    pressedShape = MaterialTheme.shapes.large,
                    focusedShape = MaterialTheme.shapes.large,
                    hoveredShape = MaterialTheme.shapes.large,
                    draggedShape = MaterialTheme.shapes.large
                ),
                supportingContent = {
                    Text(uiState.aboutMe)
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.userProfilePicture.isNotEmpty()) {
                            AsyncImage(
                                model = uiState.userProfilePicture,
                                contentDescription = stringResource(R.string.profile_picture_content_description),
                                modifier = Modifier
                                    .size(40.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(R.string.profile_picture_content_description),
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            ) {
                Text(uiState.userName)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                SegmentedListItem(
                    onClick = {
                        onNavigateToSettingsAppearanceScreen()
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shapes = ListItemDefaults.segmentedShapes(0, 2),
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
                    shapes = ListItemDefaults.segmentedShapes(1, 2),
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