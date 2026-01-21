package com.gultekinahmetabdullah.trainvoc.core.common

/**
 * A type-safe result wrapper for operations that can fail
 *
 * This sealed class provides a better alternative to throwing exceptions
 * for expected error conditions. It forces explicit error handling and
 * makes the error path visible in the type system.
 *
 * Usage:
 * ```kotlin
 * fun loadUser(id: String): AppResult<User> {
 *     return try {
 *         val user = database.getUser(id)
 *         AppResult.Success(user)
 *     } catch (e: Exception) {
 *         AppResult.Error(AppError.Database(e.message))
 *     }
 * }
 *
 * // Consume result
 * when (val result = loadUser("123")) {
 *     is AppResult.Success -> updateUI(result.data)
 *     is AppResult.Error -> showError(result.error.message)
 *     is AppResult.Loading -> showLoading()
 * }
 * ```
 */
sealed class AppResult<out T> {
    /**
     * Operation is in progress
     */
    object Loading : AppResult<Nothing>()

    /**
     * Operation completed successfully
     * @param data The successful result data
     */
    data class Success<T>(val data: T) : AppResult<T>()

    /**
     * Operation failed with an error
     * @param error The error that occurred
     */
    data class Error(val error: AppError) : AppResult<Nothing>()

    /**
     * Check if result is successful
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Check if result is error
     */
    fun isError(): Boolean = this is Error

    /**
     * Check if result is loading
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Get data if success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Get data if success, or throw error
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw error.toException()
        is Loading -> throw IllegalStateException("Cannot get data from loading state")
    }

    /**
     * Map success value to another type
     */
    inline fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    /**
     * Flat map for chaining results
     */
    inline fun <R> flatMap(transform: (T) -> AppResult<R>): AppResult<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
        is Loading -> this
    }

    /**
     * Execute action if success
     */
    inline fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Execute action if error
     */
    inline fun onError(action: (AppError) -> Unit): AppResult<T> {
        if (this is Error) action(error)
        return this
    }

    /**
     * Execute action if loading
     */
    inline fun onLoading(action: () -> Unit): AppResult<T> {
        if (this is Loading) action()
        return this
    }
}

/**
 * Application-specific errors
 *
 * Provides typed, descriptive errors for different failure scenarios
 */
sealed class AppError {
    abstract val message: String
    abstract val cause: Throwable?

    /**
     * Network-related errors
     */
    data class Network(
        override val message: String = "Network error occurred",
        override val cause: Throwable? = null
    ) : AppError()

    /**
     * Database-related errors
     */
    data class Database(
        override val message: String = "Database error occurred",
        override val cause: Throwable? = null
    ) : AppError()

    /**
     * Input validation errors
     */
    data class Validation(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError()

    /**
     * Authentication/Authorization errors
     */
    data class Auth(
        override val message: String = "Authentication failed",
        override val cause: Throwable? = null
    ) : AppError()

    /**
     * Resource not found errors
     */
    data class NotFound(
        override val message: String = "Resource not found",
        override val cause: Throwable? = null
    ) : AppError()

    /**
     * Feature not enabled/available
     */
    data class FeatureDisabled(
        override val message: String = "Feature is not enabled",
        override val cause: Throwable? = null
    ) : AppError()

    /**
     * Generic/Unknown errors
     */
    data class Unknown(
        override val message: String = "An unknown error occurred",
        override val cause: Throwable? = null
    ) : AppError()

    /**
     * Convert error to exception for compatibility
     */
    fun toException(): Exception = when (this) {
        is Network -> cause as? Exception ?: Exception(message)
        is Database -> cause as? Exception ?: Exception(message)
        is Validation -> cause as? Exception ?: IllegalArgumentException(message)
        is Auth -> cause as? Exception ?: SecurityException(message)
        is NotFound -> cause as? Exception ?: NoSuchElementException(message)
        is FeatureDisabled -> cause as? Exception ?: UnsupportedOperationException(message)
        is Unknown -> cause as? Exception ?: Exception(message)
    }

    /**
     * Get user-friendly error message
     */
    fun getUserMessage(): String = when (this) {
        is Network -> "Please check your internet connection and try again"
        is Database -> "An error occurred while saving your data"
        is Validation -> message // Validation messages are already user-friendly
        is Auth -> "Please sign in to continue"
        is NotFound -> "The requested item could not be found"
        is FeatureDisabled -> "This feature is currently unavailable"
        is Unknown -> "Something went wrong. Please try again"
    }
}

/**
 * Extension functions for Kotlin Result to AppResult conversion
 */
fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.Success(it) },
    onFailure = { AppResult.Error(AppError.Unknown(it.message ?: "Unknown error", it)) }
)

/**
 * Extension function to create AppResult from nullable value
 */
fun <T> T?.toAppResult(errorMessage: String = "Value is null"): AppResult<T> =
    this?.let { AppResult.Success(it) } ?: AppResult.Error(AppError.NotFound(errorMessage))
