package net.vpg.apex.core.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object AuthManager {
    private val auth = FirebaseAuth.getInstance()
    val authState: StateFlow<AuthState>
        field = MutableStateFlow<AuthState>(
            auth.currentUser
                ?.let { AuthState.Authenticated(it) }
                ?: AuthState.Unauthenticated
        )

    suspend fun signInWithEmail(email: String, password: String) {
        executeAuthTask("Email Sign In") {
            auth.signInWithEmailAndPassword(email, password)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String) {
        executeAuthTask("Email Sign Up") {
            auth.createUserWithEmailAndPassword(email, password)
        }
    }

    suspend fun signInWithGoogle(idToken: String) {
        executeAuthTask("Google Sign In") {
            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
        }
    }

    private suspend fun executeAuthTask(taskName: String, authTask: suspend () -> Task<AuthResult>) {
        try {
            authState.value = AuthState.Loading
            val user = authTask().await().user
            authState.value = user?.let { AuthState.Authenticated(it) } ?: AuthState.Error("$taskName failed")
        } catch (e: Exception) {
            authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun signOut() {
        auth.signOut()
        authState.value = AuthState.Unauthenticated
    }
}
