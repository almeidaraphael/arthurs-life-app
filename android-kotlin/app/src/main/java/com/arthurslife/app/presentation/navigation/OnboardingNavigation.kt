package com.arthurslife.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arthurslife.app.presentation.screens.onboarding.OnboardingCompleteScreen
import com.arthurslife.app.presentation.screens.onboarding.WelcomeScreen
import com.arthurslife.app.presentation.screens.onboarding.addChildrenScreen
import com.arthurslife.app.presentation.screens.onboarding.familySetupScreen
import com.arthurslife.app.presentation.viewmodels.OnboardingViewModel

sealed class OnboardingNavigation(val route: String) {
    data object Welcome : OnboardingNavigation("welcome")
    data object FamilySetup : OnboardingNavigation("family_setup")
    data object AddChildren : OnboardingNavigation("add_children")
    data object Complete : OnboardingNavigation("complete")
}

@Composable
fun onboardingNavigation(
    navController: NavHostController,
    onOnboardingComplete: () -> Unit,
) {
    val viewModel: OnboardingViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = OnboardingNavigation.Welcome.route,
    ) {
        composable(OnboardingNavigation.Welcome.route) {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(OnboardingNavigation.FamilySetup.route)
                },
            )
        }

        composable(OnboardingNavigation.FamilySetup.route) {
            familySetupScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.navigate(OnboardingNavigation.AddChildren.route)
                },
            )
        }

        composable(OnboardingNavigation.AddChildren.route) {
            addChildrenScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.navigate(OnboardingNavigation.Complete.route)
                },
            )
        }

        composable(OnboardingNavigation.Complete.route) {
            OnboardingCompleteScreen(
                viewModel = viewModel,
                onComplete = onOnboardingComplete,
            )
        }
    }
}
