package com.habitflow.feature.home

data class HomeState(
    val greeting: String = "Merhaba HabitFlow!"
)

sealed interface HomeEvent {
    data object OnActionClicked : HomeEvent
}

sealed interface HomeEffect {
    data class Toast(val message: String) : HomeEffect
}

