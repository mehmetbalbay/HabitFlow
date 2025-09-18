package com.habitflow.core.data.di

import com.habitflow.core.data.repository.impl.DefaultHabitRepository
import com.habitflow.core.data.repository.impl.DefaultHydrationRepository
import com.habitflow.core.data.repository.impl.DefaultExerciseRepository
import com.habitflow.core.data.repository.impl.DefaultMealRepository
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.domain.repository.ExerciseRepository
import com.habitflow.domain.repository.MealRepository
import com.habitflow.domain.repository.HydrationRepository
import com.habitflow.domain.repository.RoutineProfileRepository
import com.habitflow.core.data.repository.impl.DefaultRoutineProfileRepository
import com.habitflow.domain.analytics.Analytics
import com.habitflow.core.data.analytics.DefaultAnalytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: DefaultHabitRepository): HabitRepository

    @Binds
    @Singleton
    abstract fun bindHydrationRepository(impl: DefaultHydrationRepository): HydrationRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: DefaultExerciseRepository): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindMealRepository(impl: DefaultMealRepository): MealRepository

    @Binds
    @Singleton
    abstract fun bindRoutineProfileRepository(
        impl: DefaultRoutineProfileRepository
    ): RoutineProfileRepository

    @Binds
    @Singleton
    abstract fun bindAnalytics(impl: DefaultAnalytics): Analytics
}
