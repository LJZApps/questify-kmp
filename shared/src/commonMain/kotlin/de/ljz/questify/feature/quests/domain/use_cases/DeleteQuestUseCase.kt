package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.domain.repositories.QuestRepository

class DeleteQuestUseCase(
    private val questRepository: QuestRepository
) {
    suspend operator fun invoke(questId: Int) {
        questRepository.deleteQuest(questId)
    }
}