package com.habitflow.feature.habit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.core.ui.DateUtils
import com.habitflow.domain.model.DayCount
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.model.WeekProgress
import com.habitflow.domain.usecase.AddHabit
import com.habitflow.domain.usecase.HabitUseCases
import com.habitflow.domain.usecase.SetRemindersEnabled
import com.habitflow.domain.usecase.ToggleHabitCompletion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val habitUseCases: HabitUseCases,
    private val addHabitUseCase: AddHabit,
    private val toggleHabitCompletion: ToggleHabitCompletion,
    private val setRemindersEnabledUseCase: SetRemindersEnabled
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                habitUseCases.observeHabits(),
                habitUseCases.observeReminderSetting()
            ) { habits, reminders ->
                HabitUiState(
                    habits = habits,
                    todayKey = DateUtils.todayKey(),
                    remindersEnabled = reminders,
                    dailyCounts = DateUtils.dailyCounts(habits),
                    weeklyProgress = DateUtils.weeklyPercentages(habits)
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun addHabit(
        name: String,
        reminderType: ReminderType,
        reminderTime: String?,
        weeklyDay: Int?,
        customDateTime: String?
    ) = addHabitUseCase(name, reminderType, reminderTime, weeklyDay, customDateTime)

    fun markToday(habitId: String, done: Boolean) {
        toggleHabitCompletion(habitId, DateUtils.todayKey(), done)
    }

    fun deleteHabit(habitId: String) {
        habitUseCases.deleteHabit(habitId)
    }

    fun setRemindersEnabled(enabled: Boolean) {
        setRemindersEnabledUseCase(enabled)
    }
}

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val todayKey: String = DateUtils.todayKey(),
    val remindersEnabled: Boolean = false,
    val dailyCounts: List<DayCount> = emptyList(),
    val weeklyProgress: List<WeekProgress> = emptyList()
)

