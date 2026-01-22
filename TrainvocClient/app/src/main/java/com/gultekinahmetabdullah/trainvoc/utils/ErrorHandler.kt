package com.gultekinahmetabdullah.trainvoc.utils

import android.util.Log
import kotlinx.coroutines.CancellationException

/**
 * Centralized error handling utilities
 *
 * Features:
 * - Consistent error logging
 * - User-friendly error messages
 * - Error categorization
 * - Exception wrapping with context
 */
object ErrorHandler {

    private const val TAG = "TrainVoc"

    /**
     * Handles exceptions with proper logging and user message generation
     *
     * @param exception The exception to handle
     * @param context Additional context about where the error occurred
     * @param logLevel Log level to use (default: ERROR)
     * @return User-friendly error message
     */
    fun handleException(
        exception: Throwable,
        context: String = "",
        logLevel: LogLevel = LogLevel.ERROR
    ): String {
        // Don't handle CancellationException (coroutine cancellation is normal)
        if (exception is CancellationException) {
            throw exception
        }

        // Log the error
        logError(exception, context, logLevel)

        // Generate user-friendly message
        return getUserFriendlyMessage(exception)
    }

    /**
     * Logs an error with consistent formatting
     *
     * @param exception The exception to log
     * @param context Additional context
     * @param level Log level
     */
    fun logError(
        exception: Throwable,
        context: String = "",
        level: LogLevel = LogLevel.ERROR
    ) {
        val message = buildString {
            if (context.isNotEmpty()) {
                append("[$context] ")
            }
            append(exception.message ?: exception.javaClass.simpleName)
        }

        when (level) {
            LogLevel.ERROR -> Log.e(TAG, message, exception)
            LogLevel.WARNING -> Log.w(TAG, message, exception)
            LogLevel.INFO -> Log.i(TAG, message)
            LogLevel.DEBUG -> Log.d(TAG, message)
        }
    }

    /**
     * Generates user-friendly error message from exception
     *
     * @param exception The exception
     * @return User-friendly message
     */
    private fun getUserFriendlyMessage(exception: Throwable): String {
        return when (exception) {
            is java.io.FileNotFoundException -> "File not found. Please check the file path."
            is java.io.IOException -> "Unable to read or write file. Please try again."
            is SecurityException -> "Security error. The file may be corrupted or tampered with."
            is IllegalArgumentException -> exception.message ?: "Invalid input provided."
            is IllegalStateException -> exception.message ?: "The app is in an invalid state."
            is java.net.UnknownHostException -> "No internet connection. Please check your network."
            is java.net.SocketTimeoutException -> "Connection timed out. Please try again."
            is kotlinx.coroutines.TimeoutCancellationException -> "Operation took too long. Please try again."
            is OutOfMemoryError -> "Not enough memory. Please close other apps and try again."
            else -> exception.message ?: "An unexpected error occurred. Please try again."
        }
    }

    /**
     * Executes a block with error handling
     *
     * @param context Description of the operation
     * @param block The operation to execute
     * @return Result of the operation
     */
    suspend fun <T> withErrorHandling(
        context: String,
        block: suspend () -> T
    ): AppResult<T> {
        return try {
            val result = block()
            AppResult.Success(result)
        } catch (e: CancellationException) {
            // Re-throw cancellation exception (coroutine cancellation)
            throw e
        } catch (e: Exception) {
            val message = handleException(e, context)
            AppResult.Error(message, e)
        }
    }

    /**
     * Executes a block with error handling (synchronous)
     *
     * @param context Description of the operation
     * @param block The operation to execute
     * @return Result of the operation
     */
    fun <T> withErrorHandlingSync(
        context: String,
        block: () -> T
    ): AppResult<T> {
        return try {
            val result = block()
            AppResult.Success(result)
        } catch (e: Exception) {
            val message = handleException(e, context)
            AppResult.Error(message, e)
        }
    }
}

/**
 * Log level for error logging
 */
enum class LogLevel {
    ERROR,
    WARNING,
    INFO,
    DEBUG
}

/**
 * Result wrapper for operations that can fail
 * Similar to Kotlin's Result but with app-specific extensions
 */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception ?: Exception(message)
        is Loading -> throw IllegalStateException("Cannot get value from Loading state")
    }

    fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    fun getOrElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> defaultValue()
    }

    inline fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, exception)
        is Loading -> Loading
    }

    inline fun <R> flatMap(transform: (T) -> AppResult<R>): AppResult<R> = when (this) {
        is Success -> transform(data)
        is Error -> Error(message, exception)
        is Loading -> Loading
    }

    inline fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String, Throwable?) -> Unit): AppResult<T> {
        if (this is Error) action(message, exception)
        return this
    }

    inline fun onLoading(action: () -> Unit): AppResult<T> {
        if (this is Loading) action()
        return this
    }
}

/**
 * Extension function to convert Kotlin Result to AppResult
 */
fun <T> Result<T>.toAppResult(): AppResult<T> {
    return fold(
        onSuccess = { AppResult.Success(it) },
        onFailure = { AppResult.Error(it.message ?: "Unknown error", it) }
    )
}

/**
 * Extension function to convert AppResult to Kotlin Result
 */
fun <T> AppResult<T>.toResult(): Result<T> {
    return when (this) {
        is AppResult.Success -> Result.success(data)
        is AppResult.Error -> Result.failure(exception ?: Exception(message))
        is AppResult.Loading -> Result.failure(IllegalStateException("Cannot convert Loading to Result"))
    }
}
