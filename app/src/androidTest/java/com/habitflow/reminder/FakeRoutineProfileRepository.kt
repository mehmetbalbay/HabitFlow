package com.habitflow.reminder

import com.habitflow.domain.model.RoutineProfile
import com.habitflow.domain.repository.RoutineProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Singleton
class FakeRoutineProfileRepository @Inject constructor() : RoutineProfileRepository {
    private val flow = MutableStateFlow<RoutineProfile?>(null)

    override fun observeProfile(): Flow<RoutineProfile?> = flow

    override suspend fun saveProfile(profile: RoutineProfile) {
        flow.value = profile
    }
}

