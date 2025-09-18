package com.habitflow.core.data.di

import android.content.Context
import com.habitflow.core.data.preferences.AppPreferences
import com.habitflow.core.data.prefs.OnboardingPreferenceStore
import com.habitflow.core.data.prefs.OnboardingPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences =
        AppPreferences(context)

    @Provides
    @Singleton
    fun provideOnboardingPrefs(@ApplicationContext context: Context): OnboardingPreferenceStore =
        OnboardingPrefs(context)
}
