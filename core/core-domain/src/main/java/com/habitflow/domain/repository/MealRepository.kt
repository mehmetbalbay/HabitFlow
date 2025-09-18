package com.habitflow.domain.repository

import com.habitflow.domain.model.MealLog
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun observeDay(dayKey: String): Flow<List<MealLog>>
    fun log(mealType: String, dateTime: String, note: String? = null, tags: List<String> = emptyList(), photoUri: String? = null, calories: Int? = null, protein: Int? = null, fat: Int? = null, carbs: Int? = null): MealLog
    fun delete(id: String)
    fun onUserChanged(userId: String?)
}

