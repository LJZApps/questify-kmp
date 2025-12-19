package de.ljz.questify.feature.habis.presentation.screens.overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HabitOverviewScreen(
    onNavigateUp: () -> Unit,
    viewModel: HabitOverviewViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HabitOverviewScreen(
        uiState = uiState,
        onUiEvent = { event ->
            when (event) {
                is HabitOverviewUiEvent.OnNavigateUp -> onNavigateUp()

                else -> viewModel.onUiEvent(event)
            }
        }
    )
}

@Composable
private fun HabitOverviewScreen(
    uiState: HabitOverviewUiState,
    onUiEvent: (HabitOverviewUiEvent) -> Unit
) {

}