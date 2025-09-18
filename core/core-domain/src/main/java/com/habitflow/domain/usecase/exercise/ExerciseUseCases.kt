package com.habitflow.domain.usecase.exercise

import com.habitflow.domain.model.ExerciseSession
import com.habitflow.domain.repository.ExerciseRepository
import com.habitflow.domain.time.DateProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveExerciseThisMonth @Inject constructor(
    private val repository: ExerciseRepository,
    private val dateProvider: DateProvider
) {
    operator fun invoke(): Flow<List<ExerciseSession>> {
        val prefix = dateProvider.today().toString().substring(0, 7) // yyyy-MM
        return repository.observeWeek(prefix)
    }
}

class LogExercise @Inject constructor(
    private val repository: ExerciseRepository
) {
    operator fun invoke(type: String, durationMin: Int, dateTime: String, steps: Int? = null, kcal: Int? = null, note: String? = null): ExerciseSession =
        repository.log(type, durationMin, dateTime, steps, kcal, note)
}

class OnExerciseUserChanged @Inject constructor(
    private val repository: ExerciseRepository
) { operator fun invoke(userId: String?) = repository.onUserChanged(userId) }

