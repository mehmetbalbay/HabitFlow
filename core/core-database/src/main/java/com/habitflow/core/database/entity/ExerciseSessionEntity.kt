package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_sessions")
data class ExerciseSessionEntity(
    @PrimaryKey val id: String,
    val type: String,
    val durationMin: Int,
    val steps: Int?,
    val kcal: Int?,
    val source: String,
    val note: String?,
    val dateTime: String
)

