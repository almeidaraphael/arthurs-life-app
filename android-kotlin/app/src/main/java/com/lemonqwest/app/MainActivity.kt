package com.lemonqwest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lemonqwest.app.presentation.navigation.AuthenticationNavigation
import com.lemonqwest.app.presentation.navigation.MainAppNavigation
import com.lemonqwest.app.presentation.navigation.OnboardingNavigation
import com.lemonqwest.app.presentation.theme.AppTheme
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.presentation.viewmodels.AppState
import com.lemonqwest.app.presentation.viewmodels.AppViewModel
import com.lemonqwest.app.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LemonQwestTheme()
        }
    }
}

@Composable
fun LemonQwestTheme(
    authViewModel: AuthViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val authState by authViewModel.authState.collectAsState()
    val currentAppTheme by themeViewModel.currentAppTheme.collectAsState()

    // Refresh theme when authentication state changes
    LaunchedEffect(authState.currentUser) {
        authState.currentUser?.let {
            themeViewModel.refreshTheme()
        }
    }

    // Use the AppTheme composable which provides LocalBaseTheme
    AppTheme(
        appTheme = currentAppTheme,
    ) {
        LemonQwestApp(authViewModel = authViewModel, themeViewModel = themeViewModel)
    }
}

@Composable
fun LemonQwestApp(
    authViewModel: AuthViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val authState by authViewModel.authState.collectAsState()
    val appState by appViewModel.appState.collectAsState()
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (appState) {
            AppState.Loading -> {
                LoadingContent(innerPadding)
            }
            AppState.NeedsOnboarding -> {
                OnboardingContent(navController, appViewModel)
            }
            AppState.ReadyForAuth -> {
                AuthenticationContent(
                    authState = authState,
                    innerPadding = innerPadding,
                    navController = navController,
                    themeViewModel = themeViewModel,
                    authViewModel = authViewModel,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun OnboardingContent(
    navController: NavHostController,
    appViewModel: AppViewModel,
) {
    OnboardingNavigation(
        navController = navController,
        onOnboardingComplete = {
            appViewModel.onOnboardingCompleted()
        },
    )
}

@Composable
private fun AuthenticationContent(
    authState: com.lemonqwest.app.domain.auth.AuthenticationState,
    innerPadding: PaddingValues,
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    authViewModel: AuthViewModel,
) {
    Timber.d(
        "authenticationContent - isAuthenticated: ${authState.isAuthenticated}, role: ${authState.currentRole}",
    )

    if (authState.isAuthenticated && authState.currentRole != null) {
        Timber.i("Showing MainAppNavigation for role: ${authState.currentRole}")
        MainAppNavigation(
            userRole = authState.currentRole!!,
            themeViewModel = themeViewModel,
            authViewModel = authViewModel,
            modifier = Modifier.padding(innerPadding),
        )
    } else {
        Timber.d("Showing AuthenticationNavigation")
        AuthenticationNavigation(
            navController = navController,
            themeViewModel = themeViewModel,
            authViewModel = authViewModel,
            onChildSelected = { childUser ->
                handleChildSelection(childUser, authViewModel)
            },
        )
    }
}

private fun handleChildSelection(
    childUser: com.lemonqwest.app.domain.user.User,
    authViewModel: AuthViewModel,
) {
    Timber.i("Child selected - ${childUser.name} (${childUser.id})")
    authViewModel.authenticateAsSpecificChild(childUser) { authResult ->
        when (authResult) {
            is com.lemonqwest.app.domain.auth.AuthResult.Success -> {
                Timber.i("Child authentication successful for ${childUser.name}")
            }
            is com.lemonqwest.app.domain.auth.AuthResult.UserNotFound -> {
                Timber.e("Child authentication failed - user not found")
            }
            is com.lemonqwest.app.domain.auth.AuthResult.InvalidPin -> {
                Timber.e("Child authentication failed - invalid PIN")
            }
        }
    }
}
