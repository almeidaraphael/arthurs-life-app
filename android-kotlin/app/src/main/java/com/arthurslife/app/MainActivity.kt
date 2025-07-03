package com.arthurslife.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.arthurslife.app.presentation.navigation.authenticationNavigation
import com.arthurslife.app.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            arthursLifeTheme {
                arthursLifeApp()
            }
        }
    }
}

@Composable
fun arthursLifeTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content,
        )
    }
}

@Composable
fun arthursLifeApp(authViewModel: AuthViewModel = hiltViewModel()) {
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (authState.isAuthenticated) {
            // Navigate to main app based on user role
            mainAppContent(
                userRole = authState.currentRole,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            authenticationNavigation(
                navController = navController,
                onAuthSuccess = { _ ->
                    // Authentication handled by ViewModel
                },
            )
        }
    }
}

@Composable
fun mainAppContent(
    userRole: com.arthurslife.app.domain.user.UserRole?,
    modifier: Modifier = Modifier,
) {
    // Temporary placeholder for authenticated user content
    Text(
        text = "Welcome ${userRole?.name ?: "User"}! Main app coming soon...",
        modifier = modifier,
    )
}
