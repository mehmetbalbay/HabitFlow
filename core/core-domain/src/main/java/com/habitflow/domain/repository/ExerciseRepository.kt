package com.habitflow.domain.repository

import com.habitflow.domain.model.ExerciseSession
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeWeek(weekPrefix: String): Flow<List<ExerciseSession>>
    fun log(type: String, durationMin: Int, dateTime: String, steps: Int? = null, kcal: Int? = null, note: String? = null): ExerciseSession
    fun delete(id: String)
    fun onUserChanged(userId: String?)
}

