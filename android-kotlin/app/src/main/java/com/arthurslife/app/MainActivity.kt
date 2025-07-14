package com.arthurslife.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.arthurslife.app.presentation.navigation.AuthenticationNavigation
import com.arthurslife.app.presentation.navigation.MainAppNavigation
import com.arthurslife.app.presentation.navigation.onboardingNavigation
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.viewmodels.AppState
import com.arthurslife.app.presentation.viewmodels.AppViewModel
import com.arthurslife.app.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArthursLifeTheme()
        }
    }
}

@Composable
fun ArthursLifeTheme(
    authViewModel: AuthViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val authState by authViewModel.authState.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    // Load theme when user role changes
    LaunchedEffect(authState.currentRole) {
        authState.currentRole?.let { role ->
            themeViewModel.loadTheme(role)
        }
    }

    // Use the selected theme from ThemeViewModel (already a BaseAppTheme)
    val activeTheme = currentTheme

    MaterialTheme(
        colorScheme = activeTheme.colorScheme,
        typography = activeTheme.typography,
        shapes = activeTheme.shapes,
    ) {
        ArthursLifeApp(authViewModel = authViewModel, themeViewModel = themeViewModel)
    }
}

@Composable
fun ArthursLifeApp(
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
                loadingContent(innerPadding)
            }
            AppState.NeedsOnboarding -> {
                onboardingContent(navController, appViewModel)
            }
            AppState.ReadyForAuth -> {
                authenticationContent(
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
private fun loadingContent(innerPadding: PaddingValues) {
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
private fun onboardingContent(
    navController: NavHostController,
    appViewModel: AppViewModel,
) {
    onboardingNavigation(
        navController = navController,
        onOnboardingComplete = {
            appViewModel.onOnboardingCompleted()
        },
    )
}

@Composable
private fun authenticationContent(
    authState: com.arthurslife.app.domain.auth.AuthenticationState,
    innerPadding: PaddingValues,
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    authViewModel: AuthViewModel,
) {
    Log.d(
        TAG,
        "authenticationContent - isAuthenticated: ${authState.isAuthenticated}, role: ${authState.currentRole}",
    )

    if (authState.isAuthenticated && authState.currentRole != null) {
        Log.i(TAG, "Showing MainAppNavigation for role: ${authState.currentRole}")
        MainAppNavigation(
            userRole = authState.currentRole!!,
            themeViewModel = themeViewModel,
            authViewModel = authViewModel,
            modifier = Modifier.padding(innerPadding),
        )
    } else {
        Log.d(TAG, "Showing AuthenticationNavigation")
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
    childUser: com.arthurslife.app.domain.user.User,
    authViewModel: AuthViewModel,
) {
    Log.i(TAG, "Child selected - ${childUser.name} (${childUser.id})")
    authViewModel.authenticateAsSpecificChild(childUser) { authResult ->
        when (authResult) {
            is com.arthurslife.app.domain.auth.AuthResult.Success -> {
                Log.i(TAG, "Child authentication successful for ${childUser.name}")
            }
            is com.arthurslife.app.domain.auth.AuthResult.UserNotFound -> {
                Log.e(TAG, "Child authentication failed - user not found")
            }
            is com.arthurslife.app.domain.auth.AuthResult.InvalidPin -> {
                Log.e(TAG, "Child authentication failed - invalid PIN")
            }
        }
    }
}

private const val TAG = "MainActivity"
