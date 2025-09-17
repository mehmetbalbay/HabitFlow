package com.habitflow

import android.app.Application
import com.habitflow.domain.reminder.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class HabitFlowApplication : Application() {
    @javax.inject.Inject lateinit var reminderScheduler: ReminderScheduler
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        reminderScheduler.createNotificationChannel()
    }

}
