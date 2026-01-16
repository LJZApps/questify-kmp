package de.ljz.questify.core.data.network.dto

import de.ljz.questify.feature.quests.data.models.descriptors.Difficulty
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class SyncDtoTest {
    @Test
    fun testSerialization() {
        val now = Instant.fromEpochMilliseconds(1737033120000L) // 2025-01-16T13:12:00Z
        val request = SyncRequestDto(
            categories = listOf(QuestCategoryDto("uuid1", "cat1", now)),
            quests = listOf(QuestDto(
                uuid = "uuid2",
                categoryUuid = "uuid1",
                title = "title",
                notes = "notes",
                difficulty = Difficulty.EASY,
                dueDate = null,
                done = false,
                updatedAt = now
            )),
            subQuests = listOf(SubQuestDto("uuid3", "uuid2", "sub", false, 0, now)),
            playerStats = null,
            lastSyncTimestamp = now
        )

        val json = Json.encodeToString(request)
        val decoded = Json.decodeFromString<SyncRequestDto>(json)

        assertEquals(request, decoded)
    }
}
