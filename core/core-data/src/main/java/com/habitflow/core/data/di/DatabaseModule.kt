package com.habitflow.core.data.di

import android.content.Context
import com.habitflow.core.database.HabitFlowDatabase
import com.habitflow.core.database.dao.HabitDao
import com.habitflow.core.database.dao.HydrationDao
import com.habitflow.core.database.dao.ExerciseDao
import com.habitflow.core.database.dao.MealDao
import com.habitflow.core.database.dao.RoutineProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitFlowDatabase =
        HabitFlowDatabase.getInstance(context)

    @Provides
    fun provideHabitDao(database: HabitFlowDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideHydrationDao(database: HabitFlowDatabase): HydrationDao = database.hydrationDao()

    @Provides fun provideExerciseDao(database: HabitFlowDatabase): ExerciseDao = database.exerciseDao()
    @Provides fun provideMealDao(database: HabitFlowDatabase): MealDao = database.mealDao()
    @Provides fun provideRoutineProfileDao(database: HabitFlowDatabase): RoutineProfileDao = database.routineProfileDao()
}
