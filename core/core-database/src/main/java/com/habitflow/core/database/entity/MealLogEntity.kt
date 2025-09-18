package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_logs")
data class MealLogEntity(
    @PrimaryKey val id: String,
    val mealType: String,
    val photoUri: String?,
    val note: String?,
    val tagsCsv: String?,
    val calories: Int?,
    val protein: Int?,
    val fat: Int?,
    val carbs: Int?,
    val dateTime: String
)

