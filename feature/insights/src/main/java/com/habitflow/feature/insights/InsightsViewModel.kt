package com.habitflow.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.HydrationEntry
import com.habitflow.domain.time.DateProvider
import com.habitflow.domain.usecase.exercise.ObserveExerciseThisMonth
import com.habitflow.domain.usecase.water.ObserveWaterToday
import com.habitflow.domain.usecase.meals.ObserveMealsToday
import com.habitflow.domain.usecase.ObserveHabits
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InsightsViewModel @Inject constructor(
    observeWaterToday: ObserveWaterToday,
    observeExerciseThisMonth: ObserveExerciseThisMonth,
    observeMealsToday: ObserveMealsToday,
    observeHabits: ObserveHabits,
    private val dateProvider: DateProvider
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = combine(
        observeWaterToday(),
        observeExerciseThisMonth(),
        observeMealsToday(),
        observeHabits()
    ) { water, exercise, meals, habits ->
        mapToState(water, exercise.map { it.durationMin }, meals.size, habits)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InsightsUiState())

    private fun mapToState(
        waterEntries: List<HydrationEntry>,
        exerciseDurations: List<Int>,
        mealCount: Int,
        habits: List<Habit>
    ): InsightsUiState {
        val today = dateProvider.today()
        val last7 = (0..6).map { today.minusDays(it.toLong()).toString() }
        val totalHabits = habits.size
        val completionPerDay = last7.map { day ->
            if (totalHabits == 0) 0f else habits.count { it.history[day] == true }.toFloat() / totalHabits
        }.reversed()
        val compliancePercent = if (completionPerDay.isEmpty()) 0 else ((completionPerDay.last() * 100).toInt())
        val streak = calculateStreak(today, habits)
        val badge = when {
            streak >= 14 -> "Momentum Master"
            compliancePercent >= 80 -> "Consistency Champ"
            else -> "Devam!"
        }
        val waterTotal = waterEntries.sumOf { it.amountMl }
        val exerciseMinutes = exerciseDurations.sum()
        val weeklyAverage = if (completionPerDay.isEmpty()) 0 else (completionPerDay.average() * 100).toInt()
        return InsightsUiState(
            streak = streak,
            weeklyCompliancePercent = weeklyAverage.coerceIn(0, 100),
            waterTodayMl = waterTotal,
            exerciseMinutes = exerciseMinutes,
            mealsToday = mealCount,
            weeklyComplianceSpark = completionPerDay,
            motivationBadge = badge
        )
    }

    private fun calculateStreak(today: java.time.LocalDate, habits: List<Habit>): Int {
        var streak = 0
        var date = today
        while (true) {
            val dayKey = date.toString()
            val completed = habits.any { it.history[dayKey] == true }
            if (completed) {
                streak += 1
                date = date.minusDays(1)
            } else break
        }
        return streak
    }
}

data class InsightsUiState(
    val streak: Int = 0,
    val weeklyCompliancePercent: Int = 0,
    val waterTodayMl: Int = 0,
    val exerciseMinutes: Int = 0,
    val mealsToday: Int = 0,
    val weeklyComplianceSpark: List<Float> = emptyList(),
    val motivationBadge: String = ""
)
