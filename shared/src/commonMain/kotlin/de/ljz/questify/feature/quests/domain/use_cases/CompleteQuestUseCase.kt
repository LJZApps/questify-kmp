package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.domain.repositories.QuestRepository

class CompleteQuestUseCase(
    private val questRepository: QuestRepository
) {
    suspend operator fun invoke(id: Int, done: Boolean): QuestRepository.QuestCompletionResult {
        return questRepository.setQuestDone(id, done)
    }
}
