package de.ljz.questify

import androidx.room.RoomDatabase
import de.ljz.questify.core.data.database.AppDatabase
import de.ljz.questify.core.data.database.getDatabaseBuilder
import de.ljz.questify.core.notifications.IosNotificationScheduler
import de.ljz.questify.core.notifications.NotificationScheduler
import de.ljz.questify.feature.onboarding.presentation.screens.onboarding.OnboardingViewModel
import de.ljz.questify.feature.quests.presentation.screens.create_quest.CreateQuestViewModel
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.QuestOverviewViewModel
import de.ljz.questify.feature.quests.presentation.screens.quest_overview.sub_pages.quest_for_category_page.CategoryQuestViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val iosModule = module {
    single<NotificationScheduler> { IosNotificationScheduler() }

    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
}

fun initKoin() {
    startKoin {
        modules(appModule() + iosModule)
    }
}

object ProvideViewModel : KoinComponent {
    fun getQuestOverviewViewModel(): QuestOverviewViewModel = get()
    fun getCategoryQuestViewModel(categoryId: Int): CategoryQuestViewModel {
        return get { parametersOf(categoryId) }
    }
    fun getOnboardingViewModel(): OnboardingViewModel = get()
    fun getCreateQuestViewModel(selectedQuestCategoryIndex: Int?): CreateQuestViewModel {
        return get { parametersOf(selectedQuestCategoryIndex) }
    }
}