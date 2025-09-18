package com.habitflow.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.core.data.prefs.OnboardingPreferenceStore
import com.habitflow.domain.model.ExerciseSlot
import com.habitflow.domain.model.MealWindow
import com.habitflow.domain.model.QuietHours
import com.habitflow.domain.model.TimeBlock
import com.habitflow.domain.model.RoutineProfile
import com.habitflow.domain.repository.RoutineProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.habitflow.domain.analytics.Analytics
import com.habitflow.feature.onboarding.TimeUtils
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
    val validation: OnboardingValidation = OnboardingValidation(),
)

data class OnboardingValidation(
    val goalsError: String? = null,
    val sleepError: String? = null,
    val routineError: String? = null,
    val mealsError: String? = null,
    val hydrationError: String? = null,
    val quietHoursError: String? = null,
) {
    val goalsValid get() = goalsError == null
    val sleepValid get() = sleepError == null
    val routineValid get() = routineError == null
    val mealsValid get() = mealsError == null
    val hydrationValid get() = hydrationError == null
    val quietHoursValid get() = quietHoursError == null
    // Goals now required; include in overall validity
    val allValid get() = goalsValid && sleepValid && routineValid && mealsValid && hydrationValid && quietHoursValid

    fun errorCodes(): List<String> = buildList {
        if (!goalsValid) add("goals")
        if (!sleepValid) add("sleep")
        if (!routineValid) add("routine")
        if (!mealsValid) add("meals")
        if (!hydrationValid) add("hydration")
        if (!quietHoursValid) add("quiet_hours")
    }
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repo: RoutineProfileRepository,
    private val prefs: OnboardingPreferenceStore,
    private val analytics: Analytics,
) : ViewModel() {
    private val _ui = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _ui

    init { _ui.value = _ui.value.revalidate() }

    fun onStart() { analytics.track("onboarding_start") }

    fun setWake(v: String) = mutate(
        update = { it.copy(wake = v) },
        analyticsBlock = { state -> analytics.track("sleep_set", mapOf("wake" to state.wake, "sleep" to state.sleep, "night" to state.night)) }
    )

    fun setSleep(v: String) = mutate(
        update = { it.copy(sleep = v) },
        analyticsBlock = { state -> analytics.track("sleep_set", mapOf("wake" to state.wake, "sleep" to state.sleep, "night" to state.night)) }
    )

    fun setNightShift(v: Boolean) = mutate(
        update = { it.copy(night = v) },
        analyticsBlock = { state -> analytics.track("sleep_set", mapOf("wake" to state.wake, "sleep" to state.sleep, "night" to state.night)) }
    )

    fun addWorkBlock(block: TimeBlock) = mutate(
        update = { it.copy(workBlocks = it.workBlocks + block) },
        analyticsBlock = { state -> analytics.track("routine_blocks_set", mapOf("count" to state.workBlocks.size)) }
    )

    fun removeWorkBlock(index: Int) = mutate(
        update = { state ->
            if (index !in state.workBlocks.indices) state
            else state.copy(workBlocks = state.workBlocks.filterIndexed { i, _ -> i != index })
        },
        analyticsBlock = { state -> analytics.track("routine_blocks_set", mapOf("count" to state.workBlocks.size)) }
    )

    fun addAutoWorkBlock() {
        val list = _ui.value.workBlocks
        val newBlock = if (list.isEmpty()) {
            TimeBlock(start = "09:00", end = "18:00", label = "İş")
        } else {
            val last = list.last()
            val start = TimeUtils.plusMinutes(last.end, 0)
            val end = TimeUtils.plusMinutes(start, 60) // +1 saat
            TimeBlock(start = start, end = end, label = last.label)
        }
        addWorkBlock(newBlock)
    }

    fun updateWorkBlockStart(index: Int, time: String) = mutate(update = { state ->
        if (index !in state.workBlocks.indices) state
        else {
            val list = state.workBlocks.toMutableList()
            list[index] = list[index].copy(start = time)
            state.copy(workBlocks = list)
        }
    })

    fun updateWorkBlockEnd(index: Int, time: String) = mutate(update = { state ->
        if (index !in state.workBlocks.indices) state
        else {
            val list = state.workBlocks.toMutableList()
            list[index] = list[index].copy(end = time)
            state.copy(workBlocks = list)
        }
    })

    fun setWorkBlockLabel(index: Int, label: String) = mutate(update = { state ->
        if (index !in state.workBlocks.indices) state
        else {
            val list = state.workBlocks.toMutableList()
            list[index] = list[index].copy(label = label)
            state.copy(workBlocks = list)
        }
    })

    fun setMeals(list: List<MealWindow>) = mutate(
        update = { it.copy(mealWindows = list) },
        analyticsBlock = { state -> analytics.track("meals_set", mapOf("count" to state.mealWindows.size)) }
    )

    fun updateMeal(index: Int, time: String) = mutate(update = { state ->
        if (index !in state.mealWindows.indices) state
        else {
            val list = state.mealWindows.toMutableList()
            list[index] = list[index].copy(time = time)
            state.copy(mealWindows = list)
        }
    })

    fun addSnack() = mutate(
        update = { state ->
            val time = if (state.mealWindows.isNotEmpty()) TimeUtils.plusMinutes(state.mealWindows.last().time, 120) else "16:00"
            state.copy(mealWindows = state.mealWindows + MealWindow("Ara öğün", time))
        },
        analyticsBlock = { state -> analytics.track("meals_set", mapOf("count" to state.mealWindows.size)) }
    )

    fun removeMeal(index: Int) = mutate(
        update = { state ->
            if (index !in state.mealWindows.indices) state
            else state.copy(mealWindows = state.mealWindows.filterIndexed { i, _ -> i != index })
        },
        analyticsBlock = { state -> analytics.track("meals_set", mapOf("count" to state.mealWindows.size)) }
    )

    fun toggleExerciseSlot(slot: ExerciseSlot) = mutate(
        update = { state ->
            val current = state.exerciseSlots
            val next = if (current.any { it.label == slot.label }) current.filter { it.label != slot.label } else current + slot
            state.copy(exerciseSlots = next)
        },
        analyticsBlock = { state -> analytics.track("exercise_set", mapOf("count" to state.exerciseSlots.size)) }
    )

    fun setHydration(value: Int) = mutate(
        update = { it.copy(hydrationGoal = value.coerceIn(1000, 5000)) },
        analyticsBlock = { state -> analytics.track("hydration_goal_set", mapOf("goal" to state.hydrationGoal)) }
    )

    fun setHeightText(v: String) = mutate(update = { it.copy(heightCmText = v) })

    fun setWeightText(v: String) = mutate(update = { it.copy(weightKgText = v) })
    fun suggestedHydration(): Int? {
        val w = _ui.value.weightKgText.toIntOrNull()
        if (w == null) return null
        val ml = (w * 35).coerceIn(1000, 5000)
        return ml
    }
    fun applySuggestedHydration() { suggestedHydration()?.let { setHydration(it) } }
    fun setQuietStart(v: String) = mutate(
        update = { it.copy(quietStart = v) },
        analyticsBlock = { state -> analytics.track("quiet_hours_set", mapOf("start" to state.quietStart, "end" to state.quietEnd)) }
    )

    fun setQuietEnd(v: String) = mutate(
        update = { it.copy(quietEnd = v) },
        analyticsBlock = { state -> analytics.track("quiet_hours_set", mapOf("start" to state.quietStart, "end" to state.quietEnd)) }
    )

    fun toggleGoal(goal: String) = mutate(
        update = { state ->
            val next = if (state.goals.contains(goal)) state.goals - goal else state.goals + goal
            state.copy(goals = next)
        },
        analyticsBlock = { state -> analytics.track("goal_selected", mapOf("values" to state.goals)) }
    )

    fun setOtherGoal(v: String) = mutate(update = { it.copy(otherGoal = v) })

    fun confirm(onDone: () -> Unit) {
        viewModelScope.launch {
            revalidateState()
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
            if (!s.validation.allValid) {
                analytics.track("onboarding_validation_failed", mapOf("errors" to s.validation.errorCodes()))
                return@launch
            }
            repo.saveProfile(profile)
            prefs.setCompleted(true)
            analytics.track("onboarding_complete")
            onDone()
        }
    }

    private fun mutate(
        update: (OnboardingState) -> OnboardingState,
        analyticsBlock: (OnboardingState) -> Unit = {}
    ) {
        val newState = update(_ui.value).revalidate()
        _ui.value = newState
        analyticsBlock(newState)
    }

    private fun revalidateState() {
        _ui.value = _ui.value.revalidate()
    }

    private fun OnboardingState.revalidate(): OnboardingState {
        val goalsError = if (goals.isNotEmpty() || otherGoal.isNotBlank()) null else "En az bir hedef seçmelisin"

        val wakeMinutes = parseMinutes(wake)
        val sleepMinutes = parseMinutes(sleep)
        val sleepError = when {
            night -> null
            wakeMinutes == null || sleepMinutes == null -> "Uyku saatleri geçersiz"
            wakeMinutes >= sleepMinutes -> "Uyanış saati uyku saatinden önce olmalı"
            else -> null
        }

        val routineError = when {
            workBlocks.any { it.label.isBlank() } -> "Blok isimlerini doldur"
            workBlocks.any { timeOrderInvalid(it.start, it.end) } -> "Blok başlangıç ve bitiş saatlerini kontrol et"
            else -> null
        }

        val mealsError = when {
            mealWindows.size < 2 -> "En az iki öğün belirlemelisin"
            mealWindows.any { parseMinutes(it.time) == null } -> "Öğün saatleri geçersiz"
            else -> null
        }

        val hydrationError = if (hydrationGoal in 1000..5000) null else "Su hedefi 1000-5000 ml aralığında olmalı"

        val quietStartMinutes = parseMinutes(quietStart)
        val quietEndMinutes = parseMinutes(quietEnd)
        val quietHoursError = when {
            quietStartMinutes == null -> "Sessiz saat başlangıcı geçersiz"
            quietEndMinutes == null -> "Sessiz saat bitişi geçersiz"
            quietStartMinutes == quietEndMinutes -> "Sessiz saat başlangıç ve bitiş farklı olmalı"
            else -> null
        }

        val validation = OnboardingValidation(
            goalsError = goalsError,
            sleepError = sleepError,
            routineError = routineError,
            mealsError = mealsError,
            hydrationError = hydrationError,
            quietHoursError = quietHoursError
        )

        return copy(validation = validation)
    }

    private fun parseMinutes(value: String?): Int? {
        if (value.isNullOrBlank()) return null
        return runCatching { TimeUtils.parseHm(value) }.getOrNull()
    }

    private fun timeOrderInvalid(start: String, end: String): Boolean {
        val startMinutes = parseMinutes(start)
        val endMinutes = parseMinutes(end)
        return startMinutes == null || endMinutes == null || startMinutes >= endMinutes
    }
}
