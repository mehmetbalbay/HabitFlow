package com.habitflow.core.data.di

import android.content.Context
import com.habitflow.core.data.source.remote.FirebaseSyncManager
import com.habitflow.core.data.source.remote.HabitRemoteSync
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideHabitRemoteSync(@ApplicationContext context: Context): HabitRemoteSync =
        FirebaseSyncManager(context)
}
