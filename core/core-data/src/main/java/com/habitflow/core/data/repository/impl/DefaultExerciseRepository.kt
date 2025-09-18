package com.habitflow.core.data.repository.impl

import com.habitflow.core.data.di.IoDispatcher
import com.habitflow.core.data.mapper.toDomain
import com.habitflow.core.data.mapper.toEntity
import com.habitflow.core.database.dao.ExerciseDao
import com.habitflow.domain.model.ExerciseSession
import com.habitflow.domain.repository.ExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Singleton
class DefaultExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val uuidProvider: () -> String,
    private val externalScope: CoroutineScope
) : ExerciseRepository {
    override fun observeWeek(weekPrefix: String): Flow<List<ExerciseSession>> =
        exerciseDao.observeWeek(weekPrefix).map { list -> list.map { it.toDomain() } }

    override fun log(type: String, durationMin: Int, dateTime: String, steps: Int?, kcal: Int?, note: String?): ExerciseSession {
        val entry = ExerciseSession(
            id = uuidProvider(), type = type, durationMin = durationMin, steps = steps, kcal = kcal, note = note, dateTime = dateTime
        )
        externalScope.launch(ioDispatcher) { exerciseDao.upsert(entry.toEntity()) }
        return entry
    }

    override fun delete(id: String) { externalScope.launch(ioDispatcher) { exerciseDao.delete(id) } }
    override fun onUserChanged(userId: String?) { /* optional firestore later */ }
}

