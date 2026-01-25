package com.gultekinahmetabdullah.trainvoc.auth

import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations.
 * Uses Firebase as the primary authentication provider,
 * with backend sync for user data.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val authApiService: AuthApiService,
    private val preferencesRepository: IPreferencesRepository
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    /**
     * Check if user is currently authenticated via Firebase.
     */
    suspend fun checkAuthState() = withContext(Dispatchers.IO) {
        val firebaseUser = firebaseAuthRepository.getCurrentUser()

        if (firebaseUser == null) {
            _authState.value = AuthState.NotAuthenticated
            _currentUser.value = null
            return@withContext
        }

        // User is authenticated with Firebase
        _authState.value = AuthState.Authenticated

        // Try to sync with backend
        try {
            syncWithBackend()
        } catch (e: Exception) {
            // Backend sync failed, but user is still authenticated with Firebase
            // Keep authenticated state but note we're offline from backend
            _authState.value = AuthState.AuthenticatedOffline
        }
    }

    /**
     * Login with email and password using Firebase.
     */
    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Loading

        when (val result = firebaseAuthRepository.signInWithEmailAndPassword(email, password)) {
            is FirebaseAuthResult.Success -> {
                _authState.value = AuthState.Authenticated

                // Try to sync with backend
                try {
                    syncWithBackend()
                } catch (e: Exception) {
                    // Backend sync failed, but login succeeded
                    _authState.value = AuthState.AuthenticatedOffline
                }

                AuthResult.Success(_currentUser.value)
            }
            is FirebaseAuthResult.Error -> {
                _authState.value = AuthState.NotAuthenticated
                AuthResult.Error(result.message)
            }
            else -> {
                _authState.value = AuthState.NotAuthenticated
                AuthResult.Error("Unexpected error during login")
            }
        }
    }

    /**
     * Register a new user with email and password using Firebase.
     * Note: Username is optional and will be synced to backend.
     */
    suspend fun register(
        email: String,
        password: String,
        displayName: String? = null
    ): AuthResult = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Loading

        when (val result = firebaseAuthRepository.createUserWithEmailAndPassword(email, password)) {
            is FirebaseAuthResult.Success -> {
                // Update display name if provided
                if (!displayName.isNullOrBlank()) {
                    firebaseAuthRepository.updateDisplayName(displayName)
                }

                _authState.value = AuthState.Authenticated

                // Try to sync with backend
                try {
                    syncWithBackend()
                } catch (e: Exception) {
                    // Backend sync failed, but registration succeeded
                    _authState.value = AuthState.AuthenticatedOffline
                }

                AuthResult.Success(_currentUser.value)
            }
            is FirebaseAuthResult.Error -> {
                _authState.value = AuthState.NotAuthenticated
                AuthResult.Error(result.message)
            }
            else -> {
                _authState.value = AuthState.NotAuthenticated
                AuthResult.Error("Unexpected error during registration")
            }
        }
    }

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult = withContext(Dispatchers.IO) {
        when (val result = firebaseAuthRepository.sendPasswordResetEmail(email)) {
            is FirebaseAuthResult.PasswordResetSent -> {
                AuthResult.PasswordResetSent
            }
            is FirebaseAuthResult.Error -> {
                AuthResult.Error(result.message)
            }
            else -> {
                AuthResult.Error("Unexpected error sending reset email")
            }
        }
    }

    /**
     * Logout and clear session.
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        firebaseAuthRepository.signOut()
        clearLocalData()
        _currentUser.value = null
        _authState.value = AuthState.NotAuthenticated
    }

    /**
     * Get the Firebase ID token for backend API calls.
     */
    suspend fun getFirebaseIdToken(): String? = withContext(Dispatchers.IO) {
        firebaseAuthRepository.getIdToken(forceRefresh = false)
    }

    /**
     * Syncs the Firebase user with the backend.
     * Creates or updates the user in the backend database.
     */
    private suspend fun syncWithBackend() {
        val token = firebaseAuthRepository.getIdToken() ?: return

        try {
            val response = authApiService.firebaseSync("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val syncResponse = response.body()!!
                _currentUser.value = UserInfo(
                    id = syncResponse.id,
                    username = syncResponse.username,
                    email = syncResponse.email,
                    createdAt = null
                )

                // Save username locally for offline access
                preferencesRepository.setUsername(syncResponse.username)
            }
        } catch (e: Exception) {
            // Log error but don't fail - user is still authenticated with Firebase
            throw e
        }
    }

    /**
     * Clears local authentication data.
     */
    private fun clearLocalData() {
        preferencesRepository.clearAuthToken()
        preferencesRepository.clearRefreshToken()
    }

    /**
     * Gets the current Firebase user's email.
     */
    fun getCurrentEmail(): String? {
        return firebaseAuthRepository.getCurrentUser()?.email
    }

    /**
     * Gets the current Firebase user's display name.
     */
    fun getDisplayName(): String? {
        return firebaseAuthRepository.getCurrentUser()?.displayName
    }

    /**
     * Checks if the current user's email is verified.
     */
    fun isEmailVerified(): Boolean {
        return firebaseAuthRepository.getCurrentUser()?.isEmailVerified ?: false
    }

    /**
     * Sends email verification to current user.
     */
    suspend fun sendEmailVerification(): AuthResult = withContext(Dispatchers.IO) {
        when (val result = firebaseAuthRepository.sendEmailVerification()) {
            is FirebaseAuthResult.EmailVerificationSent -> {
                AuthResult.EmailVerificationSent
            }
            is FirebaseAuthResult.Error -> {
                AuthResult.Error(result.message)
            }
            else -> {
                AuthResult.Error("Unexpected error sending verification email")
            }
        }
    }
}

/**
 * Authentication state.
 */
sealed class AuthState {
    data object Unknown : AuthState()
    data object Loading : AuthState()
    data object Authenticated : AuthState()
    data object AuthenticatedOffline : AuthState()
    data object NotAuthenticated : AuthState()
}

/**
 * Result of authentication operations.
 */
sealed class AuthResult {
    data class Success(val user: UserInfo?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data object PasswordResetSent : AuthResult()
    data object EmailVerificationSent : AuthResult()
}
