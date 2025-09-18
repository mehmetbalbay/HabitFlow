package com.habitflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitflow.core.database.entity.RoutineProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineProfileDao {
    @Query("SELECT * FROM routine_profile WHERE id = :id LIMIT 1")
    fun observe(id: Int = RoutineProfileEntity.ID): Flow<RoutineProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RoutineProfileEntity)

    @Query("SELECT * FROM routine_profile WHERE id = :id LIMIT 1")
    suspend fun get(id: Int = RoutineProfileEntity.ID): RoutineProfileEntity?
}
