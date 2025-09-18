package com.habitflow.core.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "onboarding_prefs")

object OnboardingKeys {
    val COMPLETED = booleanPreferencesKey("onboarding_completed")
    val LAST_PROFILE_SNAPSHOT = stringPreferencesKey("last_profile_snapshot_json")
}

class OnboardingPrefs(private val context: Context) {
    val completed: Flow<Boolean> = context.dataStore.data.map { it[OnboardingKeys.COMPLETED] ?: false }

    suspend fun setCompleted(value: Boolean) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[OnboardingKeys.COMPLETED] = value
        }
    }

    suspend fun setLastProfileSnapshot(json: String) {
        context.dataStore.edit { prefs -> prefs[OnboardingKeys.LAST_PROFILE_SNAPSHOT] = json }
    }
}
