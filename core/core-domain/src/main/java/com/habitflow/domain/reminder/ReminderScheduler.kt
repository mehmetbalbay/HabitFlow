package com.habitflow.domain.reminder

interface ReminderScheduler {
    fun createNotificationChannel()
    fun scheduleDailyReminder()
    fun cancelReminders()
    fun scheduleMinuteReminder(delayMinutes: Long = 1, habitId: String? = null, habitName: String? = null)
}

