package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_prefs")
data class UserPrefsEntity(
    @PrimaryKey val id: Int = ID,
    val unitsLiquid: String = "L", // L or oz
    val unitsEnergy: String = "kcal",
    val waterGoalMl: Int? = null,
    val exerciseDaysPerWeek: Int? = null,
    val theme: String = "system",
    val quietStart: String? = null,
    val quietEnd: String? = null,
    val weightKg: Float? = null,
    val heightCm: Float? = null
) {
    companion object { const val ID = 0 }
}

