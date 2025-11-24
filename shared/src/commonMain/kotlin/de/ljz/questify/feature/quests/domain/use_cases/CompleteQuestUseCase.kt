package de.ljz.questify.feature.quests.domain.use_cases

import de.ljz.questify.feature.player_stats.domain.repositories.PlayerStatsRepository
import de.ljz.questify.feature.player_stats.domain.use_cases.UpdatePlayerStatsUseCase
import de.ljz.questify.feature.quests.data.models.QuestCompletionResult
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.domain.repositories.QuestRepository
import kotlinx.coroutines.flow.first
import kotlin.math.pow
import kotlin.time.Clock
import kotlin.time.Instant

const val BASE_XP = 100.0
const val LEVEL_FACTOR = 1.15

class CompleteQuestUseCase(
    private val questRepository: QuestRepository,
    private val playerStatsRepository: PlayerStatsRepository,

    private val updatePlayerStatsUseCase: UpdatePlayerStatsUseCase
) {

    suspend operator fun invoke(quest: QuestEntity): QuestCompletionResult {
        val completionDate: Instant = Clock.System.now()
        val completedQuest = quest.copy(done = true)
        questRepository.updateQuest(completedQuest)

        val currentStats = playerStatsRepository.getPlayerStats().first()
        val oldLevel = currentStats.level

        var rewardXp = quest.difficulty.xpValue
        var rewardPoints = quest.difficulty.pointsValue

        val isOverdue = quest.dueDate?.let { it < completionDate } ?: false

        if (isOverdue) {
            rewardXp /= 2
            rewardPoints /= 2
        }

        var newXp = currentStats.xp + rewardXp
        var newLevel = currentStats.level

        var xpForNextLevel = calculateXpForNextLevel(newLevel)
        while (newXp >= xpForNextLevel) {
            newLevel++
            newXp -= xpForNextLevel
            xpForNextLevel = calculateXpForNextLevel(newLevel)
        }

        val newStats = currentStats.copy(
            xp = newXp,
            level = newLevel,
            points = currentStats.points + rewardPoints
        )
        updatePlayerStatsUseCase.invoke(newStats)

        return QuestCompletionResult(
            earnedXp = rewardXp,
            earnedPoints = rewardPoints,
            didLevelUp = newLevel > oldLevel,
            oldLevel = oldLevel,
            newLevel = newLevel
        )
    }

    fun calculateXpForNextLevel(level: Int): Int {
        return (BASE_XP * LEVEL_FACTOR.pow(level - 1)).toInt()
    }
}