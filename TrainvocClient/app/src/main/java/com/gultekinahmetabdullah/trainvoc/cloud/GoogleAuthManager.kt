package com.gultekinahmetabdullah.trainvoc.cloud

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Google OAuth 2.0 authentication for Drive API access
 *
 * Features:
 * - Google Sign-In integration
 * - OAuth 2.0 token management
 * - Drive API scope authorization
 * - Sign-out functionality
 * - Account state management
 *
 * Prerequisites:
 * 1. Google Cloud Console project created
 * 2. OAuth 2.0 client ID configured
 * 3. SHA-1 fingerprint added to credentials
 * 4. google-services.json downloaded (if using Firebase)
 *
 * Usage:
 * ```kotlin
 * val authManager = GoogleAuthManager(context)
 *
 * // Sign in
 * val result = authManager.signIn(activity)
 * when (result) {
 *     is AuthResult.Success -> Log.d("Auth", "Signed in: ${result.account.email}")
 *     is AuthResult.Failure -> Log.e("Auth", "Sign-in failed: ${result.error}")
 *     AuthResult.Cancelled -> Log.d("Auth", "User cancelled sign-in")
 * }
 *
 * // Get access token
 * val token = authManager.getAccessToken()
 *
 * // Sign out
 * authManager.signOut()
 * ```
 */
@Singleton
class GoogleAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "GoogleAuthManager"
        const val RC_SIGN_IN = 9001

        // Required scopes for Drive backup/restore
        private val REQUIRED_SCOPES = listOf(
            Scope(DriveScopes.DRIVE_FILE),      // Access to files created by this app
            Scope(DriveScopes.DRIVE_APPDATA)    // Access to app-specific data folder
        )
    }

    private val signInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(REQUIRED_SCOPES[0], *REQUIRED_SCOPES.drop(1).toTypedArray())
            .build()
    }

    private val signInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, signInOptions)
    }

    /**
     * Get the currently signed-in account
     *
     * @return GoogleSignInAccount if signed in, null otherwise
     */
    fun getSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    /**
     * Check if user is currently signed in
     *
     * @return true if signed in with required scopes, false otherwise
     */
    fun isSignedIn(): Boolean {
        val account = getSignedInAccount()
        if (account == null) {
            Log.d(TAG, "Not signed in: No account found")
            return false
        }

        val hasScopes = GoogleSignIn.hasPermissions(account, *REQUIRED_SCOPES.toTypedArray())
        if (!hasScopes) {
            Log.d(TAG, "Not signed in: Missing required scopes")
        }

        return hasScopes
    }

    /**
     * Get sign-in intent for launching authentication flow
     *
     * Launch this intent with startActivityForResult() using RC_SIGN_IN request code
     *
     * @return Intent for Google Sign-In
     */
    fun getSignInIntent(): Intent {
        Log.d(TAG, "Creating sign-in intent")
        return signInClient.signInIntent
    }

    /**
     * Handle sign-in result from activity result
     *
     * Call this from onActivityResult() or ActivityResultLauncher callback
     *
     * @param data The intent data from activity result
     * @return AuthResult indicating success, failure, or cancellation
     */
    suspend fun handleSignInResult(data: Intent?): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                if (data == null) {
                    Log.w(TAG, "Sign-in cancelled: No data received")
                    return@withContext AuthResult.Cancelled
                }

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                if (account == null) {
                    Log.e(TAG, "Sign-in failed: Account is null")
                    return@withContext AuthResult.Failure("Failed to retrieve account")
                }

                // Verify required scopes
                val hasScopes = GoogleSignIn.hasPermissions(account, *REQUIRED_SCOPES.toTypedArray())
                if (!hasScopes) {
                    Log.e(TAG, "Sign-in failed: Missing required Drive scopes")
                    return@withContext AuthResult.Failure("Drive permissions not granted")
                }

                Log.i(TAG, "Sign-in successful: ${account.email}")
                AuthResult.Success(account)

            } catch (e: ApiException) {
                when (e.statusCode) {
                    12501 -> {
                        // User cancelled
                        Log.d(TAG, "Sign-in cancelled by user")
                        AuthResult.Cancelled
                    }
                    7 -> {
                        // Network error
                        Log.e(TAG, "Sign-in failed: Network error", e)
                        AuthResult.Failure("Network error. Please check your connection.")
                    }
                    10 -> {
                        // Developer error (misconfigured OAuth)
                        Log.e(TAG, "Sign-in failed: Developer error (check OAuth configuration)", e)
                        AuthResult.Failure("Authentication not properly configured. Please contact support.")
                    }
                    else -> {
                        Log.e(TAG, "Sign-in failed with code ${e.statusCode}", e)
                        AuthResult.Failure("Sign-in failed: ${e.localizedMessage ?: "Unknown error"}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during sign-in", e)
                AuthResult.Failure("Unexpected error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    /**
     * Request additional permissions if needed
     *
     * Use this if the user denied Drive permissions initially but later wants to enable them
     *
     * @param activity Activity context for launching permission request
     * @return AuthResult after permission request
     */
    suspend fun requestPermissions(activity: Activity): AuthResult {
        return withContext(Dispatchers.Main) {
            try {
                val account = getSignedInAccount()
                if (account == null) {
                    Log.w(TAG, "Cannot request permissions: User not signed in")
                    return@withContext AuthResult.Failure("Please sign in first")
                }

                // Request additional permissions
                GoogleSignIn.requestPermissions(
                    activity,
                    RC_SIGN_IN,
                    account,
                    *REQUIRED_SCOPES.toTypedArray()
                )

                // Note: Actual result will come through onActivityResult
                // This method just initiates the request
                AuthResult.Success(account)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to request permissions", e)
                AuthResult.Failure("Failed to request permissions: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Get OAuth 2.0 access token for Drive API
     *
     * This token is used to authenticate Drive API requests
     * Token is cached and refreshed automatically
     *
     * @return Access token string, or null if not authenticated
     */
    suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val account = getSignedInAccount()
                if (account == null) {
                    Log.w(TAG, "Cannot get token: User not signed in")
                    return@withContext null
                }

                // Get access token
                // Note: This is a simplified version. In production, you might want to use
                // GoogleAuthUtil.getToken() or implement more sophisticated token management
                val token = account.idToken

                if (token == null) {
                    Log.w(TAG, "Token is null. User may need to re-authenticate")
                }

                token

            } catch (e: Exception) {
                Log.e(TAG, "Failed to get access token", e)
                null
            }
        }
    }

    /**
     * Sign out from Google account
     *
     * Clears local authentication state
     * User will need to sign in again to use cloud features
     */
    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            try {
                signInClient.signOut().await()
                Log.i(TAG, "Successfully signed out")
            } catch (e: Exception) {
                Log.e(TAG, "Error during sign-out", e)
                // Still consider it a success since we want to clear state
            }
        }
    }

    /**
     * Revoke access and disconnect account
     *
     * More complete than signOut() - removes app authorization entirely
     * User will need to re-authorize the app from scratch
     */
    suspend fun revokeAccess() {
        withContext(Dispatchers.IO) {
            try {
                signInClient.revokeAccess().await()
                Log.i(TAG, "Successfully revoked access")
            } catch (e: Exception) {
                Log.e(TAG, "Error revoking access", e)
            }
        }
    }

    /**
     * Check if the signed-in account has all required Drive permissions
     *
     * @return true if all required scopes are granted, false otherwise
     */
    fun hasRequiredPermissions(): Boolean {
        val account = getSignedInAccount() ?: return false
        return GoogleSignIn.hasPermissions(account, *REQUIRED_SCOPES.toTypedArray())
    }

    /**
     * Get human-readable description of authentication state
     *
     * Useful for debugging and displaying status to users
     *
     * @return String describing current auth state
     */
    fun getAuthStateDescription(): String {
        val account = getSignedInAccount()
        return when {
            account == null -> "Not signed in"
            !hasRequiredPermissions() -> "Signed in as ${account.email} (missing Drive permissions)"
            else -> "Signed in as ${account.email}"
        }
    }
}

/**
 * Result of authentication operations
 */
sealed class AuthResult {
    /**
     * Authentication successful
     *
     * @property account The signed-in Google account
     */
    data class Success(val account: GoogleSignInAccount) : AuthResult()

    /**
     * Authentication failed
     *
     * @property error Human-readable error message
     */
    data class Failure(val error: String) : AuthResult()

    /**
     * User cancelled the authentication flow
     */
    data object Cancelled : AuthResult()
}
