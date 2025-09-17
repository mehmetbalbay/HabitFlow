package com.habitflow.core.data.auth

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.habitflow.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository @Inject constructor(
    @ApplicationContext context: Context
) : AuthRepository {

    private val firebaseApp = FirebaseApp.initializeApp(context)
        ?: FirebaseApp.getApps(context).firstOrNull()
    private val auth = firebaseApp?.let { FirebaseAuth.getInstance(it) }

    val isAvailable: Boolean = auth != null

    override val authState: Flow<String?> = callbackFlow {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuthInstance ->
            trySend(firebaseAuthInstance.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val currentUserEmail: String?
        get() = auth?.currentUser?.email

    override suspend fun register(email: String, password: String) {
        val firebaseAuth = auth ?: return
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun login(email: String, password: String) {
        val firebaseAuth = auth ?: return
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun sendPasswordReset(email: String) {
        val firebaseAuth = auth ?: return
        firebaseAuth.sendPasswordResetEmail(email.trim()).await()
    }

    override suspend fun loginWithGoogle(idToken: String) {
        val firebaseAuth = auth ?: return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    override fun signOut() {
        auth?.signOut()
    }
}
