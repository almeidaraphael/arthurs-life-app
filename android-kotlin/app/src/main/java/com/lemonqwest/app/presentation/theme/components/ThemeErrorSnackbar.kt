package com.lemonqwest.app.presentation.theme.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.lemonqwest.app.presentation.theme.ThemeErrorEvent
import kotlinx.coroutines.flow.SharedFlow

/**
 * Composable that handles theme error events and displays them as snackbars.
 *
 * This component observes theme error events and displays user-friendly error messages
 * using Material Design snackbars. It provides retry actions when appropriate.
 *
 * @param errorEvents SharedFlow of theme error events from ThemeViewModel
 * @param snackbarHostState SnackbarHostState for displaying messages
 * @param onRetryThemeLoad Callback for retrying theme loading operations
 * @param onRetryThemeSave Callback for retrying theme saving operations
 */
@Composable
fun ThemeErrorSnackbar(
    errorEvents: SharedFlow<ThemeErrorEvent>,
    snackbarHostState: SnackbarHostState,
    onRetryThemeLoad: (() -> Unit)? = null,
    onRetryThemeSave: (() -> Unit)? = null,
) {
    LaunchedEffect(errorEvents) {
        errorEvents.collect { errorEvent ->
            val result = snackbarHostState.showSnackbar(
                message = errorEvent.message,
                actionLabel = errorEvent.actionMessage,
                duration = errorEvent.duration,
            )

            // Handle action button clicks
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    handleErrorAction(errorEvent, onRetryThemeLoad, onRetryThemeSave)
                }
                SnackbarResult.Dismissed -> {
                    // User dismissed the snackbar, no action needed
                }
            }
        }
    }
}

/**
 * Handle action button clicks for theme error snackbars.
 *
 * @param errorEvent The error event that was displayed
 * @param onRetryThemeLoad Callback for retrying theme loading
 * @param onRetryThemeSave Callback for retrying theme saving
 */
private fun handleErrorAction(
    errorEvent: ThemeErrorEvent,
    onRetryThemeLoad: (() -> Unit)?,
    onRetryThemeSave: (() -> Unit)?,
) {
    when (errorEvent.type) {
        com.lemonqwest.app.presentation.theme.ThemeErrorType.LOAD_FAILED -> {
            onRetryThemeLoad?.invoke()
        }
        com.lemonqwest.app.presentation.theme.ThemeErrorType.SAVE_FAILED -> {
            onRetryThemeSave?.invoke()
        }
        com.lemonqwest.app.presentation.theme.ThemeErrorType.VALIDATION_FAILED -> {
            // Could open accessibility information dialog
            // For now, just dismiss
        }
        com.lemonqwest.app.presentation.theme.ThemeErrorType.GENERIC -> {
            // Generic errors typically just need dismissal
        }
    }
}

/**
 * Create a remember SnackbarHostState for theme errors.
 *
 * @return SnackbarHostState configured for theme error display
 */
@Composable
fun rememberThemeErrorSnackbarState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}
