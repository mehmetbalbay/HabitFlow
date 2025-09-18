package com.habitflow.domain.repository

import com.habitflow.domain.model.HydrationEntry
import kotlinx.coroutines.flow.Flow

interface HydrationRepository {
    fun observeDay(dayKey: String): Flow<List<HydrationEntry>>
    fun add(amountMl: Int, dateTime: String, source: String): HydrationEntry
    fun delete(id: String)
    fun onUserChanged(userId: String?)
}

