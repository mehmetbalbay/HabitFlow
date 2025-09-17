package com.habitflow.reminder

import android.content.Context
import com.habitflow.domain.reminder.ReminderScheduler as ReminderSchedulerPort
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ReminderSchedulerPort {
    override fun createNotificationChannel() {
        ReminderScheduler.createNotificationChannel(appContext)
    }

    override fun scheduleDailyReminder() {
        ReminderScheduler.scheduleDailyReminder(appContext)
    }

    override fun cancelReminders() {
        ReminderScheduler.cancelReminder(appContext)
    }

    override fun scheduleMinuteReminder(delayMinutes: Long, habitId: String?, habitName: String?) {
        ReminderScheduler.scheduleMinuteReminder(appContext, delayMinutes, habitId, habitName)
    }
}

