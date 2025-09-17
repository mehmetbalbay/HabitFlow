package com.habitflow.reminder

import com.habitflow.domain.repository.HabitRepository
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
}
