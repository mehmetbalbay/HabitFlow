package com.habitflow.core.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habitflow.core.database.dao.HabitDao
import com.habitflow.core.database.entity.HabitCompletionEntity
import com.habitflow.core.database.entity.HabitEntity
import com.habitflow.core.database.entity.SettingsEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        SettingsEntity::class,
        com.habitflow.core.database.entity.HydrationEntryEntity::class,
        com.habitflow.core.database.entity.ExerciseSessionEntity::class,
        com.habitflow.core.database.entity.MealLogEntity::class,
        com.habitflow.core.database.entity.ReminderRuleEntity::class,
        com.habitflow.core.database.entity.UserPrefsEntity::class,
        com.habitflow.core.database.entity.RoutineProfileEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HabitFlowDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun hydrationDao(): com.habitflow.core.database.dao.HydrationDao
    abstract fun exerciseDao(): com.habitflow.core.database.dao.ExerciseDao
    abstract fun mealDao(): com.habitflow.core.database.dao.MealDao
    abstract fun reminderRuleDao(): com.habitflow.core.database.dao.ReminderRuleDao
    abstract fun userPrefsDao(): com.habitflow.core.database.dao.UserPrefsDao
    abstract fun routineProfileDao(): com.habitflow.core.database.dao.RoutineProfileDao

    companion object {
        @Volatile
        private var INSTANCE: HabitFlowDatabase? = null
        @Volatile
        internal var onTestInstance: (() -> HabitFlowDatabase)? = null

        fun getInstance(context: Context): HabitFlowDatabase =
            onTestInstance?.invoke() ?: INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HabitFlowDatabase::class.java,
                    "habitflow.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }

        @VisibleForTesting
        fun setTestInstance(provider: (() -> HabitFlowDatabase)?) {
            onTestInstance = provider
            if (provider == null) {
                INSTANCE = null
            }
        }
    }
}
