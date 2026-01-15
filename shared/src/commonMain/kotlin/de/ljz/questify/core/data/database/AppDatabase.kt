package de.ljz.questify.core.data.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.ljz.questify.core.data.database.adapters.Converters
import de.ljz.questify.feature.habits.data.daos.HabitDao
import de.ljz.questify.feature.habits.data.models.HabitEntity
import de.ljz.questify.feature.quests.data.daos.QuestCategoryDao
import de.ljz.questify.feature.quests.data.daos.QuestDao
import de.ljz.questify.feature.quests.data.daos.QuestNotificationDao
import de.ljz.questify.feature.quests.data.daos.SubQuestDao
import de.ljz.questify.feature.quests.data.models.QuestCategoryEntity
import de.ljz.questify.feature.quests.data.models.QuestEntity
import de.ljz.questify.feature.quests.data.models.QuestNotificationEntity
import de.ljz.questify.feature.quests.data.models.SubQuestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        // Quests
        QuestEntity::class,
        QuestNotificationEntity::class,
        QuestCategoryEntity::class,

        // SubQuests
        SubQuestEntity::class,

        // Habits
        HabitEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ]
)
@TypeConverters(
    value = [
        Converters::class
    ]
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val questDao: QuestDao
    abstract val questNotificationDao: QuestNotificationDao
    abstract val questCategoryDao: QuestCategoryDao
    abstract val subQuestDao: SubQuestDao

    abstract val habitDao: HabitDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}