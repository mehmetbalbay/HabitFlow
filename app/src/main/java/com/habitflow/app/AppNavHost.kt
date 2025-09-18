package com.habitflow.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.habitflow.feature.auth.presentation.AuthFeedbackType
import com.habitflow.feature.auth.presentation.AuthViewModel
import com.habitflow.feature.auth.ui.ForgotPasswordScreen
import com.habitflow.feature.auth.ui.LoginScreen
import com.habitflow.feature.onboarding.OnboardingDestinations
import com.habitflow.feature.onboarding.onboardingGraph
import com.habitflow.feature.auth.ui.RegisterScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.habitflow.BuildConfig
import com.habitflow.app.navigation.AppRoute
import com.habitflow.feature.exercise.ExerciseCard
import com.habitflow.feature.habit.presentation.HabitViewModel
import com.habitflow.feature.habit.ui.AddHabitScreen
import com.habitflow.feature.habit.ui.HabitFlowScreen
import com.habitflow.feature.insights.InsightsScreen
import com.habitflow.feature.meals.MealsCard
import com.habitflow.feature.profile.ProfileScreen
import com.habitflow.feature.settings.SettingsScreen
import com.habitflow.feature.water.WaterCard
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber

@HiltViewModel
class ReminderViewModel @javax.inject.Inject constructor(
    private val scheduler: com.habitflow.domain.reminder.ReminderScheduler
) : androidx.lifecycle.ViewModel() {
    fun scheduleDaily() = scheduler.scheduleDailyReminder()
    fun scheduleMinute(habitId: String? = null, habitName: String? = null) = scheduler.scheduleMinuteReminder(habitId = habitId, habitName = habitName)
    fun cancelAll() = scheduler.cancelReminders()
}

@HiltViewModel
class HydrationBinderViewModel @javax.inject.Inject constructor(
    private val onHydrationUserChanged: com.habitflow.domain.usecase.water.OnHydrationUserChanged
) : androidx.lifecycle.ViewModel() {
    fun setUser(userId: String?) = onHydrationUserChanged(userId)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    onShowToast: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = hiltViewModel()
    val habitViewModel: HabitViewModel = hiltViewModel()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val habitUiState by habitViewModel.uiState.collectAsStateWithLifecycle()
    val reminderViewModel: ReminderViewModel = hiltViewModel()
    val hydrationBinder: HydrationBinderViewModel = hiltViewModel()

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            habitViewModel.setRemindersEnabled(true)
            reminderViewModel.scheduleDaily()
        } else {
            habitViewModel.setRemindersEnabled(false)
            Toast.makeText(context, com.habitflow.R.string.reminder_permission_rationale, Toast.LENGTH_LONG).show()
        }
    }

    val googleClient = remember(context) {
        val opts = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
            if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()) {
                requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            } else {
                Timber.w("Google sign-in yapılandırılmadı: GOOGLE_WEB_CLIENT_ID boş")
            }
        }.requestEmail().build()
        GoogleSignIn.getClient(context, opts)
    }
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                Timber.i("Google hesabı token döndürdü: email=%s", account.email)
                authViewModel.signInWithGoogle(idToken, account?.email)
            } else {
                Timber.w("Google hesabı için idToken null döndü: email=%s", account?.email)
            }
        } catch (e: ApiException) {
            Timber.e(e, "Google Sign-In başarısız oldu (status=%d)", e.statusCode)
            Toast.makeText(context, e.localizedMessage ?: "Google Sign-In failed", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(authUiState.feedback) {
        val feedback = authUiState.feedback
        if (feedback?.type == AuthFeedbackType.SUCCESS) {
            val message = feedback.messageRes?.let { context.getString(it) } ?: feedback.messageText
            if (!message.isNullOrBlank()) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.clearFeedback()
        }
    }

    LaunchedEffect(authUiState.userId) {
        hydrationBinder.setUser(authUiState.userId)
    }

    val startDestination = when {
        !authUiState.onboardingCompleted -> OnboardingDestinations.ROOT
        authUiState.userId.isNullOrEmpty() -> AppRoute.Login.route
        else -> AppRoute.Home.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        onboardingGraph(navController = navController, onCompleted = {
            authViewModel.markOnboardingSeen()
            navController.navigate(AppRoute.Home.route) {
                popUpTo(OnboardingDestinations.ROOT) { inclusive = true }
                launchSingleTop = true
            }
        }, onLogin = {
            navController.navigate(AppRoute.Login.route) {
                popUpTo(OnboardingDestinations.ROOT) { inclusive = true }
                launchSingleTop = true
            }
        })
        composable(AppRoute.Login.route) {
            LoginScreen(
                uiState = authUiState,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onNavigateRegister = { navController.navigate(AppRoute.Register.route) },
                onNavigateForgot = { navController.navigate(AppRoute.Forgot.route) },
                onGoogleSignIn = {
                    if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isBlank()) {
                        Timber.w("Google Sign-In isteği engellendi: GOOGLE_WEB CLIENT ID boş")
                        Toast.makeText(context, "Google Web Client ID eksik", Toast.LENGTH_LONG).show()
                    } else {
                        Timber.i("Google Sign-In intent başlatılıyor")
                        googleLauncher.launch(googleClient.signInIntent)
                    }
                },
                onDismissFeedback = { authViewModel.clearFeedback() }
            )
        }
        composable(AppRoute.Register.route) {
            RegisterScreen(
                uiState = authUiState,
                onRegister = { email, password -> authViewModel.register(email, password) },
                onNavigateLogin = { navController.popBackStack(AppRoute.Login.route, inclusive = false) },
                onDismissFeedback = { authViewModel.clearFeedback() }
            )
        }
        composable(AppRoute.Forgot.route) {
            ForgotPasswordScreen(
                uiState = authUiState,
                onSend = { email -> authViewModel.sendReset(email) },
                onNavigateBack = { navController.popBackStack() },
                onDismissFeedback = { authViewModel.clearFeedback() }
            )
        }
        composable(AppRoute.Home.route) {
            androidx.compose.material3.Scaffold(
                floatingActionButton = {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = { navController.navigate(AppRoute.Add.route) }
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = androidx.compose.ui.res.stringResource(id = com.habitflow.feature.habit.R.string.new_habit_title)
                        )
                    }
                },
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WaterCard()
                    ExerciseCard()
                    MealsCard()
                    HabitFlowScreen(
                        habits = habitUiState.habits,
                        todayKey = habitUiState.todayKey,
                        remindersEnabled = habitUiState.remindersEnabled,
                        dailyCounts = habitUiState.dailyCounts,
                        weeklyProgress = habitUiState.weeklyProgress,
                        onToggleHabit = { id, done ->
                            habitViewModel.markToday(id, done)
                            if (!done && habitUiState.remindersEnabled) {
                                reminderViewModel.scheduleMinute()
                            }
                        },
                        onDeleteHabit = { id -> habitViewModel.deleteHabit(id) },
                        onToggleReminders = { enabled ->
                            if (enabled) {
                                val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                if (needsPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    habitViewModel.setRemindersEnabled(true)
                                    reminderViewModel.scheduleDaily()
                                }
                            } else {
                                habitViewModel.setRemindersEnabled(false)
                                reminderViewModel.cancelAll()
                            }
                        },
                        onProfileClick = { navController.navigate(AppRoute.Profile.route) }
                    )
                }
            }
        }
        composable(AppRoute.Add.route) {
            AddHabitScreen(
                onSave = { name, type, reminderTime, weeklyDay, customDateTime ->
                    habitViewModel.addHabit(name, type, reminderTime, weeklyDay, customDateTime)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() },
                modifier = Modifier.padding(contentPadding)
            )
        }
        composable(AppRoute.Habits.route) {
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Alışkanlıklar", style = MaterialTheme.typography.headlineSmall)
                HabitFlowScreen(
                    habits = habitUiState.habits,
                    todayKey = habitUiState.todayKey,
                    remindersEnabled = habitUiState.remindersEnabled,
                    dailyCounts = habitUiState.dailyCounts,
                    weeklyProgress = habitUiState.weeklyProgress,
                    onToggleHabit = { id, done -> habitViewModel.markToday(id, done) },
                    onDeleteHabit = { id -> habitViewModel.deleteHabit(id) },
                    onToggleReminders = { enabled -> habitViewModel.setRemindersEnabled(enabled) },
                    onProfileClick = { navController.navigate(AppRoute.Profile.route) }
                )
            }
        }
        composable(AppRoute.Insights.route) {
            InsightsScreen(modifier = Modifier.padding(contentPadding))
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(modifier = Modifier.padding(contentPadding))
        }
        composable(AppRoute.Profile.route) {
            ProfileScreen(
                userEmail = authUiState.userEmail,
                onSignOut = {
                    authViewModel.signOut()
                    val post = if (authUiState.onboardingCompleted) AppRoute.Login.route else AppRoute.Onboarding.route
                    navController.navigate(post) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
