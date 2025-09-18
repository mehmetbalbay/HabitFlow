package com.habitflow.domain.repository

import com.habitflow.domain.model.RoutineProfile
import kotlinx.coroutines.flow.Flow

interface RoutineProfileRepository {
    fun observeProfile(): Flow<RoutineProfile?>
    suspend fun saveProfile(profile: RoutineProfile)
}

