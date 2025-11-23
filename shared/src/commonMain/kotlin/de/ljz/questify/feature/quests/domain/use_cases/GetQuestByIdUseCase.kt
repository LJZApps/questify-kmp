package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.data.relations.QuestWithSubQuests
import de.ljz.questify.feature.quests.domain.repositories.QuestRepository

class GetQuestByIdUseCase(
    private val questRepository: QuestRepository
) {
    suspend operator fun invoke(id: Int): QuestWithSubQuests {
        return questRepository.getQuestById(id = id)
    }
}