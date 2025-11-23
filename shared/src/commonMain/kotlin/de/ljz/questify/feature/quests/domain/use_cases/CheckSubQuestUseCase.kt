package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.quests.domain.repositories.SubQuestRepository

class CheckSubQuestUseCase(
    private val subQuestRepository: SubQuestRepository
) {

    suspend operator fun invoke(id: Int, checked: Boolean) {
        subQuestRepository.checkSubQuest(
            id = id,
            checked = checked
        )
    }

}