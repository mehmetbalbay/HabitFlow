package com.habitflow.domain.usecase.onboarding

import com.habitflow.domain.model.RoutineProfile
import com.habitflow.domain.repository.RoutineProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRoutineProfile @Inject constructor(
    private val repository: RoutineProfileRepository
) {
    operator fun invoke(): Flow<RoutineProfile?> = repository.observeProfile()
}

class SaveRoutineProfile @Inject constructor(
    private val repository: RoutineProfileRepository
) {
    suspend operator fun invoke(profile: RoutineProfile) = repository.saveProfile(profile)
}

