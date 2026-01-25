package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gultekinahmetabdullah.trainvoc.auth.AuthRepository
import com.gultekinahmetabdullah.trainvoc.auth.AuthResult
import com.gultekinahmetabdullah.trainvoc.auth.AuthState
import com.gultekinahmetabdullah.trainvoc.auth.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication operations.
 * Manages login, registration, password reset, and auth state for the UI.
 *
 * With Firebase Auth integration:
 * - Login/Register use Firebase Authentication
 * - Password reset uses Firebase
 * - Backend sync happens automatically after Firebase auth
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState
    val currentUser: StateFlow<UserInfo?> = authRepository.currentUser

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _passwordResetError = MutableStateFlow<String?>(null)
    val passwordResetError: StateFlow<String?> = _passwordResetError.asStateFlow()

    private val _passwordResetSent = MutableStateFlow(false)
    val passwordResetSent: StateFlow<Boolean> = _passwordResetSent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkAuthState()
    }

    /**
     * Check current authentication state.
     */
    fun checkAuthState() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.checkAuthState()
            _isLoading.value = false
        }
    }

    /**
     * Login with email and password.
     * Note: Firebase Auth requires email, not username.
     */
    fun login(email: String, password: String) {
        if (email.isBlank()) {
            _loginError.value = "Please enter your email"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginError.value = "Please enter a valid email"
            return
        }
        if (password.isBlank()) {
            _loginError.value = "Please enter your password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null

            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    // Login successful, authState will be updated
                }
                is AuthResult.Error -> {
                    _loginError.value = result.message
                }
                else -> {
                    _loginError.value = "Unexpected error during login"
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Register a new user with email and password.
     * Display name is optional and can be set during registration.
     */
    fun register(email: String, password: String, confirmPassword: String, displayName: String = "") {
        if (email.isBlank()) {
            _registerError.value = "Please enter an email"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerError.value = "Please enter a valid email"
            return
        }
        if (password.isBlank()) {
            _registerError.value = "Please enter a password"
            return
        }
        if (password.length < 6) {
            _registerError.value = "Password must be at least 6 characters"
            return
        }
        if (password != confirmPassword) {
            _registerError.value = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _registerError.value = null

            val name = displayName.ifBlank { null }
            when (val result = authRepository.register(email, password, name)) {
                is AuthResult.Success -> {
                    // Registration successful, authState will be updated
                }
                is AuthResult.Error -> {
                    _registerError.value = result.message
                }
                else -> {
                    _registerError.value = "Unexpected error during registration"
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Send password reset email.
     */
    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _passwordResetError.value = "Please enter your email"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _passwordResetError.value = "Please enter a valid email"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _passwordResetError.value = null
            _passwordResetSent.value = false

            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is AuthResult.PasswordResetSent -> {
                    _passwordResetSent.value = true
                }
                is AuthResult.Error -> {
                    _passwordResetError.value = result.message
                }
                else -> {
                    _passwordResetError.value = "Unexpected error sending reset email"
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Logout and clear session.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    /**
     * Clear login error.
     */
    fun clearLoginError() {
        _loginError.value = null
    }

    /**
     * Clear registration error.
     */
    fun clearRegisterError() {
        _registerError.value = null
    }

    /**
     * Clear password reset state.
     */
    fun clearPasswordResetState() {
        _passwordResetError.value = null
        _passwordResetSent.value = false
    }

    /**
     * Gets the current user's email.
     */
    fun getCurrentEmail(): String? {
        return authRepository.getCurrentEmail()
    }

    /**
     * Checks if email is verified.
     */
    fun isEmailVerified(): Boolean {
        return authRepository.isEmailVerified()
    }
}
