package com.habitflow.core.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_preferences")

class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val onboardingKey = booleanPreferencesKey("onboarding_completed")

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[onboardingKey] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[onboardingKey] = completed
        }
    }
}

