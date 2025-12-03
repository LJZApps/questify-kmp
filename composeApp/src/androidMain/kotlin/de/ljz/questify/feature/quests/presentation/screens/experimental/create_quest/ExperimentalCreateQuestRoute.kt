package de.ljz.questify.feature.quests.presentation.screens.experimental.create_quest

import de.ljz.questify.core.presentation.navigation.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class ExperimentalCreateQuestRoute(
    val selectedCategoryIndex: Int? = null
): AppNavKey