package com.habitflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitflow.core.database.entity.UserPrefsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPrefsDao {
    @Query("SELECT * FROM user_prefs WHERE id = :id LIMIT 1")
    fun observe(id: Int = UserPrefsEntity.ID): Flow<UserPrefsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(prefs: UserPrefsEntity)
}

