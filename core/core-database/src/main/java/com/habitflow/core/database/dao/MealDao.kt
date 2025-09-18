package com.habitflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitflow.core.database.entity.MealLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meal: MealLogEntity)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM meal_logs WHERE dateTime LIKE :dayPrefix || '%' ORDER BY dateTime DESC")
    fun observeDay(dayPrefix: String): Flow<List<MealLogEntity>>
}

