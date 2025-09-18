package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_rules")
data class ReminderRuleEntity(
    @PrimaryKey val id: String,
    val targetType: String,
    val windowStart: String?,
    val windowEnd: String?,
    val minIntervalMin: Int?,
    val quietStart: String?,
    val quietEnd: String?
)

