package com.habitflow.domain.usecase.meals

import com.habitflow.domain.model.MealLog
import com.habitflow.domain.repository.MealRepository
import com.habitflow.domain.time.DateProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveMealsToday @Inject constructor(
    private val repository: MealRepository,
    private val dateProvider: DateProvider
) {
    operator fun invoke(): Flow<List<MealLog>> = repository.observeDay(dateProvider.today().toString())
}

class LogMeal @Inject constructor(
    private val repository: MealRepository
) {
    operator fun invoke(mealType: String, dateTime: String, note: String? = null, tags: List<String> = emptyList(), photoUri: String? = null, calories: Int? = null, protein: Int? = null, fat: Int? = null, carbs: Int? = null): MealLog =
        repository.log(mealType, dateTime, note, tags, photoUri, calories, protein, fat, carbs)
}

class OnMealsUserChanged @Inject constructor(
    private val repository: MealRepository
) { operator fun invoke(userId: String?) = repository.onUserChanged(userId) }

