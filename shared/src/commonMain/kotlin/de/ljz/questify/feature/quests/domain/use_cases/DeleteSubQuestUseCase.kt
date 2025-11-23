package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.domain.repositories.SubQuestRepository

class DeleteSubQuestUseCase(
    private val subQuestRepository: SubQuestRepository
) {
    suspend operator fun invoke(id: Int) {
        subQuestRepository.deleteSubQuest(id = id)
    }
}