package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = ID,
    val remindersEnabled: Boolean
) {
    companion object {
        const val ID = 0
    }
}
