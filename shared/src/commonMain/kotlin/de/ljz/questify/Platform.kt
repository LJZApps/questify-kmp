package de.ljz.questify

import de.ljz.questify.core.di.commonModule
import de.ljz.questify.feature.quests.di.questModule

fun appModule() = listOf(
    commonModule,
    questModule
)