package com.habitflow.reminder

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.habitflow.core.database.HabitFlowDatabase
import com.habitflow.core.database.entity.HabitCompletionEntity
import com.habitflow.core.database.entity.HabitEntity
import com.habitflow.core.database.entity.SettingsEntity
import com.habitflow.domain.model.ReminderType
import com.habitflow.util.DateUtils
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderWorkerTest {

    private lateinit var context: Context
    private lateinit var database: HabitFlowDatabase
    private val showCalls = mutableListOf<Pair<String?, String?>>()
    private val minuteCalls = mutableListOf<MinuteCall>()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, HabitFlowDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        HabitFlowDatabase.setTestInstance { database }

        ReminderScheduler.onShowReminder = { _, habitId, habitName ->
            showCalls += habitId to habitName
        }
        ReminderScheduler.onScheduleMinuteReminder = { _, delay, habitId, habitName ->
            minuteCalls += MinuteCall(delay, habitId, habitName)
        }
        ReminderScheduler.notificationsEnabledOverride = true
    }

    @After
    fun tearDown() {
        ReminderScheduler.onShowReminder = null
        ReminderScheduler.onScheduleMinuteReminder = null
        ReminderScheduler.notificationsEnabledOverride = null
        HabitFlowDatabase.setTestInstance(null)
        database.close()
        showCalls.clear()
        minuteCalls.clear()
    }

    @Test
    fun skipsWorkWhenRemindersDisabled() = runBlocking {
        val dao = database.habitDao()
        dao.upsertSettings(SettingsEntity(remindersEnabled = false))

        val worker = TestListenableWorkerBuilder<ReminderWorker>(context)
            .setInputData(androidx.work.Data.EMPTY)
            .build()
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)
        assertTrue(showCalls.isEmpty())
        assertTrue(minuteCalls.isEmpty())
    }

    @Test
    fun showsReminderForNextIncompleteHabit() = runBlocking {
        val dao = database.habitDao()
        val todayKey = DateUtils.todayKey()
        dao.upsertSettings(SettingsEntity(remindersEnabled = true))
        dao.insertHabit(
            HabitEntity(
                id = "habit-1",
                name = "Meditate",
                createdAt = todayKey,
                reminderType = ReminderType.DAILY,
                reminderTime = null,
                weeklyDay = null,
                customDateTime = null
            )
        )
        dao.insertHabit(
            HabitEntity(
                id = "habit-2",
                name = "Read",
                createdAt = todayKey,
                reminderType = ReminderType.DAILY,
                reminderTime = null,
                weeklyDay = null,
                customDateTime = null
            )
        )
        dao.insertCompletion(HabitCompletionEntity("habit-2", todayKey))

        val worker = TestListenableWorkerBuilder<ReminderWorker>(context)
            .setInputData(androidx.work.Data.EMPTY)
            .build()
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)
        assertEquals(listOf("habit-1" to "Meditate"), showCalls)
        assertEquals(1, minuteCalls.size)
        val minuteCall = minuteCalls.first()
        assertEquals(1L, minuteCall.delayMinutes)
        assertEquals(null, minuteCall.habitId)
        assertEquals(null, minuteCall.habitName)
    }

    private data class MinuteCall(
        val delayMinutes: Long,
        val habitId: String?,
        val habitName: String?
    )
}
