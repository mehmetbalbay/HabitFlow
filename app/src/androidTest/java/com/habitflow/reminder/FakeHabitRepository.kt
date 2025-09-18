package com.habitflow.reminder

import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.core.ui.DateUtils
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ToggleCall(val id: String, val date: String, val completed: Boolean)

@Singleton
class FakeHabitRepository @Inject constructor() : HabitRepository {
    private val habitsFlow = MutableStateFlow<List<Habit>>(emptyList())
    private val remindersFlow = MutableStateFlow(false)
    val toggleCalls = mutableListOf<ToggleCall>()

    fun reset() {
        toggleCalls.clear()
        remindersFlow.value = false
    }

    override val habits: StateFlow<List<Habit>> = habitsFlow
    override val remindersEnabled: StateFlow<Boolean> = remindersFlow

    override fun addHabit(
        name: String,
        reminderType: ReminderType,
        reminderTime: String?,
        weeklyDay: Int?,
        customDateTime: String?
    ): Habit {
        val habit = Habit(
            id = name,
            name = name,
            createdAt = DateUtils.todayKey(),
            reminderType = reminderType,
            reminderTime = reminderTime,
            weeklyDay = weeklyDay,
            customDateTime = customDateTime
        )
        habitsFlow.value = habitsFlow.value + habit
        return habit
    }

    override fun toggleCompletion(id: String, dateKey: String, completed: Boolean) {
        toggleCalls += ToggleCall(id, dateKey, completed)
    }

    override fun deleteHabit(id: String) {
        habitsFlow.value = habitsFlow.value.filterNot { it.id == id }
    }

    override fun setRemindersEnabled(enabled: Boolean) {
        remindersFlow.value = enabled
    }

    override fun onUserChanged(userId: String?) = Unit
}
