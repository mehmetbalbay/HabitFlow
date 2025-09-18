package com.habitflow.domain.model

data class ExerciseSession(
    val id: String,
    val type: String,
    val durationMin: Int,
    val steps: Int? = null,
    val kcal: Int? = null,
    val source: String = "manual",
    val note: String? = null,
    val dateTime: String
)

