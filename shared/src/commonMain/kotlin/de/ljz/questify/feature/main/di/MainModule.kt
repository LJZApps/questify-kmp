package de.ljz.questify.feature.main.di

import de.ljz.questify.feature.main.presentation.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {
    viewModelOf(::MainViewModel)
}