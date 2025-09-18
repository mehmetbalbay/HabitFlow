package com.habitflow.app

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.habitflow.app.navigation.AppRoute
import com.habitflow.feature.auth.presentation.AuthUiState
import com.habitflow.feature.onboarding.OnboardingDestinations
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashNavigationTest {

    @get:Rule val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun navigatesToOnboarding_whenOnboardingNotCompleted() {
        lateinit var navController: TestNavHostController
        composeRule.setContent {
            val context = LocalContext.current
            navController = TestNavHostController(context).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            TestSplashHost(
                navController = navController,
                state = AuthUiState(initialRouteResolved = true, onboardingCompleted = false)
            )
        }

        composeRule.runOnIdle {
            assertEquals(OnboardingDestinations.ROOT, navController.currentDestination?.route)
        }
    }

    @Test
    fun navigatesToLogin_whenOnboardingCompletedWithoutUser() {
        lateinit var navController: TestNavHostController
        composeRule.setContent {
            val context = LocalContext.current
            navController = TestNavHostController(context).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            TestSplashHost(
                navController = navController,
                state = AuthUiState(initialRouteResolved = true, onboardingCompleted = true, userId = null)
            )
        }

        composeRule.runOnIdle {
            assertEquals(AppRoute.Login.route, navController.currentDestination?.route)
        }
    }

    @Test
    fun navigatesToHome_whenOnboardingCompletedWithUser() {
        lateinit var navController: TestNavHostController
        composeRule.setContent {
            val context = LocalContext.current
            navController = TestNavHostController(context).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            TestSplashHost(
                navController = navController,
                state = AuthUiState(initialRouteResolved = true, onboardingCompleted = true, userId = "user-1")
            )
        }

        composeRule.runOnIdle {
            assertEquals(AppRoute.Home.route, navController.currentDestination?.route)
        }
    }
}

@Composable
private fun TestSplashHost(
    navController: TestNavHostController,
    state: AuthUiState
) {
    NavHost(navController = navController, startDestination = AppRoute.Splash.route) {
        composable(AppRoute.Splash.route) {
            LaunchedEffect(state.initialRouteResolved, state.onboardingCompleted, state.userId) {
                if (!state.initialRouteResolved) return@LaunchedEffect
                val target = resolveInitialRoute(state)
                navController.navigate(target) {
                    popUpTo(AppRoute.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable(OnboardingDestinations.ROOT) { Text("Onboarding") }
        composable(AppRoute.Login.route) { Text("Login") }
        composable(AppRoute.Home.route) { Text("Home") }
    }
}
