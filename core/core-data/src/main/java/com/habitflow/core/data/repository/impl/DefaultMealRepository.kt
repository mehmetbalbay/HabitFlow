package com.habitflow.core.data.repository.impl

import com.habitflow.core.data.di.IoDispatcher
import com.habitflow.core.data.mapper.toDomain
import com.habitflow.core.data.mapper.toEntity
import com.habitflow.core.database.dao.MealDao
import com.habitflow.domain.model.MealLog
import com.habitflow.domain.repository.MealRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Singleton
class DefaultMealRepository @Inject constructor(
    private val mealDao: MealDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val uuidProvider: () -> String,
    private val externalScope: CoroutineScope
) : MealRepository {
    override fun observeDay(dayKey: String): Flow<List<MealLog>> =
        mealDao.observeDay(dayKey).map { it.map { e -> e.toDomain() } }

    override fun log(mealType: String, dateTime: String, note: String?, tags: List<String>, photoUri: String?, calories: Int?, protein: Int?, fat: Int?, carbs: Int?): MealLog {
        val entry = MealLog(
            id = uuidProvider(), mealType = mealType, photoUri = photoUri, note = note, tags = tags,
            calories = calories, protein = protein, fat = fat, carbs = carbs, dateTime = dateTime
        )
        externalScope.launch(ioDispatcher) { mealDao.upsert(entry.toEntity()) }
        return entry
    }

    override fun delete(id: String) { externalScope.launch(ioDispatcher) { mealDao.delete(id) } }
    override fun onUserChanged(userId: String?) { /* optional firestore later */ }
}

