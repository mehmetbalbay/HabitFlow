package com.habitflow.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitflow.core.database.HabitFlowDatabase
import com.habitflow.core.ui.DateUtils
import java.time.LocalTime

private const val MINUTES_IN_DAY = 24 * 60

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = HabitFlowDatabase.getInstance(applicationContext)
        val dao = database.habitDao()
        val settings = dao.getSettingsOnce()
        if (settings?.remindersEnabled != true) {
            return Result.success()
        }

        val quietDelay = database.routineProfileDao().get()?.let { entity ->
            val startMinutes = parseMinutes(entity.quietStart)
            val endMinutes = parseMinutes(entity.quietEnd)
            if (startMinutes != null && endMinutes != null) {
                val now = LocalTime.now()
                val nowMinutes = now.hour * 60 + now.minute
                if (isWithinQuietHours(nowMinutes, startMinutes, endMinutes)) {
                    minutesUntilQuietEnd(nowMinutes, startMinutes, endMinutes)
                } else null
            } else null
        }

        if (quietDelay != null) {
            val delay = quietDelay.coerceAtLeast(1L)
            ReminderScheduler.scheduleMinuteReminder(applicationContext, delayMinutes = delay)
            return Result.success()
        }

        val todayKey = DateUtils.todayKey()
        val habits = dao.getHabitsWithCompletionsOnce()
        val requestedId = inputData.getString(ReminderScheduler.KEY_HABIT_ID)
        val requestedHabit = requestedId?.let { id ->
            habits.firstOrNull { habitWith ->
                habitWith.habit.id == id && habitWith.completions.none { it.date == todayKey }
            }
        }
        val nextHabit = requestedHabit ?: habits.firstOrNull { habitWith ->
            habitWith.completions.none { it.date == todayKey }
        }

        if (nextHabit != null) {
            ReminderScheduler.showReminder(
                applicationContext,
                habitId = nextHabit.habit.id,
                habitName = nextHabit.habit.name
            )
            ReminderScheduler.scheduleMinuteReminder(applicationContext)
        }
        return Result.success()
    }
}

private fun parseMinutes(hhmm: String?): Int? {
    if (hhmm.isNullOrBlank()) return null
    val parts = hhmm.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    return hour * 60 + minute
}

private fun isWithinQuietHours(now: Int, start: Int, end: Int): Boolean {
    return if (start <= end) {
        now in start until end
    } else {
        now >= start || now < end
    }
}

private fun minutesUntilQuietEnd(now: Int, start: Int, end: Int): Long {
    return if (start <= end) {
        (end - now).coerceAtLeast(1).toLong()
    } else {
        if (now < end) {
            (end - now).coerceAtLeast(1).toLong()
        } else {
            (MINUTES_IN_DAY - now + end).coerceAtLeast(1).toLong()
        }
    }
}
