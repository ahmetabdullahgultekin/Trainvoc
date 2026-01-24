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
 * Manages login, registration, and auth state for the UI.
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
     * Login with username and password.
     */
    fun login(username: String, password: String) {
        if (username.isBlank()) {
            _loginError.value = "Please enter a username"
            return
        }
        if (password.isBlank()) {
            _loginError.value = "Please enter a password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null

            when (val result = authRepository.login(username, password)) {
                is AuthResult.Success -> {
                    // Login successful, authState will be updated
                }
                is AuthResult.Error -> {
                    _loginError.value = result.message
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * Register a new user.
     */
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isBlank()) {
            _registerError.value = "Please enter a username"
            return
        }
        if (username.length < 3) {
            _registerError.value = "Username must be at least 3 characters"
            return
        }
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

            when (val result = authRepository.register(username, email, password)) {
                is AuthResult.Success -> {
                    // Registration successful, authState will be updated
                }
                is AuthResult.Error -> {
                    _registerError.value = result.message
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
}
