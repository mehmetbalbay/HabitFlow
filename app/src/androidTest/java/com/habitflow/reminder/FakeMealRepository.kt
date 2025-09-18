package com.habitflow.reminder

import com.habitflow.domain.model.MealLog
import com.habitflow.domain.repository.MealRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Singleton
class FakeMealRepository @Inject constructor() : MealRepository {
    private val flow = MutableStateFlow<List<MealLog>>(emptyList())

    override fun observeDay(dayKey: String): Flow<List<MealLog>> = flow

    override fun log(
        mealType: String,
        dateTime: String,
        note: String?,
        tags: List<String>,
        photoUri: String?,
        calories: Int?,
        protein: Int?,
        fat: Int?,
        carbs: Int?
    ): MealLog {
        val log = MealLog(
            id = "meal-${flow.value.size + 1}",
            mealType = mealType,
            photoUri = photoUri,
            note = note,
            tags = tags,
            calories = calories,
            protein = protein,
            fat = fat,
            carbs = carbs,
            dateTime = dateTime
        )
        flow.value = flow.value + log
        return log
    }

    override fun delete(id: String) { flow.value = flow.value.filterNot { it.id == id } }

    override fun onUserChanged(userId: String?) = Unit
}

