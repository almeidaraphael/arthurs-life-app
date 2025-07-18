package com.lemonqwest.app.presentation.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.R
import com.lemonqwest.app.presentation.theme.LocalBaseTheme

/**
 * Welcome screen that introduces the app and its features to new users.
 * Provides an accessible entry point to the onboarding flow.
 *
 * @param onContinue Callback when user wants to start the onboarding process
 */
@Composable
fun WelcomeScreen(
    onContinue: () -> Unit,
) {
    val theme = LocalBaseTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Welcome screen for LemonQwest family task management app"
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            WelcomeHeader(theme)
            WelcomeFeaturesList()
            WelcomeContinueButton(onContinue)
        }
    }
}

@Composable
private fun WelcomeHeader(
    theme: com.lemonqwest.app.presentation.theme.BaseAppTheme,
) {
    Image(
        painter = painterResource(id = R.drawable.default_avatar),
        contentDescription = "LemonQwest app logo - a family task management application",
        modifier = Modifier
            .size(120.dp)
            .semantics {
                contentDescription = "App logo representing family task management"
            },
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = "Welcome to LemonQwest!",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = theme.textColors.primary,
        modifier = Modifier.semantics {
            heading()
            contentDescription = "Welcome to LemonQwest family task management app"
        },
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "A fun and engaging way for families to manage tasks, earn rewards, and build great habits together!",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = theme.textColors.secondary,
        modifier = Modifier.semantics {
            contentDescription = "App description: A family-friendly way to manage tasks, earn rewards, and build habits"
        },
    )

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun WelcomeFeaturesList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "List of app features"
            },
        horizontalAlignment = Alignment.Start,
    ) {
        WelcomeFeatureItem(
            text = "Create tasks for children",
            emoji = "✨",
            description = "Feature: Create and assign tasks to children",
        )
        WelcomeFeatureItem(
            text = "Earn tokens for completed tasks",
            emoji = "🏆",
            description = "Feature: Children earn digital tokens by completing assigned tasks",
        )
        WelcomeFeatureItem(
            text = "Redeem rewards with tokens",
            emoji = "🎁",
            description = "Feature: Use earned tokens to unlock and redeem family rewards",
        )
        WelcomeFeatureItem(
            text = "Track progress and achievements",
            emoji = "📊",
            description = "Feature: Monitor family progress and celebrate achievements",
        )
    }
}

@Composable
private fun WelcomeContinueButton(onContinue: () -> Unit) {
    Spacer(modifier = Modifier.height(48.dp))

    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Start the family setup process"
                role = Role.Button
            },
    ) {
        Text(
            text = "Get Started",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

/**
 * Individual feature item in the welcome screen feature list.
 *
 * @param text Feature description text
 * @param emoji Visual emoji representing the feature
 * @param description Accessibility description for screen readers
 */
@Composable
private fun WelcomeFeatureItem(
    text: String,
    emoji: String,
    description: String,
) {
    val theme = LocalBaseTheme.current

    Text(
        text = "$emoji $text",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .semantics {
                contentDescription = description
            },
        color = theme.textColors.secondary,
    )
}
