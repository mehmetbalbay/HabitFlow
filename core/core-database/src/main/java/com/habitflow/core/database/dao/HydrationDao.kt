package com.habitflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitflow.core.database.entity.HydrationEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HydrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: HydrationEntryEntity)

    @Query("DELETE FROM hydration_entries WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM hydration_entries WHERE dateTime LIKE :dayPrefix || '%' ORDER BY dateTime DESC")
    fun observeDay(dayPrefix: String): Flow<List<HydrationEntryEntity>>
}

