package de.ljz.questify.feature.quests.di

import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.feature.quests.data.daos.QuestCategoryDao
import de.ljz.questify.feature.quests.data.daos.QuestDao
import de.ljz.questify.feature.quests.data.daos.QuestNotificationDao
import de.ljz.questify.feature.quests.data.daos.SubQuestDao
import de.ljz.questify.feature.quests.domain.repositories.QuestCategoryRepository
import de.ljz.questify.feature.quests.domain.repositories.QuestCategoryRepositoryImpl
import de.ljz.questify.feature.quests.domain.repositories.QuestNotificationRepository
import de.ljz.questify.feature.quests.domain.repositories.QuestNotificationRepositoryImpl
import de.ljz.questify.feature.quests.domain.repositories.QuestRepository
import de.ljz.questify.feature.quests.domain.repositories.QuestRepositoryImpl
import de.ljz.questify.feature.quests.domain.repositories.SubQuestRepository
import de.ljz.questify.feature.quests.domain.repositories.SubQuestRepositoryImpl
import de.ljz.questify.feature.quests.domain.use_cases.AddQuestCategoryUseCase
import de.ljz.questify.feature.quests.domain.use_cases.AddQuestNotificationUseCase
import de.ljz.questify.feature.quests.domain.use_cases.AddSubQuestUseCase
import de.ljz.questify.feature.quests.domain.use_cases.AddSubQuestsUseCase
import de.ljz.questify.feature.quests.domain.use_cases.CheckSubQuestUseCase
import de.ljz.questify.feature.quests.domain.use_cases.DeleteQuestCategoryUseCase
import de.ljz.questify.feature.quests.domain.use_cases.DeleteQuestUseCase
import de.ljz.questify.feature.quests.domain.use_cases.DeleteSubQuestUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetAllQuestCategoriesUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetAllQuestsForCategoryUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetAllQuestsUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetNotificationsByQuestIdUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetQuestByIdAsFlowUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetQuestByIdUseCase
import de.ljz.questify.feature.quests.domain.use_cases.GetQuestCategoryByIdUseCase
import de.ljz.questify.feature.quests.domain.use_cases.RemoveNotificationsUseCase
import de.ljz.questify.feature.quests.domain.use_cases.UpdateQuestCategoryUseCase
import de.ljz.questify.feature.quests.domain.use_cases.UpsertQuestUseCase
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.QuestOverviewViewModel
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.sub_pages.quest_for_category_page.CategoryQuestViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val questModule = module {
    single<QuestCategoryDao> {
        get<AppDatabase>().questCategoryDao
    }

    single<QuestDao> {
        get<AppDatabase>().questDao
    }

    single<QuestNotificationDao> {
        get<AppDatabase>().questNotificationDao
    }

    single<SubQuestDao> {
        get<AppDatabase>().subQuestDao
    }

    singleOf(::QuestCategoryRepositoryImpl) { bind<QuestCategoryRepository>() }
    singleOf(::QuestNotificationRepositoryImpl) { bind<QuestNotificationRepository>() }
    singleOf(::QuestRepositoryImpl) { bind<QuestRepository>() }
    singleOf(::SubQuestRepositoryImpl) { bind<SubQuestRepository>() }

    factoryOf(::AddQuestCategoryUseCase)
    factoryOf(::AddQuestNotificationUseCase)
    factoryOf(::AddSubQuestUseCase)
    factoryOf(::AddSubQuestsUseCase)
    factoryOf(::CheckSubQuestUseCase)
    factoryOf(::DeleteQuestCategoryUseCase)
    factoryOf(::DeleteQuestUseCase)
    factoryOf(::DeleteSubQuestUseCase)
    factoryOf(::GetAllQuestCategoriesUseCase)
    factoryOf(::GetAllQuestsForCategoryUseCase)
    factoryOf(::GetAllQuestsUseCase)
    factoryOf(::GetNotificationsByQuestIdUseCase)
    factoryOf(::GetQuestByIdAsFlowUseCase)
    factoryOf(::GetQuestByIdUseCase)
    factoryOf(::GetQuestCategoryByIdUseCase)
    factoryOf(::RemoveNotificationsUseCase)
    factoryOf(::UpdateQuestCategoryUseCase)
    factoryOf(::UpsertQuestUseCase)

    viewModelOf(::QuestOverviewViewModel)
    viewModel { (categoryId: Int) ->
        CategoryQuestViewModel(
            categoryId = categoryId,
            getAllQuestsForCategoryUseCase = get()
        )
    }
}