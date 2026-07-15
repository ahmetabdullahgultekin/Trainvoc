package com.gultekinahmetabdullah.trainvoc.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Firebase Authentication operations.
 * Wraps Firebase Auth SDK and provides coroutine-based API.
 */
@Singleton
class FirebaseAuthRepository @Inject constructor() {

    // Builds without google-services.json (every CI build — the file is
    // gitignored) skip the google-services plugin, so no default FirebaseApp
    // exists at runtime and FirebaseAuth.getInstance() throws. Resolving the
    // handle lazily and tolerating the missing app keeps the whole auth
    // surface inert in those builds instead of crashing Hilt singleton-graph
    // creation at app startup (#103).
    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "Default FirebaseApp is not initialized; Firebase sign-in is disabled", e)
            null
        }
    }

    /**
     * Observes the current Firebase user state.
     * Emits the current user when authentication state changes.
     */
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val auth = firebaseAuth
        if (auth == null) {
            trySend(null)
            awaitClose { }
        } else {
            val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }
    }

    /**
     * Gets the current Firebase user synchronously.
     */
    fun getCurrentUser(): FirebaseUser? = firebaseAuth?.currentUser

    /**
     * Signs in with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return FirebaseAuthResult indicating success or failure
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseAuthResult {
        val auth = firebaseAuth ?: return FirebaseAuthResult.Error(NOT_CONFIGURED_ERROR)
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                FirebaseAuthResult.Success(user)
            } else {
                FirebaseAuthResult.Error("Sign in failed: No user returned")
            }
        } catch (e: FirebaseAuthException) {
            FirebaseAuthResult.Error(mapFirebaseError(e.errorCode))
        } catch (e: Exception) {
            FirebaseAuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    /**
     * Creates a new user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return FirebaseAuthResult indicating success or failure
     */
    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseAuthResult {
        val auth = firebaseAuth ?: return FirebaseAuthResult.Error(NOT_CONFIGURED_ERROR)
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                FirebaseAuthResult.Success(user)
            } else {
                FirebaseAuthResult.Error("Registration failed: No user returned")
            }
        } catch (e: FirebaseAuthException) {
            FirebaseAuthResult.Error(mapFirebaseError(e.errorCode))
        } catch (e: Exception) {
            FirebaseAuthResult.Error(e.message ?: "Registration failed")
        }
    }

    /**
     * Signs in with a Google account using the ID token obtained from the
     * Google Sign-In flow (see [com.google.android.gms.auth.api.signin.GoogleSignIn]).
     *
     * @param idToken The Google ID token returned by `GoogleSignInAccount.idToken`
     * @return FirebaseAuthResult indicating success or failure
     */
    suspend fun signInWithGoogle(idToken: String): FirebaseAuthResult {
        val auth = firebaseAuth ?: return FirebaseAuthResult.Error(NOT_CONFIGURED_ERROR)
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                FirebaseAuthResult.Success(user)
            } else {
                FirebaseAuthResult.Error("Google sign in failed: No user returned")
            }
        } catch (e: FirebaseAuthException) {
            FirebaseAuthResult.Error(mapFirebaseError(e.errorCode))
        } catch (e: Exception) {
            FirebaseAuthResult.Error(e.message ?: "Google sign in failed")
        }
    }

    /**
     * Sends a password reset email.
     *
     * @param email User's email address
     * @return FirebaseAuthResult indicating success or failure
     */
    suspend fun sendPasswordResetEmail(email: String): FirebaseAuthResult {
        val auth = firebaseAuth ?: return FirebaseAuthResult.Error(NOT_CONFIGURED_ERROR)
        return try {
            auth.sendPasswordResetEmail(email).await()
            FirebaseAuthResult.PasswordResetSent
        } catch (e: FirebaseAuthException) {
            FirebaseAuthResult.Error(mapFirebaseError(e.errorCode))
        } catch (e: Exception) {
            FirebaseAuthResult.Error(e.message ?: "Failed to send reset email")
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        firebaseAuth?.signOut()
    }

    /**
     * Gets the current user's ID token for backend API calls.
     *
     * @param forceRefresh If true, forces a token refresh
     * @return The ID token, or null if not authenticated
     */
    suspend fun getIdToken(forceRefresh: Boolean = false): String? {
        return try {
            firebaseAuth?.currentUser?.getIdToken(forceRefresh)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Reloads the current user's data from Firebase.
     */
    suspend fun reloadUser(): FirebaseAuthResult {
        val auth = firebaseAuth ?: return FirebaseAuthResult.Error(NOT_CONFIGURED_ERROR)
        return try {
            auth.currentUser?.reload()?.await()
            val user = auth.currentUser
            if (user != null) {
                FirebaseAuthResult.Success(user)
            } else {
                FirebaseAuthResult.Error("No user signed in")
            }
        } catch (e: Exception) {
            FirebaseAuthResult.Error(e.message ?: "Failed to reload user")
        }
    }

    /**
     * Sends email verification to the current user.
     */
    suspend fun sendEmailVerification(): FirebaseAuthResult {
        val auth = firebaseAuth ?: return FirebaseAuthResult.Error(NOT_CONFIGURED_ERROR)
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            FirebaseAuthResult.EmailVerificationSent
        } catch (e: Exception) {
            FirebaseAuthResult.Error(e.message ?: "Failed to send verification email")
        }
    }

    /**
     * Updates the user's display name.
     *
     * @param displayName New display name
     */
    suspend fun updateDisplayName(displayName: String): FirebaseAuthResult {
        val auth = firebaseAuth ?: return FirebaseAuthResult.Error(NOT_CONFIGURED_ERROR)
        return try {
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            FirebaseAuthResult.Success(auth.currentUser!!)
        } catch (e: Exception) {
            FirebaseAuthResult.Error(e.message ?: "Failed to update display name")
        }
    }

    /**
     * Maps Firebase error codes to user-friendly messages.
     */
    private fun mapFirebaseError(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later"
            "ERROR_OPERATION_NOT_ALLOWED" -> "Email/password sign-in is not enabled"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters"
            "ERROR_INVALID_CREDENTIAL" -> "Invalid email or password"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection"
            else -> "Authentication failed: $errorCode"
        }
    }

    private companion object {
        const val TAG = "FirebaseAuthRepository"
        const val NOT_CONFIGURED_ERROR =
            "Firebase authentication is not available in this build"
    }
}

/**
 * Result of Firebase authentication operations.
 */
sealed class FirebaseAuthResult {
    data class Success(val user: FirebaseUser) : FirebaseAuthResult()
    data class Error(val message: String) : FirebaseAuthResult()
    data object PasswordResetSent : FirebaseAuthResult()
    data object EmailVerificationSent : FirebaseAuthResult()
}
