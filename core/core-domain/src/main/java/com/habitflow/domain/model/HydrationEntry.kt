package com.habitflow.domain.model

data class HydrationEntry(
    val id: String,
    val amountMl: Int,
    val dateTime: String,
    val source: String
)

