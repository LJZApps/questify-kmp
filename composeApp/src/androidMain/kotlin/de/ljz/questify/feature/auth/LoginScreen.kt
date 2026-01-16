package de.ljz.questify.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    // ViewModel wird via Koin injiziert
    viewModel: LoginViewModel = koinViewModel(),
    onNavigateHome: () -> Unit,
    onNavigateOnboarding: (String?) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    // Reagieren auf Status-Änderungen (Navigation)
    LaunchedEffect(state) {
        println("LoginScreen: State Änderung erkannt: $state")
        when (val s = state) {
            is LoginUiState.NavigateToHome -> {
                println("LoginScreen: Navigiere zu Home")
                onNavigateHome()
            }
            is LoginUiState.NavigateToOnboarding -> {
                println("LoginScreen: Navigiere zu Onboarding für ${s.username}")
                onNavigateOnboarding(s.username)
            }
            is LoginUiState.Error -> {
                println("LoginScreen: Fehlerstatus angezeigt: ${s.message}")
            }
            LoginUiState.Loading -> {
                println("LoginScreen: Lade-Status...")
            }
            LoginUiState.Idle -> {
                println("LoginScreen: Idle-Status")
            }
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Willkommen bei Questify",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Logge dich ein, um deine Quests zu synchronisieren.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (val s = state) {
                    is LoginUiState.Loading -> {
                        CircularProgressIndicator()
                        Text("Verbinde mit Server...", style = MaterialTheme.typography.bodySmall)
                    }

                    is LoginUiState.Error -> {
                        Text(
                            text = s.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = {
                            println("LoginScreen: 'Erneut versuchen' geklickt")
//                            val url = viewModel.getLoginUrl()
//                            println("LoginScreen: Öffne URL: $url")
//                            uriHandler.openUri(url)
                        }) {
                            Text("Erneut versuchen")
                        }
                    }

                    else -> {
                        Button(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            onClick = {
                                println("LoginScreen: 'Anmelden mit Omrix ID' geklickt")
                                // Browser öffnen
//                                val url = viewModel.getLoginUrl()
//                                println("LoginScreen: Öffne URL: $url")
//                                uriHandler.openUri(url)
                            }
                        ) {
                            Text("Anmelden mit Omrix ID")
                        }
                    }
                }
            }
        }
    }
}