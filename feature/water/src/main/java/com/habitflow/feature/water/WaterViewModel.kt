package com.habitflow.feature.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.domain.model.HydrationEntry
import com.habitflow.domain.time.DateProvider
import com.habitflow.domain.usecase.water.AddWater
import com.habitflow.domain.usecase.water.ObserveWaterToday
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class WaterViewModel @Inject constructor(
    observeToday: ObserveWaterToday,
    private val addWater: AddWater,
    private val dateProvider: com.habitflow.domain.time.DateProvider
) : ViewModel() {

    val todayEntries: StateFlow<List<HydrationEntry>> =
        observeToday().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val dailyGoalMl: Int = 2000 // TODO: pull from UserPrefs

    fun quickAdd(amount: Int) {
        addWater(amount, dateProvider.today().toString(), source = "quick")
    }
}
