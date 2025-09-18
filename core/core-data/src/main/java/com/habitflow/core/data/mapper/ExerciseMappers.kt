package com.habitflow.core.data.mapper

import com.habitflow.core.database.entity.ExerciseSessionEntity
import com.habitflow.domain.model.ExerciseSession

fun ExerciseSessionEntity.toDomain(): ExerciseSession = ExerciseSession(
    id = id,
    type = type,
    durationMin = durationMin,
    steps = steps,
    kcal = kcal,
    source = source,
    note = note,
    dateTime = dateTime
)

fun ExerciseSession.toEntity(): ExerciseSessionEntity = ExerciseSessionEntity(
    id = id,
    type = type,
    durationMin = durationMin,
    steps = steps,
    kcal = kcal,
    source = source,
    note = note,
    dateTime = dateTime
)

