package com.habitflow.core.data.mapper

import com.habitflow.core.database.entity.MealLogEntity
import com.habitflow.domain.model.MealLog

private fun List<String>.toCsv(): String? = if (isEmpty()) null else joinToString(",")
private fun String?.fromCsv(): List<String> = this?.split(',')?.filter { it.isNotBlank() } ?: emptyList()

fun MealLogEntity.toDomain(): MealLog = MealLog(
    id = id,
    mealType = mealType,
    photoUri = photoUri,
    note = note,
    tags = tagsCsv.fromCsv(),
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs,
    dateTime = dateTime
)

fun MealLog.toEntity(): MealLogEntity = MealLogEntity(
    id = id,
    mealType = mealType,
    photoUri = photoUri,
    note = note,
    tagsCsv = tags.toCsv(),
    calories = calories,
    protein = protein,
    fat = fat,
    carbs = carbs,
    dateTime = dateTime
)

