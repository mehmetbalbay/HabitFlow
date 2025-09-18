package com.habitflow.feature.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.domain.model.ExerciseSession
import com.habitflow.domain.time.DateProvider
import com.habitflow.domain.usecase.exercise.LogExercise
import com.habitflow.domain.usecase.exercise.ObserveExerciseThisMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    observeThisMonth: ObserveExerciseThisMonth,
    private val logExercise: LogExercise,
    private val dateProvider: DateProvider
) : ViewModel() {
    val sessions: StateFlow<List<ExerciseSession>> =
        observeThisMonth().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun quickLog(type: String = "Yürüyüş", durationMin: Int = 20) {
        logExercise(type, durationMin, dateProvider.today().toString(), note = "quick")
    }
}

