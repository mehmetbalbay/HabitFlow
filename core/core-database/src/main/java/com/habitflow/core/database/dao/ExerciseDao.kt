package com.habitflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitflow.core.database.entity.ExerciseSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: ExerciseSessionEntity)

    @Query("DELETE FROM exercise_sessions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM exercise_sessions WHERE dateTime LIKE :weekPrefix || '%' ORDER BY dateTime DESC")
    fun observeWeek(weekPrefix: String): Flow<List<ExerciseSessionEntity>>
}

