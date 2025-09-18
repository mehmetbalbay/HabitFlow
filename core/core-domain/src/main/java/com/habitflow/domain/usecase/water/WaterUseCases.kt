package com.habitflow.domain.usecase.water

import com.habitflow.domain.model.HydrationEntry
import com.habitflow.domain.repository.HydrationRepository
import com.habitflow.domain.time.DateProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWaterToday @Inject constructor(
    private val repository: HydrationRepository,
    private val dateProvider: DateProvider
) {
    operator fun invoke(): Flow<List<HydrationEntry>> =
        repository.observeDay(dateProvider.today().toString())
}

class ObserveWaterDay @Inject constructor(
    private val repository: HydrationRepository
) {
    operator fun invoke(dayKey: String): Flow<List<HydrationEntry>> = repository.observeDay(dayKey)
}

class AddWater @Inject constructor(
    private val repository: HydrationRepository
) {
    operator fun invoke(amountMl: Int, dateTime: String, source: String = "quick"): HydrationEntry =
        repository.add(amountMl, dateTime, source)
}

class OnHydrationUserChanged @Inject constructor(
    private val repository: HydrationRepository
) {
    operator fun invoke(userId: String?) = repository.onUserChanged(userId)
}

