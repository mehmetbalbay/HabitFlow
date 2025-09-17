package com.habitflow.reminder

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.util.DateUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(com.habitflow.core.data.di.RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
class ReminderActionReceiverTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: FakeHabitRepository

    private val minuteCalls = mutableListOf<MinuteCall>()
    private lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()
        repository.reset()
        ReminderScheduler.onScheduleMinuteReminder = { _, delay, habitId, habitName ->
            minuteCalls += MinuteCall(delay, habitId, habitName)
        }
        ReminderScheduler.notificationsEnabledOverride = true
    }

    @After
    fun tearDown() {
        ReminderScheduler.onScheduleMinuteReminder = null
        ReminderScheduler.notificationsEnabledOverride = null
        minuteCalls.clear()
        repository.reset()
    }

    @Test
    fun handlesMarkCompleteAction() = runBlocking {
        val habitId = "habit-1"
        val habitName = "Meditate"
        val intent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ReminderActionReceiver.ACTION_MARK_COMPLETE
            putExtra(ReminderScheduler.KEY_HABIT_ID, habitId)
            putExtra(ReminderScheduler.KEY_HABIT_NAME, habitName)
            putExtra("notification_id", 42)
        }

        ReminderActionReceiver().onReceive(context, intent)

        val todayKey = DateUtils.todayKey()
        assertEquals(listOf(ToggleCall(habitId, todayKey, true)), repository.toggleCalls)
        assertEquals(1, minuteCalls.size)
        val minuteCall = minuteCalls.first()
        assertEquals(1L, minuteCall.delayMinutes)
        assertEquals(null, minuteCall.habitId)
        assertEquals(null, minuteCall.habitName)
    }

    @Test
    fun handlesSnoozeAction() = runBlocking {
        val habitId = "habit-2"
        val habitName = "Read"
        val snoozeMinutes = 15L
        val intent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ReminderActionReceiver.ACTION_SNOOZE
            putExtra(ReminderScheduler.KEY_HABIT_ID, habitId)
            putExtra(ReminderScheduler.KEY_HABIT_NAME, habitName)
            putExtra("notification_id", 99)
            putExtra("snooze_minutes", snoozeMinutes)
        }

        ReminderActionReceiver().onReceive(context, intent)

        assertTrue(repository.toggleCalls.isEmpty())
        assertEquals(1, minuteCalls.size)
        val minuteCall = minuteCalls.first()
        assertEquals(snoozeMinutes, minuteCall.delayMinutes)
        assertEquals(habitId, minuteCall.habitId)
        assertEquals(habitName, minuteCall.habitName)
    }

    private data class MinuteCall(val delayMinutes: Long, val habitId: String?, val habitName: String?)

}
