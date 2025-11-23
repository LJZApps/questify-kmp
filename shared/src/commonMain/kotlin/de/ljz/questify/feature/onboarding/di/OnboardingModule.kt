package de.ljz.questify.feature.onboarding.di

import de.ljz.questify.feature.onboarding.presentation.screens.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingModule = module {
    viewModelOf(::OnboardingViewModel)
}