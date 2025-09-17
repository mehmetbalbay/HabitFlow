package com.habitflow.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.core.ui.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {

    @Inject lateinit var habitRepository: HabitRepository

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra(ReminderScheduler.KEY_HABIT_ID) ?: return
        val habitName = intent.getStringExtra(ReminderScheduler.KEY_HABIT_NAME)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, habitId.hashCode())

        when (intent.action) {
            ACTION_MARK_COMPLETE -> {
                habitRepository.toggleCompletion(habitId, DateUtils.todayKey(), true)
                NotificationManagerCompat.from(context).cancel(notificationId)
                ReminderScheduler.scheduleMinuteReminder(context)
            }
            ACTION_SNOOZE -> {
                val snoozeMinutes = intent.getLongExtra(EXTRA_SNOOZE_MINUTES, DEFAULT_SNOOZE_MINUTES)
                ReminderScheduler.scheduleMinuteReminder(
                    context,
                    delayMinutes = snoozeMinutes,
                    habitId = habitId,
                    habitName = habitName
                )
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
        }
    }

    companion object {
        const val ACTION_MARK_COMPLETE = "com.habitflow.reminder.ACTION_MARK_COMPLETE"
        const val ACTION_SNOOZE = "com.habitflow.reminder.ACTION_SNOOZE"
        private const val EXTRA_NOTIFICATION_ID = "notification_id"
        private const val EXTRA_SNOOZE_MINUTES = "snooze_minutes"
        private const val DEFAULT_SNOOZE_MINUTES = 10L

        private fun baseIntent(context: Context, action: String, habitId: String, habitName: String, notificationId: Int): Intent =
            Intent(context, ReminderActionReceiver::class.java).apply {
                this.action = action
                putExtra(ReminderScheduler.KEY_HABIT_ID, habitId)
                putExtra(ReminderScheduler.KEY_HABIT_NAME, habitName)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }

        fun createCompletePendingIntent(
            context: Context,
            habitId: String,
            habitName: String,
            notificationId: Int
        ): PendingIntent {
            val intent = baseIntent(context, ACTION_MARK_COMPLETE, habitId, habitName, notificationId)
            return PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun createSnoozePendingIntent(
            context: Context,
            habitId: String,
            habitName: String,
            notificationId: Int,
            snoozeMinutes: Long = DEFAULT_SNOOZE_MINUTES
        ): PendingIntent {
            val intent = baseIntent(context, ACTION_SNOOZE, habitId, habitName, notificationId).apply {
                putExtra(EXTRA_SNOOZE_MINUTES, snoozeMinutes)
            }
            return PendingIntent.getBroadcast(
                context,
                notificationId + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}

