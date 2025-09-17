package com.habitflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.habitflow.core.database.entity.HabitCompletionEntity
import com.habitflow.core.database.entity.HabitEntity
import com.habitflow.core.database.entity.HabitWithCompletions
import com.habitflow.core.database.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Transaction
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun observeHabitsWithCompletions(): Flow<List<HabitWithCompletions>>

    @Transaction
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    suspend fun getHabitsWithCompletionsOnce(): List<HabitWithCompletions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletionEntity)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: String, date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletions(completions: List<HabitCompletionEntity>)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: String)

    @Query("DELETE FROM habit_completions")
    suspend fun clearCompletions()

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("SELECT * FROM settings WHERE id = :id LIMIT 1")
    fun observeSettings(id: Int = SettingsEntity.ID): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = :id LIMIT 1")
    suspend fun getSettingsOnce(id: Int = SettingsEntity.ID): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: SettingsEntity)
}
