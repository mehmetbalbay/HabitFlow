package com.habitflow.reminder

import com.habitflow.domain.model.ExerciseSession
import com.habitflow.domain.repository.ExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Singleton
class FakeExerciseRepository @Inject constructor() : ExerciseRepository {
    private val flow = MutableStateFlow<List<ExerciseSession>>(emptyList())

    override fun observeWeek(weekPrefix: String): Flow<List<ExerciseSession>> = flow

    override fun log(
        type: String,
        durationMin: Int,
        dateTime: String,
        steps: Int?,
        kcal: Int?,
        note: String?
    ): ExerciseSession {
        val s = ExerciseSession(
            id = "ex-${flow.value.size + 1}",
            type = type,
            durationMin = durationMin,
            steps = steps,
            kcal = kcal,
            note = note,
            dateTime = dateTime
        )
        flow.value = flow.value + s
        return s
    }

    override fun delete(id: String) { flow.value = flow.value.filterNot { it.id == id } }

    override fun onUserChanged(userId: String?) = Unit
}

