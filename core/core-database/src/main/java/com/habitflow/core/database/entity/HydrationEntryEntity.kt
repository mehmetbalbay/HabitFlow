package com.habitflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hydration_entries")
data class HydrationEntryEntity(
    @PrimaryKey val id: String,
    val amountMl: Int,
    val dateTime: String,
    val source: String
)

