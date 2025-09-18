package com.habitflow.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.lifecycle.compose.collectAsStateWithLifecycle

object OnboardingDestinations {
    const val ROOT = "onboarding"
    const val WELCOME = "onb_welcome"
    const val GOALS = "onb_goals"
    const val SLEEP = "onb_sleep"
    const val ROUTINE = "onb_routine"
    const val MEALS = "onb_meals"
    const val EXERCISE = "onb_exercise"
    const val HYDRATION = "onb_hydration"
    const val QUIET = "onb_quiet"
    const val PREVIEW = "onb_preview"
}

fun NavGraphBuilder.onboardingGraph(
    navController: NavHostController,
    onCompleted: () -> Unit,
    onLogin: () -> Unit,
) {
    navigation(startDestination = OnboardingDestinations.WELCOME, route = OnboardingDestinations.ROOT) {
        composable(OnboardingDestinations.WELCOME) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            WelcomeScreen(onNext = {
                vm.onStart()
                navController.navigate(OnboardingDestinations.GOALS)
            }, onLogin = onLogin)
        }
        composable(OnboardingDestinations.GOALS) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            GoalsScreen(
                selected = state.goals,
                other = state.otherGoal,
                onToggle = vm::toggleGoal,
                onOtherChanged = vm::setOtherGoal,
                validation = state.validation,
                onNext = { navController.navigate(OnboardingDestinations.SLEEP) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(OnboardingDestinations.SLEEP) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            SleepScreen(
                state = state,
                onWakeChange = vm::setWake,
                onSleepChange = vm::setSleep,
                onNightShiftToggle = vm::setNightShift,
                validation = state.validation,
                onNext = { navController.navigate(OnboardingDestinations.ROUTINE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(OnboardingDestinations.ROUTINE) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            RoutineBlocksScreen(
                blocks = state.workBlocks,
                onAdd = vm::addWorkBlock,
                onAddAuto = vm::addAutoWorkBlock,
                onRemove = vm::removeWorkBlock,
                onStartMinus = { idx ->
                    state.workBlocks.getOrNull(idx)?.let { block ->
                        vm.updateWorkBlockStart(idx, TimeUtils.plusMinutes(block.start, -30))
                    }
                },
                onStartPlus = { idx ->
                    state.workBlocks.getOrNull(idx)?.let { block ->
                        vm.updateWorkBlockStart(idx, TimeUtils.plusMinutes(block.start, +30))
                    }
                },
                onEndMinus = { idx ->
                    state.workBlocks.getOrNull(idx)?.let { block ->
                        vm.updateWorkBlockEnd(idx, TimeUtils.plusMinutes(block.end, -30))
                    }
                },
                onEndPlus = { idx ->
                    state.workBlocks.getOrNull(idx)?.let { block ->
                        vm.updateWorkBlockEnd(idx, TimeUtils.plusMinutes(block.end, +30))
                    }
                },
                onLabelChange = vm::setWorkBlockLabel,
                validation = state.validation,
                onNext = { navController.navigate(OnboardingDestinations.MEALS) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(OnboardingDestinations.MEALS) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            MealsScreen(
                meals = state.mealWindows,
                onSet = vm::setMeals,
                onUpdate = vm::updateMeal,
                onAddSnack = vm::addSnack,
                onRemoveAt = vm::removeMeal,
                validation = state.validation,
                onNext = { navController.navigate(OnboardingDestinations.EXERCISE) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(OnboardingDestinations.EXERCISE) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            ExerciseScreen(
                slots = state.exerciseSlots,
                onToggle = vm::toggleExerciseSlot,
                onNext = { navController.navigate(OnboardingDestinations.HYDRATION) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(OnboardingDestinations.HYDRATION) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            HydrationScreen(
                value = state.hydrationGoal,
                onChange = vm::setHydration,
                heightText = state.heightCmText,
                weightText = state.weightKgText,
                onHeightChange = vm::setHeightText,
                onWeightChange = vm::setWeightText,
                suggestion = vm.suggestedHydration(),
                onApplySuggestion = vm::applySuggestedHydration,
                validation = state.validation,
                onNext = { navController.navigate(OnboardingDestinations.QUIET) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(OnboardingDestinations.QUIET) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            QuietHoursScreen(
                start = state.quietStart,
                end = state.quietEnd,
                onStartChange = vm::setQuietStart,
                onEndChange = vm::setQuietEnd,
                validation = state.validation,
                onNext = { navController.navigate(OnboardingDestinations.PREVIEW) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(OnboardingDestinations.PREVIEW) { backStackEntry ->
            val vm = sharedOnboardingViewModel(navController, backStackEntry)
            val state = vm.uiState.collectAsStateWithLifecycle().value
            PreviewScreen(
                state = state,
                onConfirm = {
                    vm.confirm(onCompleted)
                },
                onNavigateToStep = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun sharedOnboardingViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry
): OnboardingViewModel {
    val parentEntry = remember(backStackEntry) {
        navController.getBackStackEntry(OnboardingDestinations.ROOT)
    }
    return hiltViewModel(parentEntry)
}
