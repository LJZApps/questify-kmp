package de.ljz.questify

import de.ljz.questify.core.di.commonModule
import de.ljz.questify.core.di.dataStoreModule
import de.ljz.questify.core.di.syncModule
import de.ljz.questify.feature.habits.di.habitModule
import de.ljz.questify.feature.player_stats.di.playerStatsModule
import de.ljz.questify.feature.profile.di.profileModule
import de.ljz.questify.feature.quests.di.questModule
import de.ljz.questify.feature.settings.di.settingsModule

internal fun appModule() = listOf(
    commonModule,
    questModule,
    dataStoreModule,
    playerStatsModule,
    profileModule,
    settingsModule,
    habitModule,
    syncModule
)
