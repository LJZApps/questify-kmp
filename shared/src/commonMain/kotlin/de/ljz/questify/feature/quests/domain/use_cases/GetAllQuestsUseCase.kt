package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.data.relations.QuestWithSubQuests
import de.ljz.questify.feature.quests.domain.repositories.QuestRepository
import kotlinx.coroutines.flow.Flow

class GetAllQuestsUseCase(
    private val questRepository: QuestRepository
) {
    suspend operator fun invoke(): Flow<List<QuestWithSubQuests>> {
        return questRepository.getQuests()
    }
}