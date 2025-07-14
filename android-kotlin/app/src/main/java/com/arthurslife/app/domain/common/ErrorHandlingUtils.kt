package com.arthurslife.app.domain.common

import androidx.lifecycle.ViewModel

/**
 * Simple error handling utilities to eliminate generic exception catching.
 */

/**
 * Maps a generic throwable to a TaskDomainException.
 * This provides a clean way to convert exceptions without generic catching.
 */
fun mapToTaskException(operation: String, cause: Throwable): TaskDomainException {
    return TaskDomainException("$operation failed: ${cause.message}", cause)
}

/**
 * Maps a generic throwable to a UserDomainException.
 */
fun mapToUserException(operation: String, cause: Throwable): UserDomainException {
    return UserDomainException("$operation failed: ${cause.message}", cause)
}

/**
 * Maps a generic throwable to a PresentationException.
 */
fun mapToPresentationException(operation: String, cause: Throwable): PresentationException {
    return PresentationException("$operation failed: ${cause.message}", cause)
}

/**
 * Handles errors in ViewModels with consistent error message formatting.
 *
 * @param operation Name of the operation that failed
 * @param error The error that occurred
 * @return User-friendly error message
 */
fun ViewModel.handleError(
    operation: String,
    error: Throwable,
): String {
    return when (error) {
        is DomainException -> error.message ?: "Unknown error occurred"
        else -> "$operation failed: ${error.message ?: "Unknown error"}"
    }
}
