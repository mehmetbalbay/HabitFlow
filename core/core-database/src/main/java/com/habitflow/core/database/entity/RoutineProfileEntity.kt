package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_profile")
data class RoutineProfileEntity(
    @PrimaryKey val id: Int = ID,
    val wakeTime: String,
    val sleepTime: String,
    val workBlocksJson: String,
    val mealWindowsJson: String,
    val exerciseSlotsJson: String,
    val quietStart: String,
    val quietEnd: String,
    val hydrationGoal: Int,
    val isNightShift: Boolean,
    val goalsJson: String
) {
    companion object { const val ID = 0 }
}

