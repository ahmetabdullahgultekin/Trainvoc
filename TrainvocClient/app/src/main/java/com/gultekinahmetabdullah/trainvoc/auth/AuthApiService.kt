package com.gultekinahmetabdullah.trainvoc.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit interface for authentication API endpoints.
 * Connects to the Trainvoc backend for user authentication.
 */
interface AuthApiService {

    /**
     * Register a new user.
     */
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    /**
     * Login with username and password.
     */
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    /**
     * Validate current token.
     */
    @GET("api/v1/auth/me")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): Response<UserInfo>

    /**
     * Refresh access token.
     */
    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<AuthResponse>
}

// ============ Request DTOs ============

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val deviceId: String
)

data class LoginRequest(
    val username: String,
    val password: String,
    val deviceId: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

// ============ Response DTOs ============

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val user: UserInfo?
)

data class UserInfo(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: String?
)
