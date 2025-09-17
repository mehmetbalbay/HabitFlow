package com.habitflow.di

import com.habitflow.domain.usecase.AddHabit
import com.habitflow.domain.usecase.DeleteHabit
import com.habitflow.domain.usecase.HabitUseCases
import com.habitflow.domain.usecase.ObserveHabits
import com.habitflow.domain.usecase.ObserveReminderSetting
import com.habitflow.domain.usecase.OnUserChanged
import com.habitflow.domain.usecase.SetRemindersEnabled
import com.habitflow.domain.usecase.ToggleHabitCompletion
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideHabitUseCases(
        observeHabits: ObserveHabits,
        observeReminderSetting: ObserveReminderSetting,
        addHabit: AddHabit,
        toggleHabitCompletion: ToggleHabitCompletion,
        deleteHabit: DeleteHabit,
        setRemindersEnabled: SetRemindersEnabled,
        onUserChanged: OnUserChanged
    ): HabitUseCases = HabitUseCases(
        observeHabits = observeHabits,
        observeReminderSetting = observeReminderSetting,
        addHabit = addHabit,
        toggleCompletion = toggleHabitCompletion,
        deleteHabit = deleteHabit,
        setRemindersEnabled = setRemindersEnabled,
        onUserChanged = onUserChanged
    )
}
