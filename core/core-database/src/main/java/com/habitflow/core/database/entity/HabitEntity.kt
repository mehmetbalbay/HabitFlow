package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.habitflow.domain.model.ReminderType

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: String,
    val reminderType: ReminderType,
    val reminderTime: String?,
    val weeklyDay: Int?,
    val customDateTime: String?
)
