package com.habitflow.app

import com.habitflow.app.navigation.AppRoute
import com.habitflow.feature.auth.presentation.AuthUiState
import com.habitflow.feature.onboarding.OnboardingDestinations
import org.junit.Assert.assertEquals
import org.junit.Test

class AppNavHostTest {

    @Test
    fun resolveInitialRoute_returnsOnboarding_whenNotCompleted() {
        val state = AuthUiState(onboardingCompleted = false, initialRouteResolved = true)

        val target = resolveInitialRoute(state)

        assertEquals(OnboardingDestinations.ROOT, target)
    }

    @Test
    fun resolveInitialRoute_returnsLogin_whenCompletedWithoutUser() {
        val state = AuthUiState(onboardingCompleted = true, userId = null, initialRouteResolved = true)

        val target = resolveInitialRoute(state)

        assertEquals(AppRoute.Login.route, target)
    }

    @Test
    fun resolveInitialRoute_returnsHome_whenCompletedWithUser() {
        val state = AuthUiState(onboardingCompleted = true, userId = "user-1", initialRouteResolved = true)

        val target = resolveInitialRoute(state)

        assertEquals(AppRoute.Home.route, target)
    }

    @Test
    fun resolvePostOnboardingTarget_returnsLogin_whenUserMissing() {
        val state = AuthUiState(userId = null, onboardingCompleted = false)

        val target = resolvePostOnboardingTarget(state)

        assertEquals(AppRoute.Login.route, target)
    }

    @Test
    fun resolvePostOnboardingTarget_returnsHome_whenUserAvailable() {
        val state = AuthUiState(userId = "42", onboardingCompleted = true)

        val target = resolvePostOnboardingTarget(state)

        assertEquals(AppRoute.Home.route, target)
    }
}
