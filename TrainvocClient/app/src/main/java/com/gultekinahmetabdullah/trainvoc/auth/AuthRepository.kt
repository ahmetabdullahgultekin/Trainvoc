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
 * Handles login, registration, token management, and auth state.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val preferencesRepository: IPreferencesRepository
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    /**
     * Check if user is currently authenticated.
     * Validates stored token with the server.
     */
    suspend fun checkAuthState() = withContext(Dispatchers.IO) {
        val token = preferencesRepository.getAuthToken()
        if (token == null) {
            _authState.value = AuthState.NotAuthenticated
            return@withContext
        }

        try {
            val response = authApiService.validateToken("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                _currentUser.value = response.body()
                _authState.value = AuthState.Authenticated
            } else {
                // Token invalid, try to refresh
                val refreshResult = refreshToken()
                if (!refreshResult) {
                    clearTokens()
                    _authState.value = AuthState.NotAuthenticated
                }
            }
        } catch (e: Exception) {
            // Network error - keep current state but mark as offline
            _authState.value = if (token.isNotEmpty()) {
                AuthState.AuthenticatedOffline
            } else {
                AuthState.NotAuthenticated
            }
        }
    }

    /**
     * Login with username and password.
     */
    suspend fun login(username: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            _authState.value = AuthState.Loading

            val response = authApiService.login(
                LoginRequest(
                    username = username,
                    password = password,
                    deviceId = preferencesRepository.getDeviceId()
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                saveTokens(body.accessToken, body.refreshToken)
                _currentUser.value = body.user
                _authState.value = AuthState.Authenticated
                AuthResult.Success(body.user)
            } else {
                _authState.value = AuthState.NotAuthenticated
                AuthResult.Error(response.body()?.message ?: "Login failed")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.NotAuthenticated
            AuthResult.Error("Network error: ${e.message}")
        }
    }

    /**
     * Register a new user.
     */
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {
        try {
            _authState.value = AuthState.Loading

            val response = authApiService.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    deviceId = preferencesRepository.getDeviceId()
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                saveTokens(body.accessToken, body.refreshToken)
                _currentUser.value = body.user
                _authState.value = AuthState.Authenticated
                AuthResult.Success(body.user)
            } else {
                _authState.value = AuthState.NotAuthenticated
                AuthResult.Error(response.body()?.message ?: "Registration failed")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.NotAuthenticated
            AuthResult.Error("Network error: ${e.message}")
        }
    }

    /**
     * Logout and clear tokens.
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        clearTokens()
        _currentUser.value = null
        _authState.value = AuthState.NotAuthenticated
    }

    /**
     * Refresh the access token.
     */
    private suspend fun refreshToken(): Boolean {
        val refreshToken = preferencesRepository.getRefreshToken() ?: return false

        return try {
            val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                saveTokens(body.accessToken, body.refreshToken)
                _currentUser.value = body.user
                _authState.value = AuthState.Authenticated
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the current access token for API calls.
     */
    fun getAccessToken(): String? = preferencesRepository.getAuthToken()

    private fun saveTokens(accessToken: String?, refreshToken: String?) {
        accessToken?.let { preferencesRepository.setAuthToken(it) }
        refreshToken?.let { preferencesRepository.setRefreshToken(it) }
    }

    private fun clearTokens() {
        preferencesRepository.clearAuthToken()
        preferencesRepository.clearRefreshToken()
    }
}

/**
 * Authentication state.
 */
sealed class AuthState {
    object Unknown : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object AuthenticatedOffline : AuthState()
    object NotAuthenticated : AuthState()
}

/**
 * Result of authentication operations.
 */
sealed class AuthResult {
    data class Success(val user: UserInfo?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
