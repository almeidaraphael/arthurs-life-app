package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.user.UserRole

@Composable
fun roleSelectionScreen(onRoleSelected: (UserRole) -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Who are you?",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onRoleSelected(UserRole.CHILD) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(80.dp),
        ) {
            Text(
                text = "Child",
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onRoleSelected(UserRole.CAREGIVER) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(80.dp),
        ) {
            Text(
                text = "Caregiver",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}
