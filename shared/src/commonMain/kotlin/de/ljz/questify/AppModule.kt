package de.ljz.questify

import de.ljz.questify.core.di.commonModule
import de.ljz.questify.core.di.dataStoreModule
import de.ljz.questify.feature.player_stats.di.playerStatsModule
import de.ljz.questify.feature.profile.di.appUserModule
import de.ljz.questify.feature.quests.di.questModule

fun appModule() = listOf(
    commonModule,
    questModule,
    dataStoreModule,
    playerStatsModule,
    appUserModule
)