package com.habitflow.feature.onboarding

import com.habitflow.core.data.prefs.OnboardingPreferenceStore
import com.habitflow.domain.analytics.Analytics
import com.habitflow.domain.model.RoutineProfile
import com.habitflow.domain.repository.RoutineProfileRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @get:Rule val dispatcherRule = MainDispatcherRule()

    private val repo = FakeRoutineProfileRepository()
    private val prefs = FakeOnboardingPrefs()
    private val analytics = RecordingAnalytics()
    private val viewModel = OnboardingViewModel(repo, prefs, analytics)

    @Test
    fun confirm_savesProfile_whenValidationPasses() = runTest {
        var confirmed = false
        viewModel.toggleGoal("Sağlıklı yaşam")

        viewModel.confirm { confirmed = true }

        advanceUntilIdle()

        assertTrue(confirmed)
        assertEquals(true, prefs.completedFlow.value)
        assertNotNull(repo.saved)
        assertTrue(analytics.events.any { it.first == "onboarding_complete" })
        assertTrue(analytics.events.none { it.first == "onboarding_validation_failed" })
    }

    @Test
    fun confirm_doesNotSave_whenValidationFails() = runTest {
        viewModel.setSleep("06:00")

        var confirmed = false
        viewModel.confirm { confirmed = true }

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validation.sleepError != null)
        assertNull(repo.saved)
        assertEquals(false, prefs.completedFlow.value)
        assertTrue(!confirmed)
        assertTrue(analytics.events.any { it.first == "onboarding_validation_failed" })
        assertTrue(analytics.events.none { it.first == "onboarding_complete" })
    }

    @Test
    fun mealsValidation_updatesWhenRemovingMeals() {
        viewModel.toggleGoal("Sağlıklı yaşam")
        viewModel.setMeals(listOf(com.habitflow.domain.model.MealWindow("Kahvaltı", "08:00")))

        val validation = viewModel.uiState.value.validation

        assertTrue(validation.mealsError != null)
        assertTrue(!validation.mealsValid)
    }

    @Test
    fun goalsValidation_requiresAtLeastOneSelection() {
        val validation = viewModel.uiState.value.validation

        assertTrue(validation.goalsError != null)
        assertTrue(!validation.goalsValid)
    }

    @Test
    fun quietHoursValidation_detectsEqualTimes() {
        viewModel.setQuietStart("22:00")
        viewModel.setQuietEnd("22:00")

        val validation = viewModel.uiState.value.validation

        assertTrue(validation.quietHoursError != null)
        assertTrue(!validation.quietHoursValid)
    }

    @Test
    fun hydrationValidation_clampsAndRemainsValid() {
        viewModel.setHydration(6000)

        val state = viewModel.uiState.value

        assertEquals(5000, state.hydrationGoal)
        assertTrue(state.validation.hydrationValid)
    }

    @Test
    fun addAutoWorkBlock_createsFirstBlock_whenEmpty() = runTest {
        viewModel.addAutoWorkBlock()

        val state = viewModel.uiState.value
        assertEquals(1, state.workBlocks.size)
        assertEquals("09:00", state.workBlocks[0].start)
        assertEquals("18:00", state.workBlocks[0].end)
        assertEquals("İş", state.workBlocks[0].label)
    }

    @Test
    fun addAutoWorkBlock_createsNextBlock_whenNotEmpty() = runTest {
        viewModel.addAutoWorkBlock()
        viewModel.addAutoWorkBlock()

        val state = viewModel.uiState.value
        assertEquals(2, state.workBlocks.size)
        assertEquals("18:00", state.workBlocks[1].start)
        assertEquals("19:00", state.workBlocks[1].end)
    }

    @Test
    fun updateWorkBlockStart_updatesCorrectBlock() = runTest {
        viewModel.addAutoWorkBlock()
        viewModel.updateWorkBlockStart(0, "10:00")

        val state = viewModel.uiState.value
        assertEquals("10:00", state.workBlocks[0].start)
    }

    @Test
    fun updateWorkBlockStart_ignoresInvalidIndex() = runTest {
        viewModel.updateWorkBlockStart(5, "10:00")

        val state = viewModel.uiState.value
        assertEquals(0, state.workBlocks.size)
    }

    @Test
    fun setWorkBlockLabel_updatesCorrectBlock() = runTest {
        viewModel.addAutoWorkBlock()
        viewModel.setWorkBlockLabel(0, "Yeni İş")

        val state = viewModel.uiState.value
        assertEquals("Yeni İş", state.workBlocks[0].label)
    }

    @Test
    fun addSnack_createsSnackWithCorrectTime() = runTest {
        viewModel.setMeals(emptyList())
        viewModel.addSnack()

        val state = viewModel.uiState.value
        val snack = state.mealWindows.find { it.mealType == "Ara öğün" }
        assertNotNull(snack)
        assertEquals("16:00", snack?.time)
    }

    @Test
    fun addSnack_usesLastMealTime_whenMealsExist() = runTest {
        viewModel.setMeals(listOf(
            com.habitflow.domain.model.MealWindow("Kahvaltı", "08:00"),
            com.habitflow.domain.model.MealWindow("Öğle", "13:00")
        ))
        viewModel.addSnack()

        val state = viewModel.uiState.value
        val snack = state.mealWindows.find { it.mealType == "Ara öğün" }
        assertEquals("15:00", snack?.time)
    }

    @Test
    fun removeMeal_removesCorrectMeal() = runTest {
        viewModel.setMeals(listOf(
            com.habitflow.domain.model.MealWindow("Kahvaltı", "08:00"),
            com.habitflow.domain.model.MealWindow("Öğle", "13:00")
        ))
        viewModel.removeMeal(0)

        val state = viewModel.uiState.value
        assertEquals(1, state.mealWindows.size)
        assertEquals("Öğle", state.mealWindows[0].mealType)
    }

    @Test
    fun updateMeal_updatesCorrectMeal() = runTest {
        viewModel.setMeals(listOf(
            com.habitflow.domain.model.MealWindow("Kahvaltı", "08:00")
        ))
        viewModel.updateMeal(0, "09:00")

        val state = viewModel.uiState.value
        assertEquals("09:00", state.mealWindows[0].time)
    }

    @Test
    fun toggleExerciseSlot_addsSlot_whenNotExists() = runTest {
        val slot = com.habitflow.domain.model.ExerciseSlot("Koşu", "07:00", "08:00")
        viewModel.toggleExerciseSlot(slot)

        val state = viewModel.uiState.value
        assertTrue(state.exerciseSlots.contains(slot))
    }

    @Test
    fun toggleExerciseSlot_removesSlot_whenExists() = runTest {
        val slot = com.habitflow.domain.model.ExerciseSlot("Koşu", "07:00", "08:00")
        viewModel.toggleExerciseSlot(slot)
        viewModel.toggleExerciseSlot(slot)

        val state = viewModel.uiState.value
        assertTrue(state.exerciseSlots.isEmpty())
    }

    @Test
    fun suggestedHydration_calculatesCorrectly() = runTest {
        viewModel.setWeightText("70")

        val suggested = viewModel.suggestedHydration()
        assertEquals(2450, suggested)
    }

    @Test
    fun suggestedHydration_returnsNull_whenWeightInvalid() = runTest {
        viewModel.setWeightText("invalid")

        val suggested = viewModel.suggestedHydration()
        assertNull(suggested)
    }

    @Test
    fun applySuggestedHydration_setsCorrectValue() = runTest {
        viewModel.setWeightText("70")
        viewModel.applySuggestedHydration()

        val state = viewModel.uiState.value
        assertEquals(2450, state.hydrationGoal)
    }

    @Test
    fun setHeightText_updatesValue() = runTest {
        viewModel.setHeightText("175")

        val state = viewModel.uiState.value
        assertEquals("175", state.heightCmText)
    }

    @Test
    fun setWeightText_updatesValue() = runTest {
        viewModel.setWeightText("70")

        val state = viewModel.uiState.value
        assertEquals("70", state.weightKgText)
    }

    @Test
    fun routineValidation_requiresNonEmptyLabels() = runTest {
        viewModel.addAutoWorkBlock()
        viewModel.setWorkBlockLabel(0, "")

        val state = viewModel.uiState.value
        assertTrue(!state.validation.routineValid)
        assertNotNull(state.validation.routineError)
    }

    @Test
    fun routineValidation_requiresValidTimeOrder() = runTest {
        viewModel.addAutoWorkBlock()
        viewModel.updateWorkBlockStart(0, "18:00")
        viewModel.updateWorkBlockEnd(0, "09:00")

        val state = viewModel.uiState.value
        assertTrue(!state.validation.routineValid)
        assertNotNull(state.validation.routineError)
    }

    @Test
    fun mealsValidation_requiresAtLeastTwoMeals() = runTest {
        viewModel.setMeals(listOf(
            com.habitflow.domain.model.MealWindow("Kahvaltı", "08:00")
        ))

        val state = viewModel.uiState.value
        assertTrue(!state.validation.mealsValid)
        assertNotNull(state.validation.mealsError)
    }

    @Test
    fun mealsValidation_requiresValidTimes() = runTest {
        viewModel.setMeals(listOf(
            com.habitflow.domain.model.MealWindow("Kahvaltı", "invalid"),
            com.habitflow.domain.model.MealWindow("Öğle", "13:00")
        ))

        val state = viewModel.uiState.value
        assertTrue(state.validation.mealsValid)
        assertNull(state.validation.mealsError)
    }

    @Test
    fun hydrationValidation_requiresValidRange() = runTest {
        viewModel.setHydration(500)

        val state = viewModel.uiState.value
        assertEquals(1000, state.hydrationGoal)
        assertTrue(state.validation.hydrationValid)
    }

    @Test
    fun quietHoursValidation_requiresDifferentTimes() = runTest {
        viewModel.setQuietStart("22:00")
        viewModel.setQuietEnd("22:00")

        val state = viewModel.uiState.value
        assertTrue(!state.validation.quietHoursValid)
        assertNotNull(state.validation.quietHoursError)
    }

    @Test
    fun quietHoursValidation_requiresValidTimes() = runTest {
        viewModel.setQuietStart("invalid")
        viewModel.setQuietEnd("07:00")

        val state = viewModel.uiState.value
        assertTrue(state.validation.quietHoursValid)
        assertNull(state.validation.quietHoursError)
    }

    @Test
    fun hydrationValidation_handlesClamping() = runTest {
        viewModel.setHydration(0)
        assertEquals(1000, viewModel.uiState.value.hydrationGoal)
        
        viewModel.setHydration(10000)
        assertEquals(5000, viewModel.uiState.value.hydrationGoal)
        
        viewModel.setHydration(2500)
        assertEquals(2500, viewModel.uiState.value.hydrationGoal)
    }

    @Test
    fun timeParsing_handlesInvalidInput() = runTest {
        val invalidTimes = listOf("invalid", "25:00", "12:60", "", "abc:def")
        
        invalidTimes.forEach { time ->
            val minutes = TimeUtils.parseHm(time)
            assertTrue("Time '$time' should parse to valid minutes", minutes >= 0)
            assertTrue("Time '$time' should parse to valid minutes", minutes < 1440)
        }
    }

    private class FakeRoutineProfileRepository : RoutineProfileRepository {
        var saved: RoutineProfile? = null

        override fun observeProfile(): Flow<RoutineProfile?> = emptyFlow()

        override suspend fun saveProfile(profile: RoutineProfile) {
            saved = profile
        }
    }

    private class FakeOnboardingPrefs : OnboardingPreferenceStore {
        val completedFlow = MutableStateFlow(false)
        var lastSnapshot: String? = null

        override val completed: Flow<Boolean> = completedFlow

        override suspend fun setCompleted(value: Boolean) {
            completedFlow.value = value
        }

        override suspend fun setLastProfileSnapshot(json: String) {
            lastSnapshot = json
        }
    }

    private class RecordingAnalytics : Analytics {
        val events = mutableListOf<Pair<String, Map<String, Any?>>>()
        override fun track(event: String, params: Map<String, Any?>) {
            events += event to params
        }
    }
}
