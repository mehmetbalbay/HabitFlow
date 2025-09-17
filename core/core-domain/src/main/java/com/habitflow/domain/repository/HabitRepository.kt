package com.habitflow.domain.repository

import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import kotlinx.coroutines.flow.StateFlow

interface HabitRepository {
    val habits: StateFlow<List<Habit>>
    val remindersEnabled: StateFlow<Boolean>

    fun addHabit(
        name: String,
        reminderType: ReminderType,
        reminderTime: String?,
        weeklyDay: Int?,
        customDateTime: String?
    ): Habit

    fun toggleCompletion(id: String, dateKey: String, completed: Boolean)
    fun deleteHabit(id: String)
    fun setRemindersEnabled(enabled: Boolean)
    fun onUserChanged(userId: String?)
}
