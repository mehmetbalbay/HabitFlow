package com.habitflow.reminder.di

import com.habitflow.domain.reminder.ReminderScheduler
import com.habitflow.reminder.ReminderSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: ReminderSchedulerImpl): ReminderScheduler
}

