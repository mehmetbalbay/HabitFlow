package com.habitflow.reminder

import com.habitflow.domain.analytics.Analytics
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.domain.repository.ExerciseRepository
import com.habitflow.domain.repository.MealRepository
import com.habitflow.domain.repository.HydrationRepository
import com.habitflow.domain.repository.RoutineProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [com.habitflow.core.data.di.RepositoryModule::class]
)
abstract class ReminderActionTestModule {

    @Binds
    abstract fun bindHabitRepository(fake: FakeHabitRepository): HabitRepository

    @Binds
    abstract fun bindExerciseRepository(fake: FakeExerciseRepository): ExerciseRepository

    @Binds
    abstract fun bindMealRepository(fake: FakeMealRepository): MealRepository

    @Binds
    abstract fun bindHydrationRepository(fake: FakeHydrationRepository): HydrationRepository

    @Binds
    abstract fun bindRoutineProfileRepository(fake: FakeRoutineProfileRepository): RoutineProfileRepository

    @Binds
    abstract fun bindAnalytics(fake: FakeAnalytics): Analytics
}
