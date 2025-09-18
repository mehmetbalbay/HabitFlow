package com.habitflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitflow.core.database.entity.ReminderRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rule: ReminderRuleEntity)

    @Query("DELETE FROM reminder_rules WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM reminder_rules")
    fun observeAll(): Flow<List<ReminderRuleEntity>>
}

