package com.habitflow.app.navigation

sealed class AppRoute(val route: String) {
    data object Onboarding : AppRoute("onboarding")
    data object Login : AppRoute("login")
    data object Register : AppRoute("register")
    data object Forgot : AppRoute("forgot")
    data object Home : AppRoute("home")
    data object Add : AppRoute("add")
    data object Profile : AppRoute("profile")
}

