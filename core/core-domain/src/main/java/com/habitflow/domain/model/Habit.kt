package com.habitflow.domain.model

data class Habit(
    val id: String,
    val name: String,
    val createdAt: String,
    val history: Map<String, Boolean> = emptyMap(),
    val reminderType: ReminderType = ReminderType.DAILY,
    val reminderTime: String? = null,
    val weeklyDay: Int? = null,
    val customDateTime: String? = null
)
