package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import de.ljz.questify.feature.quests.domain.repositories.QuestRepository

class GetQuestByIdUseCase(
    private val questRepository: QuestRepository
) {
    suspend operator fun invoke(id: Int): QuestWithDetails {
        return questRepository.getQuestById(id = id)
    }
}