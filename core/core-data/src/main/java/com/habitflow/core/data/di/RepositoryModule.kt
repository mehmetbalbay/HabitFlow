package com.habitflow.core.data.di

import com.habitflow.core.data.repository.impl.DefaultHabitRepository
import com.habitflow.domain.repository.HabitRepository
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
}
