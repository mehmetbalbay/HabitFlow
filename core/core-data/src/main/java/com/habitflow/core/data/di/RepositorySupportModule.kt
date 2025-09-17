package com.habitflow.core.data.di

import androidx.room.withTransaction
import com.habitflow.core.database.HabitFlowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.habitflow.domain.time.DateProvider
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus

@Module
@InstallIn(SingletonComponent::class)
object RepositorySupportModule {

    @Provides
    @Singleton
    fun provideDateProvider(): DateProvider = object : DateProvider {
        override fun today() = java.time.LocalDate.now()
    }

    @Provides
    @Singleton
    fun provideUuidProvider(): () -> String = { java.util.UUID.randomUUID().toString() }

    @Provides
    @Singleton
    fun provideExternalScope(@IoDispatcher ioDispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob()) + ioDispatcher

    @Provides
    @Singleton
    @RoomTransactionRunner
    fun provideTransactionRunner(database: HabitFlowDatabase): @JvmSuppressWildcards suspend (suspend () -> Unit) -> Unit = { block ->
        database.withTransaction { block() }
    }
}
