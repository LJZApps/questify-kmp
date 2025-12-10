package de.ljz.questify.feature.quests.presentation.screens.quest_overview

sealed interface QuestOverviewUiEffect {
    data class ShowDeleteSuccessfulSnackBar(val text: String) : QuestOverviewUiEffect
}