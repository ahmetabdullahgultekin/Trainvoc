package com.gultekinahmetabdullah.trainvoc.auth

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Body

/**
 * Retrofit interface for authentication API endpoints.
 * Connects to the Trainvoc backend for user authentication.
 *
 * Note: With Firebase Auth integration, most authentication is handled
 * by Firebase SDK. This service is primarily used for:
 * - Syncing Firebase users to backend database
 * - Backend-specific user operations
 */
interface AuthApiService {

    /**
     * Sync Firebase user to backend database.
     * Called after successful Firebase authentication to ensure
     * user exists in backend and to get backend-specific user info.
     *
     * The Firebase ID token is sent in the Authorization header.
     */
    @GET("api/v1/auth/firebase-sync")
    suspend fun firebaseSync(
        @Header("Authorization") token: String
    ): Response<FirebaseSyncResponse>

    /**
     * Register a new user (legacy endpoint, kept for backward compatibility).
     * With Firebase Auth, registration is handled by Firebase SDK.
     */
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    /**
     * Login with username and password (legacy endpoint, kept for backward compatibility).
     * With Firebase Auth, login is handled by Firebase SDK.
     */
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    /**
     * Validate current token (legacy endpoint).
     */
    @GET("api/v1/auth/me")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): Response<UserInfo>

    /**
     * Refresh access token (legacy endpoint).
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

/**
 * Response from Firebase sync endpoint.
 */
data class FirebaseSyncResponse(
    val id: Long,
    val username: String,
    val email: String,
    val displayName: String?,
    val firebaseUid: String,
    val emailVerified: Boolean,
    val authProvider: String,
    val totalGamesPlayed: Int,
    val totalScore: Int,
    val gamesWon: Int
)
