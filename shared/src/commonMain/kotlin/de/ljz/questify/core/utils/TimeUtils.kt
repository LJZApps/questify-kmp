package de.ljz.questify.core.utils

import kotlin.time.Instant

object TimeUtils {
    @OptIn(kotlin.time.ExperimentalTime::class)
    fun now(): Instant = kotlin.time.Clock.System.now()
}
