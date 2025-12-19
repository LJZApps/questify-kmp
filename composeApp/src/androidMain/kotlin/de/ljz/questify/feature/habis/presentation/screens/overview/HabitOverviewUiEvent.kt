package de.ljz.questify.feature.habis.presentation.screens.overview

sealed interface HabitOverviewUiEvent {
    object OnNavigateUp : HabitOverviewUiEvent
    object OnToggleDrawer : HabitOverviewUiEvent
}