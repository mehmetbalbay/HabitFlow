package com.habitflow.domain.usecase.auth

import com.habitflow.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAuthState @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<String?> = repository.authState
}

class GetCurrentUserEmail @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): String? = repository.currentUserEmail
}

class Login @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        repository.login(email, password)
    }
}

class Register @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        repository.register(email, password)
    }
}

class SendPasswordReset @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) {
        repository.sendPasswordReset(email)
    }
}

class LoginWithGoogle @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String) {
        repository.loginWithGoogle(idToken)
    }
}

class SignOut @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() {
        repository.signOut()
    }
}
