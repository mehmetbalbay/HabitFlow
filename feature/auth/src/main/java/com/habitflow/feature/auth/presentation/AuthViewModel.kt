package com.habitflow.feature.auth.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.domain.usecase.OnUserChanged
import com.habitflow.domain.usecase.auth.GetCurrentUserEmail
import com.habitflow.domain.usecase.auth.Login
import com.habitflow.domain.usecase.auth.LoginWithGoogle
import com.habitflow.domain.usecase.auth.ObserveAuthState
import com.habitflow.domain.usecase.auth.Register
import com.habitflow.domain.usecase.auth.SendPasswordReset
import com.habitflow.domain.usecase.auth.SignOut
import com.habitflow.core.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferences: AppPreferences,
    private val loginUseCase: Login,
    private val registerUseCase: Register,
    private val sendPasswordResetUseCase: SendPasswordReset,
    private val loginWithGoogleUseCase: LoginWithGoogle,
    private val signOutUseCase: SignOut,
    private val observeAuthState: ObserveAuthState,
    private val onUserChanged: OnUserChanged,
    private val getCurrentUserEmail: GetCurrentUserEmail
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(observeAuthState(), preferences.onboardingCompleted) { userId, onboarding ->
                onUserChanged(userId)
                userId to onboarding
            }.collect { (userId, onboarding) ->
                _uiState.value = _uiState.value.copy(
                    userId = userId,
                    onboardingCompleted = onboarding,
                    userEmail = getCurrentUserEmail()
                )
            }
        }
    }

    fun markOnboardingSeen() {
        viewModelScope.launch { preferences.setOnboardingCompleted(true) }
    }

    fun signOut() {
        signOutUseCase()
        _uiState.value = _uiState.value.copy(
            userId = null,
            userEmail = null,
            feedback = null
        )
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedback = null)
    }

    fun signInWithGoogle(idToken: String, email: String?) {
        viewModelScope.launch {
            runCatching { loginWithGoogleUseCase(idToken) }
                .onSuccess {
                    showSuccessFeedback(
                        titleRes = com.habitflow.feature.auth.R.string.auth_feedback_google_success_title,
                        messageRes = com.habitflow.feature.auth.R.string.auth_feedback_google_success_body,
                        emailOverride = email
                    )
                }
                .onFailure {
                    setFeedback(mapAuthError(it))
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val emailTrimmed = email.trim()
            val passwordTrimmed = password.trim()
            if (emailTrimmed.isBlank() || passwordTrimmed.isBlank()) {
                setFeedback(
                    AuthFeedback(
                        type = AuthFeedbackType.ERROR,
                        titleRes = com.habitflow.feature.auth.R.string.auth_feedback_input_title,
                        messageRes = com.habitflow.feature.auth.R.string.auth_feedback_input_body
                    )
                )
                return@launch
            }

            runCatching { loginUseCase(emailTrimmed, passwordTrimmed) }
                .onSuccess {
                    showSuccessFeedback(
                        titleRes = com.habitflow.feature.auth.R.string.auth_feedback_login_success_title,
                        messageRes = com.habitflow.feature.auth.R.string.auth_feedback_login_success_body,
                        emailOverride = emailTrimmed
                    )
                }
                .onFailure {
                    setFeedback(mapAuthError(it))
                }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val emailTrimmed = email.trim()
            val passwordTrimmed = password.trim()
            if (emailTrimmed.isBlank() || passwordTrimmed.isBlank()) {
                setFeedback(
                    AuthFeedback(
                        type = AuthFeedbackType.ERROR,
                        titleRes = com.habitflow.feature.auth.R.string.auth_feedback_input_title,
                        messageRes = com.habitflow.feature.auth.R.string.auth_feedback_input_body
                    )
                )
                return@launch
            }

            if (passwordTrimmed.length < 6) {
                setFeedback(
                    AuthFeedback(
                        type = AuthFeedbackType.ERROR,
                        titleRes = com.habitflow.feature.auth.R.string.auth_feedback_weak_password_title,
                        messageRes = com.habitflow.feature.auth.R.string.auth_feedback_weak_password_body
                    )
                )
                return@launch
            }

            runCatching { registerUseCase(emailTrimmed, passwordTrimmed) }
                .onSuccess {
                    showSuccessFeedback(
                        titleRes = com.habitflow.feature.auth.R.string.auth_feedback_register_success_title,
                        messageRes = com.habitflow.feature.auth.R.string.auth_feedback_register_success_body,
                        emailOverride = emailTrimmed
                    )
                }
                .onFailure {
                    setFeedback(mapAuthError(it))
                }
        }
    }

    fun sendReset(email: String) {
        viewModelScope.launch {
            val emailTrimmed = email.trim()
            if (emailTrimmed.isBlank()) {
                setFeedback(
                    AuthFeedback(
                        type = AuthFeedbackType.ERROR,
                        titleRes = com.habitflow.feature.auth.R.string.auth_feedback_input_title,
                        messageRes = com.habitflow.feature.auth.R.string.auth_feedback_input_body
                    )
                )
                return@launch
            }

            runCatching { sendPasswordResetUseCase(emailTrimmed) }
                .onSuccess {
                    setFeedback(
                        AuthFeedback(
                            type = AuthFeedbackType.SUCCESS,
                            titleRes = com.habitflow.feature.auth.R.string.auth_feedback_reset_sent_title,
                            messageRes = com.habitflow.feature.auth.R.string.auth_feedback_reset_sent_body
                        )
                    )
                }
                .onFailure {
                    setFeedback(mapAuthError(it))
                }
        }
    }

    private fun setFeedback(feedback: AuthFeedback?) {
        _uiState.value = _uiState.value.copy(feedback = feedback)
    }

    private fun showSuccessFeedback(
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        emailOverride: String? = null
    ) {
        val trimmedEmail = emailOverride?.takeIf { it.isNotBlank() }
        _uiState.value = _uiState.value.copy(
            userEmail = trimmedEmail ?: getCurrentUserEmail() ?: _uiState.value.userEmail,
            feedback = AuthFeedback(
                type = AuthFeedbackType.SUCCESS,
                titleRes = titleRes,
                messageRes = messageRes
            )
        )
    }

    private fun mapAuthError(error: Throwable): AuthFeedback {
        val mapped = when (val cause = error.unwrap()) {
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_invalid_credentials_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_invalid_credentials_body
            )
            is com.google.firebase.auth.FirebaseAuthInvalidUserException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_user_not_found_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_user_not_found_body
            )
            is com.google.firebase.auth.FirebaseAuthUserCollisionException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_user_collision_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_user_collision_body
            )
            is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_weak_password_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_weak_password_body
            )
            is com.google.firebase.FirebaseNetworkException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_network_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_network_body
            )
            is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_recent_login_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_recent_login_body
            )
            is com.google.firebase.FirebaseTooManyRequestsException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_too_many_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_too_many_body
            )
            is IllegalArgumentException -> AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_input_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_input_body
            )
            else -> null
        }

        return mapped
            ?: AuthFeedback(
                type = AuthFeedbackType.ERROR,
                titleRes = com.habitflow.feature.auth.R.string.auth_feedback_generic_title,
                messageRes = com.habitflow.feature.auth.R.string.auth_feedback_generic_body
            )
    }

    private fun Throwable.unwrap(): Throwable = generateSequence(this) { it.cause }.last()
}

data class AuthUiState(
    val userId: String? = null,
    val onboardingCompleted: Boolean = false,
    val feedback: AuthFeedback? = null,
    val userEmail: String? = null
)

data class AuthFeedback(
    val type: AuthFeedbackType,
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int? = null,
    val messageText: String? = null
)

enum class AuthFeedbackType { ERROR, INFO, SUCCESS }

