package com.habitflow.domain.model

data class MealLog(
    val id: String,
    val mealType: String,
    val photoUri: String? = null,
    val note: String? = null,
    val tags: List<String> = emptyList(),
    val calories: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val carbs: Int? = null,
    val dateTime: String
)

