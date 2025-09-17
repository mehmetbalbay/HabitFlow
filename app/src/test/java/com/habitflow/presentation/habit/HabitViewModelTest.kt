package com.habitflow.presentation.habit

import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.domain.usecase.AddHabit
import com.habitflow.domain.usecase.DeleteHabit
import com.habitflow.domain.usecase.HabitUseCases
import com.habitflow.domain.usecase.ObserveHabits
import com.habitflow.domain.usecase.ObserveReminderSetting
import com.habitflow.domain.usecase.OnUserChanged
import com.habitflow.domain.usecase.SetRemindersEnabled
import com.habitflow.domain.usecase.ToggleHabitCompletion
import com.habitflow.testing.MainDispatcherRule
import com.habitflow.util.DateUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var repository: HabitRepository
    private lateinit var habitsFlow: MutableStateFlow<List<Habit>>
    private lateinit var remindersFlow: MutableStateFlow<Boolean>
    private lateinit var viewModel: HabitViewModel

    @Before
    fun setup() {
        habitsFlow = MutableStateFlow(emptyList())
        remindersFlow = MutableStateFlow(false)
        repository = mockk(relaxed = true)

        every { repository.habits } returns habitsFlow
        every { repository.remindersEnabled } returns remindersFlow
        coEvery { repository.addHabit(any(), any(), any(), any(), any()) } answers {
            Habit(id = "generated", name = arg(0), createdAt = DateUtils.dateKey(java.time.LocalDate.now()))
        }

        val useCases = HabitUseCases(
            observeHabits = ObserveHabits(repository),
            observeReminderSetting = ObserveReminderSetting(repository),
            addHabit = AddHabit(repository),
            toggleCompletion = ToggleHabitCompletion(repository),
            deleteHabit = DeleteHabit(repository),
            setRemindersEnabled = SetRemindersEnabled(repository),
            onUserChanged = OnUserChanged(repository)
        )

        viewModel = HabitViewModel(
            habitUseCases = useCases,
            addHabitUseCase = useCases.addHabit,
            toggleHabitCompletion = useCases.toggleCompletion,
            setRemindersEnabledUseCase = useCases.setRemindersEnabled
        )
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun `ui state stays in sync with repository flows`() {
        val todayKey = DateUtils.todayKey()
        val habits = listOf(
            Habit(
                id = "1",
                name = "Spor",
                createdAt = todayKey,
                history = mapOf(todayKey to true),
                reminderType = ReminderType.DAILY
            )
        )
        habitsFlow.value = habits
        remindersFlow.value = true

        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(habits, state.habits)
        assertTrue(state.remindersEnabled)
        assertEquals(todayKey, state.todayKey)
        assertEquals(7, state.dailyCounts.size)
        assertEquals(1, state.dailyCounts.last().count)
        assertEquals(4, state.weeklyProgress.size)
    }

    @Test
    fun `markToday delegates completion update to repository`() {
        val habitId = "habit-1"
        viewModel.markToday(habitId, true)
        verify { repository.toggleCompletion(habitId, DateUtils.todayKey(), true) }
    }

    @Test
    fun `deleteHabit delegates to repository`() {
        viewModel.deleteHabit("habit-d")
        verify { repository.deleteHabit("habit-d") }
    }

    @Test
    fun `setRemindersEnabled updates reminder preference`() {
        viewModel.setRemindersEnabled(true)
        verify { repository.setRemindersEnabled(true) }
    }

    @Test
    fun `addHabit returns repository result`() {
        val habit = viewModel.addHabit(
            name = "Kitap",
            reminderType = ReminderType.DAILY,
            reminderTime = "09:00",
            weeklyDay = null,
            customDateTime = null
        )
        assertEquals("Kitap", habit.name)
        coVerify { repository.addHabit("Kitap", ReminderType.DAILY, "09:00", null, null) }
    }
}
