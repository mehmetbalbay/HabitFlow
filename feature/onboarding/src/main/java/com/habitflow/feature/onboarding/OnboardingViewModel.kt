package com.habitflow.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.core.data.prefs.OnboardingPrefs
import com.habitflow.domain.model.ExerciseSlot
import com.habitflow.domain.model.MealWindow
import com.habitflow.domain.model.QuietHours
import com.habitflow.domain.model.TimeBlock
import com.habitflow.domain.model.RoutineProfile
import com.habitflow.domain.repository.RoutineProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.habitflow.domain.analytics.Analytics
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OnboardingState(
    val wake: String = "07:00",
    val sleep: String = "23:00",
    val night: Boolean = false,
    val workBlocks: List<TimeBlock> = emptyList(),
    val mealWindows: List<MealWindow> = listOf(
        MealWindow("Breakfast", "08:00"),
        MealWindow("Lunch", "12:30"),
        MealWindow("Dinner", "19:30"),
    ),
    val exerciseSlots: List<ExerciseSlot> = emptyList(),
    val hydrationGoal: Int = 2000,
    val quietStart: String = "22:00",
    val quietEnd: String = "07:00",
    val goals: List<String> = emptyList(),
    val otherGoal: String = "",
    val heightCmText: String = "",
    val weightKgText: String = "",
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repo: RoutineProfileRepository,
    private val prefs: OnboardingPrefs,
    private val analytics: Analytics,
) : ViewModel() {
    private val _ui = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _ui

    fun onStart() { analytics.track("onboarding_start") }

    fun setWake(v: String) { _ui.value = _ui.value.copy(wake = v); analytics.track("sleep_set", mapOf("wake" to v, "sleep" to _ui.value.sleep, "night" to _ui.value.night)) }
    fun setSleep(v: String) { _ui.value = _ui.value.copy(sleep = v); analytics.track("sleep_set", mapOf("wake" to _ui.value.wake, "sleep" to v, "night" to _ui.value.night)) }
    fun setNightShift(v: Boolean) { _ui.value = _ui.value.copy(night = v); analytics.track("sleep_set", mapOf("wake" to _ui.value.wake, "sleep" to _ui.value.sleep, "night" to v)) }

    fun addWorkBlock(block: TimeBlock) { _ui.value = _ui.value.copy(workBlocks = _ui.value.workBlocks + block); analytics.track("routine_blocks_set", mapOf("count" to _ui.value.workBlocks.size)) }
    fun removeWorkBlock(index: Int) { _ui.value = _ui.value.copy(workBlocks = _ui.value.workBlocks.filterIndexed { i, _ -> i != index }); analytics.track("routine_blocks_set", mapOf("count" to _ui.value.workBlocks.size)) }
    fun addAutoWorkBlock() {
        val list = _ui.value.workBlocks
        val newBlock = if (list.isEmpty()) {
            TimeBlock(start = "09:00", end = "18:00", label = "İş")
        } else {
            val last = list.last()
            val start = com.habitflow.feature.onboarding.TimeUtils.plusMinutes(last.end, 0)
            val end = com.habitflow.feature.onboarding.TimeUtils.plusMinutes(start, 60) // +1 saat
            TimeBlock(start = start, end = end, label = last.label)
        }
        addWorkBlock(newBlock)
    }
    fun updateWorkBlockStart(index: Int, time: String) {
        val list = _ui.value.workBlocks.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(start = time)
            _ui.value = _ui.value.copy(workBlocks = list)
        }
    }
    fun updateWorkBlockEnd(index: Int, time: String) {
        val list = _ui.value.workBlocks.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(end = time)
            _ui.value = _ui.value.copy(workBlocks = list)
        }
    }
    fun setWorkBlockLabel(index: Int, label: String) {
        val list = _ui.value.workBlocks.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(label = label)
            _ui.value = _ui.value.copy(workBlocks = list)
        }
    }

    fun setMeals(list: List<MealWindow>) { _ui.value = _ui.value.copy(mealWindows = list); analytics.track("meals_set", mapOf("count" to list.size)) }
    fun updateMeal(index: Int, time: String) {
        val cur = _ui.value.mealWindows.toMutableList()
        if (index in cur.indices) {
            cur[index] = cur[index].copy(time = time)
            _ui.value = _ui.value.copy(mealWindows = cur)
        }
    }
    fun addSnack() {
        val cur = _ui.value.mealWindows
        val time = if (cur.isNotEmpty()) com.habitflow.feature.onboarding.TimeUtils.plusMinutes(cur.last().time, 120) else "16:00"
        val next = cur + MealWindow("Ara öğün", time)
        _ui.value = _ui.value.copy(mealWindows = next)
        analytics.track("meals_set", mapOf("count" to next.size))
    }
    fun removeMeal(index: Int) {
        val cur = _ui.value.mealWindows
        if (index in cur.indices) {
            val next = cur.filterIndexed { i, _ -> i != index }
            _ui.value = _ui.value.copy(mealWindows = next)
            analytics.track("meals_set", mapOf("count" to next.size))
        }
    }
    fun toggleExerciseSlot(slot: ExerciseSlot) {
        val cur = _ui.value.exerciseSlots
        val next = if (cur.any { it.label == slot.label }) cur.filter { it.label != slot.label } else cur + slot
        _ui.value = _ui.value.copy(exerciseSlots = next)
        analytics.track("exercise_set", mapOf("count" to next.size))
    }
    fun setHydration(value: Int) { val v = value.coerceIn(1000, 5000); _ui.value = _ui.value.copy(hydrationGoal = v); analytics.track("hydration_goal_set", mapOf("goal" to v)) }
    fun setHeightText(v: String) { _ui.value = _ui.value.copy(heightCmText = v) }
    fun setWeightText(v: String) { _ui.value = _ui.value.copy(weightKgText = v) }
    fun suggestedHydration(): Int? {
        val w = _ui.value.weightKgText.toIntOrNull()
        if (w == null) return null
        val ml = (w * 35).coerceIn(1000, 5000)
        return ml
    }
    fun applySuggestedHydration() { suggestedHydration()?.let { setHydration(it) } }
    fun setQuietStart(v: String) { _ui.value = _ui.value.copy(quietStart = v); analytics.track("quiet_hours_set", mapOf("start" to v, "end" to _ui.value.quietEnd)) }
    fun setQuietEnd(v: String) { _ui.value = _ui.value.copy(quietEnd = v); analytics.track("quiet_hours_set", mapOf("start" to _ui.value.quietStart, "end" to v)) }
    fun toggleGoal(goal: String) {
        val cur = _ui.value.goals
        val next = if (cur.contains(goal)) cur - goal else cur + goal
        _ui.value = _ui.value.copy(goals = next)
        analytics.track("goal_selected", mapOf("values" to next))
    }
    fun setOtherGoal(v: String) { _ui.value = _ui.value.copy(otherGoal = v) }

    private fun isValid(): Boolean {
        val s = _ui.value
        val mealsOk = s.mealWindows.size >= 2
        val hydrationOk = s.hydrationGoal in 1000..5000
        val sleepOk = s.night || s.wake < s.sleep
        return mealsOk && hydrationOk && sleepOk
    }

    fun confirm(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _ui.value
            val profile = RoutineProfile(
                wakeTime = s.wake,
                sleepTime = s.sleep,
                workBlocks = s.workBlocks,
                mealWindows = s.mealWindows,
                exerciseSlots = s.exerciseSlots,
                quietHours = QuietHours(s.quietStart, s.quietEnd),
                hydrationGoal = s.hydrationGoal,
                isNightShift = s.night,
                goals = (s.goals + s.otherGoal.takeIf { it.isNotBlank() }.orEmpty()).filter { it.isNotBlank() }
            )
            analytics.track("preview_viewed")
            if (isValid()) {
                repo.saveProfile(profile)
                prefs.setCompleted(true)
                analytics.track("onboarding_complete")
                onDone()
            }
        }
    }
}
