package com.habitflow.core.data.mapper

import com.habitflow.core.database.entity.HydrationEntryEntity
import com.habitflow.domain.model.HydrationEntry

fun HydrationEntryEntity.toDomain(): HydrationEntry =
    HydrationEntry(id = id, amountMl = amountMl, dateTime = dateTime, source = source)

fun HydrationEntry.toEntity(): HydrationEntryEntity =
    HydrationEntryEntity(id = id, amountMl = amountMl, dateTime = dateTime, source = source)

