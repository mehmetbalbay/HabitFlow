package com.habitflow.presentation.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.habitflow.feature.auth.R
import com.habitflow.feature.auth.presentation.AuthFeedbackType.ERROR
import com.habitflow.feature.auth.presentation.AuthFeedbackType.SUCCESS
import com.habitflow.core.data.preferences.AppPreferences
import com.habitflow.domain.repository.AuthRepository
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.domain.usecase.OnUserChanged
import com.habitflow.domain.usecase.auth.Login
import com.habitflow.domain.usecase.auth.GetCurrentUserEmail
import com.habitflow.domain.usecase.auth.LoginWithGoogle
import com.habitflow.domain.usecase.auth.ObserveAuthState
import com.habitflow.domain.usecase.auth.Register
import com.habitflow.domain.usecase.auth.SendPasswordReset
import com.habitflow.domain.usecase.auth.SignOut
import com.habitflow.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var authState: MutableStateFlow<String?>
    private lateinit var onboardingState: MutableStateFlow<Boolean>
    private lateinit var authRepository: AuthRepository
    private lateinit var preferences: AppPreferences
    private lateinit var habitRepository: HabitRepository
    private lateinit var viewModel: com.habitflow.feature.auth.presentation.AuthViewModel

    @Before
    fun setUp() {
        authState = MutableStateFlow(null)
        onboardingState = MutableStateFlow(false)
        authRepository = mockk(relaxed = true)
        preferences = mockk(relaxed = true)
        habitRepository = mockk(relaxed = true)

        every { authRepository.authState } returns authState
        every { preferences.onboardingCompleted } returns onboardingState
        coEvery { preferences.setOnboardingCompleted(any()) } returns Unit

        val onUserChangedUseCase = OnUserChanged(habitRepository)
        viewModel = com.habitflow.feature.auth.presentation.AuthViewModel(
            preferences = preferences,
            loginUseCase = Login(authRepository),
            registerUseCase = Register(authRepository),
            sendPasswordResetUseCase = SendPasswordReset(authRepository),
            loginWithGoogleUseCase = LoginWithGoogle(authRepository),
            signOutUseCase = SignOut(authRepository),
            observeAuthState = ObserveAuthState(authRepository),
            onUserChanged = onUserChangedUseCase,
            getCurrentUserEmail = GetCurrentUserEmail(authRepository)
        )
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun `updates user and habit binding when auth state changes`() {
        authState.value = "user-42"
        onboardingState.value = true

        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("user-42", state.userId)
        assertTrue(state.onboardingCompleted)
        verify { habitRepository.onUserChanged("user-42") }
    }

    @Test
    fun `shows input warning when login invoked with empty fields`() {
        viewModel.login(" ", "")
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val feedback = viewModel.uiState.value.feedback
        assertEquals(ERROR, feedback?.type)
        assertEquals(R.string.auth_feedback_input_title, feedback?.titleRes)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `maps firebase credential errors to friendly message`() {
        val exception = mockk<FirebaseAuthInvalidCredentialsException>(relaxed = true)
        every { exception.cause } returns null
        coEvery { authRepository.login(any(), any()) } throws exception

        viewModel.login("test@example.com", "secret123")
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val feedback = viewModel.uiState.value.feedback
        assertEquals(ERROR, feedback?.type)
        assertEquals(R.string.auth_feedback_invalid_credentials_title, feedback?.titleRes)
    }

    @Test
    fun `shows success feedback after password reset`() {
        coEvery { authRepository.sendPasswordReset("user@habitflow.com") } returns Unit

        viewModel.sendReset(" user@habitflow.com ")
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val feedback = viewModel.uiState.value.feedback
        assertEquals(SUCCESS, feedback?.type)
        assertEquals(R.string.auth_feedback_reset_sent_title, feedback?.titleRes)
    }

    @Test
    fun `shows network feedback when sign in fails with network error`() {
        val networkException = mockk<FirebaseNetworkException>(relaxed = true)
        every { networkException.cause } returns null
        coEvery { authRepository.loginWithGoogle(any()) } throws networkException

        viewModel.signInWithGoogle("id-token", null)
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val feedback = viewModel.uiState.value.feedback
        assertEquals(ERROR, feedback?.type)
        assertEquals(R.string.auth_feedback_network_title, feedback?.titleRes)
    }

    @Test
    fun `marks onboarding as completed`() {
        viewModel.markOnboardingSeen()
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferences.setOnboardingCompleted(true) }
        assertTrue(viewModel.uiState.value.onboardingCompleted)
    }

    @Test
    fun `signOut clears user but keeps onboarding flag`() {
        // Simulate user already seeing onboarding
        onboardingState.value = true
        authState.value = "user-42"
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.signOut()

        val state = viewModel.uiState.value
        assertNull(state.userId)
        assertTrue(state.onboardingCompleted)
    }

    @Test
    fun `shows success feedback after successful login`() {
        coEvery { authRepository.login(any(), any()) } returns Unit
        viewModel.login("hello@habitflow.com", "password")
        dispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        
        val feedback = viewModel.uiState.value.feedback
        assertEquals(SUCCESS, feedback?.type)
        assertEquals(R.string.auth_feedback_login_success_title, feedback?.titleRes)
    }
}
