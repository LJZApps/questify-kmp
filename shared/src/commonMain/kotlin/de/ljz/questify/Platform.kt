package de.ljz.questify

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect class Platform() {
    val name: String
}

val platformModule = module {
    singleOf(::Platform)
}

fun appModule() = listOf(platformModule)