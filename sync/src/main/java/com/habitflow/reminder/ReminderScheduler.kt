package com.habitflow.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.annotation.VisibleForTesting
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.habitflow.sync.R
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val CHANNEL_ID = "habitflow_reminders"
    private const val WORK_NAME = "HabitReminderWork"
    private const val MINUTE_WORK_NAME = "HabitMinuteReminder"
    private const val DAILY_NOTIFICATION_ID = 1001
    private const val REMINDER_HOUR = 20
    internal const val KEY_HABIT_NAME = "habit_name"
    internal const val KEY_HABIT_ID = "habit_id"

    internal var onShowReminder: ((Context, String?, String?) -> Unit)? = null
    internal var onScheduleMinuteReminder: ((Context, Long, String?, String?) -> Unit)? = null
    internal var notificationsEnabledOverride: Boolean? = null

    @VisibleForTesting
    fun setOnScheduleMinuteReminderForTesting(handler: ((Context, Long, String?, String?) -> Unit)?) {
        onScheduleMinuteReminder = handler
    }

    @VisibleForTesting
    fun setOnShowReminderForTesting(handler: ((Context, String?, String?) -> Unit)?) {
        onShowReminder = handler
    }

    @VisibleForTesting
    fun setNotificationsEnabledOverrideForTesting(value: Boolean?) {
        notificationsEnabledOverride = value
    }

    @VisibleForTesting
    fun keyHabitIdForTesting(): String = KEY_HABIT_ID

    @VisibleForTesting
    fun keyHabitNameForTesting(): String = KEY_HABIT_NAME

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.reminder_channel_description)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun scheduleDailyReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val initialDelay = calculateInitialDelay()
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork(WORK_NAME)
            cancelUniqueWork(MINUTE_WORK_NAME)
        }
    }

    fun scheduleMinuteReminder(
        context: Context,
        delayMinutes: Long = 1,
        habitId: String? = null,
        habitName: String? = null
    ) {
        val dataBuilder = androidx.work.Data.Builder()
        habitId?.let { dataBuilder.putString(KEY_HABIT_ID, it) }
        habitName?.let { dataBuilder.putString(KEY_HABIT_NAME, it) }
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(dataBuilder.build())
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            MINUTE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
        onScheduleMinuteReminder?.invoke(context, delayMinutes, habitId, habitName)
    }

    internal fun showReminder(
        context: Context,
        habitId: String? = null,
        habitName: String? = null
    ) {
        val notificationsEnabled = notificationsEnabledOverride
            ?: NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (!notificationsEnabled) {
            return
        }
        if (notificationsEnabledOverride == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }
        val baseText = habitName?.let {
            context.getString(R.string.notification_content_habit, it)
        } ?: context.getString(R.string.notification_content)
        val contentText: CharSequence = if (habitName != null) {
            val spannable = SpannableString(baseText)
            val start = baseText.indexOf(habitName)
            if (start >= 0) {
                val end = start + habitName.length
                spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val black = ContextCompat.getColor(context, android.R.color.black)
                spannable.setSpan(ForegroundColorSpan(black), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            spannable
        } else {
            baseText
        }
        val notificationId = habitId?.hashCode() ?: DAILY_NOTIFICATION_ID
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        if (habitId != null && habitName != null) {
            val completeAction = NotificationCompat.Action.Builder(
                android.R.drawable.ic_popup_reminder,
                context.getString(R.string.notification_action_complete),
                ReminderActionReceiver.createCompletePendingIntent(
                    context,
                    habitId,
                    habitName,
                    notificationId
                )
            ).build()
            val snoozeAction = NotificationCompat.Action.Builder(
                android.R.drawable.ic_popup_reminder,
                context.getString(R.string.notification_action_snooze),
                ReminderActionReceiver.createSnoozePendingIntent(
                    context,
                    habitId,
                    habitName,
                    notificationId
                )
            ).build()
            builder.addAction(completeAction)
            builder.addAction(snoozeAction)
        }
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        onShowReminder?.invoke(context, habitId, habitName)
    }

    private fun calculateInitialDelay(): Long {
        val now = ZonedDateTime.now()
        var next = now.withHour(REMINDER_HOUR).withMinute(0).withSecond(0).withNano(0)
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        return Duration.between(now, next).toMillis()
    }
}
