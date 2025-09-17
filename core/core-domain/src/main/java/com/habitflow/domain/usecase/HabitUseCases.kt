package com.habitflow.domain.usecase

import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.repository.HabitRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

data class HabitUseCases @Inject constructor(
    val observeHabits: ObserveHabits,
    val observeReminderSetting: ObserveReminderSetting,
    val addHabit: AddHabit,
    val toggleCompletion: ToggleHabitCompletion,
    val deleteHabit: DeleteHabit,
    val setRemindersEnabled: SetRemindersEnabled,
    val onUserChanged: OnUserChanged
)

class ObserveHabits @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> = repository.habits
}

class ObserveReminderSetting @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.remindersEnabled
}

class AddHabit @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(
        name: String,
        reminderType: ReminderType,
        reminderTime: String?,
        weeklyDay: Int?,
        customDateTime: String?
    ): Habit = repository.addHabit(name, reminderType, reminderTime, weeklyDay, customDateTime)
}

class ToggleHabitCompletion @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(id: String, dateKey: String, completed: Boolean) {
        repository.toggleCompletion(id, dateKey, completed)
    }
}

class DeleteHabit @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(id: String) {
        repository.deleteHabit(id)
    }
}

class SetRemindersEnabled @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(enabled: Boolean) {
        repository.setRemindersEnabled(enabled)
    }
}

class OnUserChanged @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(userId: String?) {
        repository.onUserChanged(userId)
    }
}
