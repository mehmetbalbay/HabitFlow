package com.habitflow.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habitflow.core.database.HabitFlowDatabase
import com.habitflow.core.ui.DateUtils

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val dao = HabitFlowDatabase.getInstance(applicationContext).habitDao()
        val settings = dao.getSettingsOnce()
        if (settings?.remindersEnabled != true) {
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

