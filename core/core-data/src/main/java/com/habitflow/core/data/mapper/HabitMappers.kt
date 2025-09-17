package com.habitflow.core.data.mapper

import com.habitflow.core.database.entity.HabitCompletionEntity
import com.habitflow.core.database.entity.HabitEntity
import com.habitflow.core.database.entity.HabitWithCompletions
import com.habitflow.domain.model.Habit

fun HabitWithCompletions.toDomain(): Habit = Habit(
    id = habit.id,
    name = habit.name,
    createdAt = habit.createdAt,
    history = completions.associate { it.date to true },
    reminderType = habit.reminderType,
    reminderTime = habit.reminderTime,
    weeklyDay = habit.weeklyDay,
    customDateTime = habit.customDateTime
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    name = name,
    createdAt = createdAt,
    reminderType = reminderType,
    reminderTime = reminderTime,
    weeklyDay = weeklyDay,
    customDateTime = customDateTime
)

fun Habit.toCompletionEntities(): List<HabitCompletionEntity> =
    history.entries.filter { it.value }.map { HabitCompletionEntity(id, it.key) }

