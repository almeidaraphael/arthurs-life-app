package com.arthurslife.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.navigation.AuthenticationNavigation
import com.arthurslife.app.presentation.navigation.MainAppNavigation
import com.arthurslife.app.presentation.theme.ThemeViewModel
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
) {
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (authState.isAuthenticated && authState.currentRole != null) {
            // Navigate to main app based on user role
            MainAppNavigation(
                userRole = authState.currentRole!!,
                themeViewModel = themeViewModel,
                modifier = Modifier.padding(innerPadding),
                onRoleSwitch = { newRole ->
                    // Role switching is handled by the AuthViewModel
                    // The UI will automatically update when authState changes
                },
            )
        } else {
            AuthenticationNavigation(
                navController = navController,
                themeViewModel = themeViewModel,
                onAuthSuccess = { role ->
                    // Set authentication state based on role
                    when (role) {
                        UserRole.CHILD -> {
                            // For child, directly authenticate without PIN
                            authViewModel.authenticateAsChild { _ ->
                                // Authentication result handled by ViewModel state
                            }
                        }
                        UserRole.CAREGIVER -> {
                            // PIN authentication for caregiver is handled in PinEntryScreen
                            // This callback should only be called after successful PIN entry
                        }
                    }
                },
            )
        }
    }
}
