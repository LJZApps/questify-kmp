package de.ljz.questify.feature.settings.presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ljz.questify.R
import de.ljz.questify.core.utils.AuthUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsLoginScreen(
    onNavigateUp: () -> Unit,
    onNavigateToUsernameSetup: () -> Unit,
    viewModel: SettingsLoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigation Handling
    // Wir reagieren nur, wenn navigationTarget gesetzt ist.
    LaunchedEffect(uiState.navigationTarget) {
        uiState.navigationTarget?.let { target ->
            when (target) {
                LoginNavigationTarget.Back -> onNavigateUp()
                LoginNavigationTarget.UsernameSetup -> onNavigateToUsernameSetup()
            }
            // WICHTIG: Event feuern, damit der State zurückgesetzt wird
            // und wir nicht in einer Navigations-Schleife hängen bleiben.
            viewModel.onUiEvent(SettingsLoginUiEvent.NavigationHandled)
        }
    }

    SettingsLoginScreenContent(
        loginUrl = viewModel.getLoginUrl(),
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent // Methodenreferenz für kürzeren Code
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SettingsLoginScreenContent(
    loginUrl: String,
    uiState: SettingsLoginUiState,
    onUiEvent: (SettingsLoginUiEvent) -> Unit
) {
    val context = LocalContext.current

    // Dialog bleibt unverändert (nur Logik)
    if (uiState.dialogState is SettingsLoginDialogState.OmrixInformation) {
        AlertDialog(
            onDismissRequest = { onUiEvent(SettingsLoginUiEvent.OnCloseDialog) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_omrix),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            },
            title = { Text("Was ist OMRIX?") },
            text = {
                Text(
                    text = "OMRIX ist dein sicherer Login für Questify. Unser selbst entwickeltes System läuft auf ISO-zertifizierten Servern in Deutschland (Falkenstein). So stellen wir sicher, dass deine Daten bestmöglich geschützt sind und du die volle Kontrolle behältst."
                )
            },
            confirmButton = {
                TextButton(onClick = { onUiEvent(SettingsLoginUiEvent.OnCloseDialog) }) {
                    Text("Verstanden")
                }
            }
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp), // Etwas mehr Rand für Eleganz
            horizontalAlignment = Alignment.CenterHorizontally // Alles zentrieren
        ) {

            // 1. Oberer Bereich: Hero Visual & Text
            // Wir nutzen weight, damit dies den Platz füllt und optisch mittig wirkt
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_omrix),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Account-Sync aktivieren",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Verbinde dein Profil mit OMRIX, um deine Quests und Einstellungen automatisch zu sichern. Deine Daten bleiben dabei privat, verschlüsselt und gehören nur dir.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                uiState.error?.let { errorMessage ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp), // Abstand zum Bildschirmrand
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { AuthUtils.launchCustomTab(context, loginUrl) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    if (uiState.isLoading) {
                        LoadingIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_omrix),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Mit OMRIX anmelden")
                    }
                }

                TextButton(
                    onClick = {
                        onUiEvent(SettingsLoginUiEvent.OnShowDialog(SettingsLoginDialogState.OmrixInformation))
                    }
                ) {
                    Text(
                        text = "Warum ist OMRIX sicher?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}