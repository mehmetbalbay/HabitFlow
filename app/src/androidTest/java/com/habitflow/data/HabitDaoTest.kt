package com.habitflow.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.habitflow.core.database.HabitFlowDatabase
import com.habitflow.core.database.dao.HabitDao
import com.habitflow.core.database.entity.HabitCompletionEntity
import com.habitflow.core.database.entity.HabitEntity
import com.habitflow.core.database.entity.SettingsEntity
import com.habitflow.domain.model.ReminderType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: HabitFlowDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, HabitFlowDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = database.habitDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertHabitAndCompletions_returnsHabitWithCompletion() = runBlocking {
        val habit = HabitEntity(
            id = "habit-1",
            name = "Meditasyon",
            createdAt = "2024-01-01",
            reminderType = ReminderType.DAILY,
            reminderTime = "08:00",
            weeklyDay = null,
            customDateTime = null
        )
        val completion = HabitCompletionEntity(
            habitId = habit.id,
            date = "2024-01-02"
        )

        habitDao.insertHabit(habit)
        habitDao.insertCompletion(completion)

        val result = habitDao.getHabitsWithCompletionsOnce()

        assertEquals(1, result.size)
        val habitWithCompletions = result.first()
        assertEquals(habit.id, habitWithCompletions.habit.id)
        assertEquals(1, habitWithCompletions.completions.size)
        assertEquals(completion.date, habitWithCompletions.completions.first().date)
    }

    @Test
    fun deleteCompletion_removesOnlyTargetRecord() = runBlocking {
        val habit = HabitEntity(
            id = "habit-2",
            name = "Kitap",
            createdAt = "2024-01-01",
            reminderType = ReminderType.DAILY,
            reminderTime = null,
            weeklyDay = null,
            customDateTime = null
        )
        val completionA = HabitCompletionEntity(habit.id, "2024-01-02")
        val completionB = HabitCompletionEntity(habit.id, "2024-01-03")

        habitDao.insertHabit(habit)
        habitDao.insertCompletions(listOf(completionA, completionB))

        habitDao.deleteCompletion(habit.id, completionA.date)

        val result = habitDao.getHabitsWithCompletionsOnce()
        val remainingDates = result.first().completions.map { it.date }

        assertEquals(listOf(completionB.date), remainingDates)
    }

    @Test
    fun upsertSettings_updatesExistingEntry() = runBlocking {
        val initial = SettingsEntity(remindersEnabled = false)
        val updated = initial.copy(remindersEnabled = true)

        habitDao.upsertSettings(initial)
        val stored = requireNotNull(habitDao.getSettingsOnce())
        assertFalse(stored.remindersEnabled)

        habitDao.upsertSettings(updated)
        val updatedStored = requireNotNull(habitDao.getSettingsOnce())
        assertTrue(updatedStored.remindersEnabled)
    }
}
