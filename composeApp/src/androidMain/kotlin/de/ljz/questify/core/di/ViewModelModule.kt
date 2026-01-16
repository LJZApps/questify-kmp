package de.ljz.questify.core.di

import de.ljz.questify.core.presentation.screens.AppViewModel
import de.ljz.questify.core.presentation.theme.ThemeViewModel
import de.ljz.questify.feature.auth.LoginViewModel
import de.ljz.questify.feature.habis.presentation.screens.overview.HabitOverviewViewModel
import de.ljz.questify.feature.main.presentation.screens.main.MainViewModel
import de.ljz.questify.feature.onboarding.presentation.screens.onboarding.OnboardingViewModel
import de.ljz.questify.feature.profile.presentation.screens.edit_profile.EditProfileViewModel
import de.ljz.questify.feature.profile.presentation.screens.username_setup.UsernameSetupViewModel
import de.ljz.questify.feature.profile.presentation.screens.view_profile.ViewProfileViewModel
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestViewModel
import de.ljz.questify.feature.quests.presentation.screens.edit_quest.EditQuestViewModel
import de.ljz.questify.feature.quests.presentation.screens.quest_detail.QuestDetailViewModel
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.QuestOverviewViewModel
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.sub_pages.quest_for_category_page.CategoryQuestViewModel
import de.ljz.questify.feature.settings.presentation.screens.appearance.SettingsAppearanceViewModel
import de.ljz.questify.feature.settings.presentation.screens.login.SettingsLoginViewModel
import de.ljz.questify.feature.settings.presentation.screens.main.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::ThemeViewModel)
    viewModelOf(::AppViewModel)

    viewModelOf(::MainViewModel)

    viewModelOf(::OnboardingViewModel)

    viewModelOf(::QuestOverviewViewModel)

    viewModelOf(::ViewProfileViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::UsernameSetupViewModel)

    single { LoginViewModel(get()) }

    single { SettingsLoginViewModel(get(), get()) }

    viewModel { (categoryId: Int) ->
        CategoryQuestViewModel(
            categoryId = categoryId,
            getAllQuestsForCategoryUseCase = get(),
            getQuestSortingPreferencesUseCase = get()
        )
    }

    viewModel { (selectedCategoryIndex: Int?) ->
        CreateQuestViewModel(
            selectedCategoryIndex = selectedCategoryIndex,
            addSubQuestsUseCase = get(),
            addQuestCategoryUseCase = get(),
            getAllQuestCategoriesUseCase = get(),
            upsertQuestUseCase = get(),
            addQuestNotificationUseCase = get(),
            syncRepository = get()
        )
    }

    viewModel { (questId: Int) ->
        EditQuestViewModel(
            id = questId,
            upsertQuestUseCase = get(),
            getQuestByIdUseCase = get(),
            getQuestByIdAsFlowUseCase = get(),
            deleteQuestUseCase = get(),
            addQuestCategoryUseCase = get(),
            getAllQuestCategoriesUseCase = get(),
            addSubQuestsUseCase = get(),
            deleteSubQuestsUseCase = get(),
            deleteSubQuestUseCase = get(),
            addQuestNotificationUseCase = get(),
            cancelQuestNotificationsUseCase = get(),
        )
    }

    viewModel { (questId: Int) ->
        QuestDetailViewModel(
            id = questId,
            getQuestByIdAsFlowUseCase = get(),
            completeQuestUseCase = get(),
            deleteQuestUseCase = get(),
            getAllQuestCategoriesUseCase = get(),
            getQuestCategoryByIdUseCase = get(),
            checkSubQuestUseCase = get(),
            cancelQuestNotificationsUseCase = get(),
            syncRepository = get()
        )
    }

    viewModelOf(::SettingsViewModel)
    viewModelOf(::SettingsAppearanceViewModel)

    viewModelOf(::HabitOverviewViewModel)
}
