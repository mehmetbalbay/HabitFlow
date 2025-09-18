package com.habitflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RoutineProfile(
    val wakeTime: String,
    val sleepTime: String,
    val workBlocks: List<TimeBlock>,
    val mealWindows: List<MealWindow>,
    val exerciseSlots: List<ExerciseSlot>,
    val quietHours: QuietHours,
    val hydrationGoal: Int,
    val isNightShift: Boolean = false,
    val goals: List<String> = emptyList()
)

@Serializable
data class TimeBlock(
    val start: String,
    val end: String,
    val label: String
)

@Serializable
data class MealWindow(
    val mealType: String,
    val time: String
)

@Serializable
data class ExerciseSlot(
    val label: String,
    val start: String,
    val end: String
)

@Serializable
data class QuietHours(
    val start: String,
    val end: String
)
