package com.habitflow.core.data.repository.impl

import com.habitflow.core.data.repository.impl.DefaultHabitRepository
import com.habitflow.core.data.source.remote.HabitRemoteSync
import com.habitflow.core.database.dao.HabitDao
import com.habitflow.core.database.entity.HabitCompletionEntity
import com.habitflow.core.database.entity.HabitEntity
import com.habitflow.core.database.entity.HabitWithCompletions
import com.habitflow.core.database.entity.SettingsEntity
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import com.habitflow.domain.time.DateProvider

@OptIn(ExperimentalCoroutinesApi::class)
class HabitRepositoryTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val dispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope
    private lateinit var habitDao: FakeHabitDao
    private lateinit var remoteSync: HabitRemoteSync
    private lateinit var repository: HabitRepository

    private val fixedDate = LocalDate.of(2024, 1, 10)

    @Before
    fun setup() {
        testScope = TestScope(dispatcher)
        habitDao = FakeHabitDao()
        remoteSync = mockk(relaxed = true)
        every { remoteSync.isAvailable } returns true
        coEvery { remoteSync.upsertHabit(any(), any()) } returns Unit
        coEvery { remoteSync.deleteHabit(any(), any()) } returns Unit
        every { remoteSync.startListening(any(), any(), any()) } answers { }
        every { remoteSync.clear() } answers { }

        repository = DefaultHabitRepository(
            habitDao = habitDao,
            remoteSync = remoteSync,
            dateProvider = object : DateProvider { override fun today(): LocalDate = fixedDate },
            uuidProvider = { "generated-id" },
            externalScope = testScope,
            transactionRunner = { block -> block() }
        )

        testScope.runCurrent()
    }

    @Test
    fun addHabit_insertsHabitAndEmitsState() {
        val habit = repository.addHabit(
            name = "Meditasyon",
            reminderType = ReminderType.DAILY,
            reminderTime = "08:00",
            weeklyDay = null,
            customDateTime = null
        )

        testScope.runCurrent()

        coVerify { remoteSync.upsertHabit(null, habit) }
        val emitted = repository.habits.value
        assertEquals(1, emitted.size)
        assertEquals("generated-id", emitted.first().id)
        assertEquals("08:00", emitted.first().reminderTime)
    }

    @Test
    fun toggleCompletion_updatesHistoryAndSyncsRemote() {
        val habit = repository.addHabit(
            name = "Spor",
            reminderType = ReminderType.DAILY,
            reminderTime = null,
            weeklyDay = null,
            customDateTime = null
        )
        testScope.runCurrent()

        val dateKey = "2024-01-09"
        repository.toggleCompletion(habit.id, dateKey, completed = true)
        testScope.runCurrent()

        coVerify { remoteSync.upsertHabit(null, match { it.history[dateKey] == true }) }
        val emitted = repository.habits.value
        val updatedHabit = emitted.first { it.id == habit.id }
        assertTrue(updatedHabit.history[dateKey] == true)
    }

    @Test
    fun setRemindersEnabled_persistsSetting() {
        repository.setRemindersEnabled(true)
        testScope.runCurrent()

        assertTrue(repository.remindersEnabled.value)
        assertTrue(habitDao.currentSettings?.remindersEnabled == true)
    }

    @Test
    fun deleteHabit_removesLocalDataAndSyncsRemote() {
        val habit = repository.addHabit(
            name = "Okuma",
            reminderType = ReminderType.DAILY,
            reminderTime = null,
            weeklyDay = null,
            customDateTime = null
        )
        testScope.runCurrent()
        repository.toggleCompletion(habit.id, "2024-01-09", true)
        testScope.runCurrent()

        repository.deleteHabit(habit.id)
        testScope.runCurrent()

        coVerify { remoteSync.deleteHabit(null, habit.id) }
        assertTrue(repository.habits.value.isEmpty())
        assertEquals(0, habitDao.habitCount())
        assertTrue(habitDao.completionsFor(habit.id).isEmpty())
    }
}

private class FakeHabitDao : HabitDao {
    private val habits = linkedMapOf<String, HabitEntity>()
    private val completions = linkedMapOf<Pair<String, String>, HabitCompletionEntity>()
    private val habitFlow = MutableStateFlow<List<HabitWithCompletions>>(emptyList())
    private val settingsFlow = MutableStateFlow<SettingsEntity?>(null)

    val currentSettings: SettingsEntity?
        get() = settingsFlow.value

    fun habitCount(): Int = habits.size

    fun completionsFor(habitId: String): List<HabitCompletionEntity> =
        completions.values.filter { it.habitId == habitId }

    private fun emitState() {
        habitFlow.value = habits.values
            .sortedByDescending { it.createdAt }
            .map { entity ->
                HabitWithCompletions(
                    habit = entity,
                    completions = completions.values.filter { it.habitId == entity.id }
                )
            }
    }

    override fun observeHabitsWithCompletions(): Flow<List<HabitWithCompletions>> = habitFlow

    override suspend fun getHabitsWithCompletionsOnce(): List<HabitWithCompletions> = habitFlow.value

    override suspend fun insertHabit(habit: HabitEntity) {
        habits[habit.id] = habit
        emitState()
    }

    override suspend fun deleteHabit(habitId: String) {
        habits.remove(habitId)
        completions.keys.filter { it.first == habitId }.toList().forEach { completions.remove(it) }
        emitState()
    }

    override suspend fun insertCompletion(completion: HabitCompletionEntity) {
        completions[completion.habitId to completion.date] = completion
        emitState()
    }

    override suspend fun deleteCompletion(habitId: String, date: String) {
        completions.remove(habitId to date)
        emitState()
    }

    override suspend fun insertHabits(habits: List<HabitEntity>) {
        habits.forEach { this.habits[it.id] = it }
        emitState()
    }

    override suspend fun insertCompletions(completions: List<HabitCompletionEntity>) {
        completions.forEach { this.completions[it.habitId to it.date] = it }
        emitState()
    }

    override suspend fun deleteCompletionsForHabit(habitId: String) {
        completions.keys.filter { it.first == habitId }.toList().forEach { completions.remove(it) }
        emitState()
    }

    override suspend fun clearCompletions() {
        completions.clear()
        emitState()
    }

    override suspend fun clearHabits() {
        habits.clear()
        completions.clear()
        emitState()
    }

    override fun observeSettings(id: Int): Flow<SettingsEntity?> = settingsFlow

    override suspend fun getSettingsOnce(id: Int): SettingsEntity? = settingsFlow.value

    override suspend fun upsertSettings(settings: SettingsEntity) {
        settingsFlow.value = settings
    }
}
