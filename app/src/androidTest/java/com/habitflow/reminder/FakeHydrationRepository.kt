package com.habitflow.reminder

import com.habitflow.domain.model.HydrationEntry
import com.habitflow.domain.repository.HydrationRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Singleton
class FakeHydrationRepository @Inject constructor() : HydrationRepository {
    private val flow = MutableStateFlow<List<HydrationEntry>>(emptyList())

    override fun observeDay(dayKey: String): Flow<List<HydrationEntry>> = flow

    override fun add(amountMl: Int, dateTime: String, source: String): HydrationEntry {
        val e = HydrationEntry(id = "hyd-${flow.value.size + 1}", amountMl = amountMl, dateTime = dateTime, source = source)
        flow.value = flow.value + e
        return e
    }

    override fun delete(id: String) { flow.value = flow.value.filterNot { it.id == id } }

    override fun onUserChanged(userId: String?) = Unit
}

