package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.data.relations.QuestWithDetails
import de.ljz.questify.feature.quests.domain.repositories.QuestRepository
import kotlinx.coroutines.flow.Flow

class GetQuestByIdAsFlowUseCase(
    private val questRepository: QuestRepository
) {
    suspend operator fun invoke(id: Int): Flow<QuestWithDetails?> {
        return questRepository.getQuestByIdFlow(id)
    }
}