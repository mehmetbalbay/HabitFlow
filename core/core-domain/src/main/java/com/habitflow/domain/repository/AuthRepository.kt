package com.habitflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<String?>
    val currentUserEmail: String?
    suspend fun register(email: String, password: String)
    suspend fun login(email: String, password: String)
    suspend fun sendPasswordReset(email: String)
    suspend fun loginWithGoogle(idToken: String)
    fun signOut()
}
