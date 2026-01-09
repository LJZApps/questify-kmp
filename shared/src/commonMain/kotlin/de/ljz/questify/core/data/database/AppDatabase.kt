package de.ljz.questify.core.data.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.ljz.questify.core.data.database.adapters.InstantAdapter
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
    version = 6,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ]
)
@TypeConverters(
    value = [
        InstantAdapter::class
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

private val MIGRATION_3_6 = object : Migration(3, 6) {
    override fun migrate(connection: SQLiteConnection) {
        addSyncColumns(connection)
    }
}

private val MIGRATION_4_6 = object : Migration(4, 6) {
    override fun migrate(connection: SQLiteConnection) {
        addSyncColumns(connection)
    }
}

private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        addSyncColumns(connection)
    }
}

private fun addSyncColumns(connection: SQLiteConnection) {
    fun exec(sql: String) {
        try {
            connection.prepare(sql).use { it.step() }
        } catch (e: Exception) {
            // Ignore errors if columns already exist
        }
    }

    // QuestEntity
    exec("ALTER TABLE quest_entity ADD COLUMN remote_id INTEGER DEFAULT NULL")
    exec("ALTER TABLE quest_entity ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'SYNCED'")

    // QuestCategoryEntity
    exec("ALTER TABLE quest_category_entity ADD COLUMN remote_id INTEGER DEFAULT NULL")
    exec("ALTER TABLE quest_category_entity ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'SYNCED'")
    exec("ALTER TABLE quest_category_entity ADD COLUMN updated_at INTEGER DEFAULT NULL")

    // SubQuestEntity
    exec("ALTER TABLE sub_quest_entity ADD COLUMN remote_id INTEGER DEFAULT NULL")
    exec("ALTER TABLE sub_quest_entity ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'SYNCED'")
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .addMigrations(MIGRATION_3_6, MIGRATION_4_6, MIGRATION_5_6)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}