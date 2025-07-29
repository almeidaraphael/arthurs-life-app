package com.arthurslife.app.presentation.theme

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Handles theme-related errors and provides user-friendly error messages.
 *
 * This class manages error reporting for theme operations, providing appropriate
 * user-facing messages and fallback behavior when theme loading or saving fails.
 */
object ThemeErrorHandler {

    private val _errorEvents = MutableSharedFlow<ThemeErrorEvent>()
    val errorEvents: SharedFlow<ThemeErrorEvent> = _errorEvents.asSharedFlow()

    /**
     * Handle theme loading errors.
     *
     * @param error The original exception
     * @param fallbackTheme The fallback theme that was used
     */
    suspend fun handleThemeLoadError(
        error: Throwable,
        fallbackTheme: String,
    ) {
        val errorEvent = ThemeErrorEvent(
            type = ThemeErrorType.LOAD_FAILED,
            message = "Failed to load your saved theme. Using $fallbackTheme theme instead.",
            actionMessage = "Retry",
            duration = SnackbarDuration.Long,
            originalError = error,
        )
        _errorEvents.emit(errorEvent)
    }

    /**
     * Handle theme saving errors.
     *
     * @param error The original exception
     * @param themeName The name of the theme that failed to save
     */
    suspend fun handleThemeSaveError(
        error: Throwable,
        themeName: String,
    ) {
        val errorEvent = ThemeErrorEvent(
            type = ThemeErrorType.SAVE_FAILED,
            message = "Failed to save $themeName theme preference. Your selection may not persist.",
            actionMessage = "Try Again",
            duration = SnackbarDuration.Long,
            originalError = error,
        )
        _errorEvents.emit(errorEvent)
    }

    /**
     * Handle theme validation errors.
     *
     * @param error The original exception
     * @param themeName The name of the theme that failed validation
     */
    suspend fun handleThemeValidationError(
        error: Throwable,
        themeName: String,
    ) {
        val errorEvent = ThemeErrorEvent(
            type = ThemeErrorType.VALIDATION_FAILED,
            message = "The $themeName theme has accessibility issues and may not be suitable for all users.",
            actionMessage = "Learn More",
            duration = SnackbarDuration.Short,
            originalError = error,
        )
        _errorEvents.emit(errorEvent)
    }

    /**
     * Handle general theme errors.
     *
     * @param error The original exception
     * @param message Custom error message
     */
    suspend fun handleGenericThemeError(
        error: Throwable,
        message: String = "A theme-related error occurred.",
    ) {
        val errorEvent = ThemeErrorEvent(
            type = ThemeErrorType.GENERIC,
            message = message,
            actionMessage = "Dismiss",
            duration = SnackbarDuration.Short,
            originalError = error,
        )
        _errorEvents.emit(errorEvent)
    }

    /**
     * Get a user-friendly error message for a theme operation.
     *
     * @param error The original exception
     * @param operation The operation that failed (e.g., "loading", "saving")
     * @return User-friendly error message
     */
    fun getErrorMessage(error: Throwable, operation: String): String {
        return when (error) {
            is SecurityException -> "Permission denied while $operation theme. Please check app permissions."
            is java.io.IOException -> "Storage error while $operation theme. Please check available storage space."
            is IllegalStateException -> "App state error while $operation theme. Please restart the app."
            is IllegalArgumentException -> "Invalid theme data while $operation theme. Using default theme instead."
            else -> "An unexpected error occurred while $operation theme."
        }
    }

    /**
     * Clear all pending error events.
     */
    suspend fun clearErrors() {
        // Emit a clear event (not implemented in this version)
    }
}

/**
 * Data class representing a theme-related error event.
 */
data class ThemeErrorEvent(
    val type: ThemeErrorType,
    val message: String,
    val actionMessage: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val originalError: Throwable? = null,
) {
    /**
     * Get a detailed error description for logging.
     */
    val detailedDescription: String
        get() = buildString {
            append("Theme Error: $type")
            append("\nMessage: $message")
            originalError?.let {
                append("\nCause: ${it.javaClass.simpleName}")
                append("\nDetails: ${it.message}")
            }
        }

    /**
     * Check if this error event has an action.
     */
    val hasAction: Boolean
        get() = actionMessage != null
}

/**
 * Enum representing different types of theme errors.
 */
enum class ThemeErrorType(val displayName: String) {
    LOAD_FAILED("Theme Load Failed"),
    SAVE_FAILED("Theme Save Failed"),
    VALIDATION_FAILED("Theme Validation Failed"),
    GENERIC("Theme Error"),
}
