package com.habitflow.feature.meals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.domain.model.MealLog
import com.habitflow.domain.time.DateProvider
import com.habitflow.domain.usecase.meals.LogMeal
import com.habitflow.domain.usecase.meals.ObserveMealsToday
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MealsViewModel @Inject constructor(
    observeMealsToday: ObserveMealsToday,
    private val logMeal: LogMeal,
    private val dateProvider: DateProvider
) : ViewModel() {
    val todayMeals: StateFlow<List<MealLog>> =
        observeMealsToday().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun quickAdd(mealType: String) {
        logMeal(mealType, dateProvider.today().toString(), note = "quick")
    }
}

