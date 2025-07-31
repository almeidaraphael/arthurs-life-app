package com.lemonqwest.app.presentation.screens.onboarding

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.R
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.LocalBaseTheme
import com.lemonqwest.app.presentation.viewmodels.ChildSetupData
import com.lemonqwest.app.presentation.viewmodels.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingCompleteScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit,
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Setup Complete!") },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (uiState.isLoading) {
                LoadingContent()
            } else {
                CompleteContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    onComplete = onComplete,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    val theme = LocalBaseTheme.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Setting up your family...",
            style = MaterialTheme.typography.bodyLarge,
            color = theme.textColors.primary,
        )
    }
}

@Composable
private fun CompleteContent(
    uiState: com.lemonqwest.app.presentation.viewmodels.OnboardingUiState,
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { SuccessHeader() }
        item {
            FamilySummaryCard(
                caregiverName = uiState.caregiverName,
                caregiverAvatarData = uiState.caregiverAvatarData,
                children = uiState.children,
            )
        }
        item { NextStepsCard() }
        item { ErrorCard(uiState.error) }
        item {
            GetStartedButton(
                isLoading = uiState.isLoading,
                onGetStarted = {
                    viewModel.completeOnboarding()
                    onComplete()
                },
            )
        }
    }
}

@Composable
private fun SuccessHeader() {
    val theme = LocalBaseTheme.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to LemonQwest!",
            style = MaterialTheme.typography.headlineMedium,
            color = theme.textColors.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your family is all set up and ready to start earning tokens and completing tasks together!",
            style = MaterialTheme.typography.bodyLarge,
            color = theme.textColors.secondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun NextStepsCard() {
    val theme = LocalBaseTheme.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.containerColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "What's Next?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )

            Spacer(modifier = Modifier.height(12.dp))

            NextStepItem("📝 Create your first tasks for the children")
            NextStepItem("🏆 Set up rewards and token values")
            NextStepItem("🎯 Watch your children complete tasks and earn tokens")
            NextStepItem("📊 Track progress and celebrate achievements")
        }
    }
}

@Composable
private fun ErrorCard(error: String?) {
    val theme = LocalBaseTheme.current

    error?.let {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = theme.textColors.error.copy(alpha = 0.1f),
            ),
        ) {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = theme.textColors.error,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun GetStartedButton(
    isLoading: Boolean,
    onGetStarted: () -> Unit,
) {
    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onGetStarted,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading,
    ) {
        Text(
            text = "Enter LemonQwest",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun FamilySummaryCard(
    caregiverName: String,
    caregiverAvatarData: String,
    children: List<ChildSetupData>,
) {
    val theme = LocalBaseTheme.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.containerColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Your Family",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textColors.primary,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Caregiver
            FamilyMemberRow(
                name = caregiverName,
                role = "Caregiver",
                avatarData = caregiverAvatarData,
            )

            if (children.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Children (${children.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = theme.textColors.secondary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                children.forEach { child ->
                    FamilyMemberRow(
                        name = child.name,
                        role = "Child",
                        avatarData = child.avatarData,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FamilyMemberRow(
    name: String,
    role: String,
    avatarData: String,
) {
    val theme = LocalBaseTheme.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FamilyMemberAvatar(name = name, avatarData = avatarData, theme = theme)
        Spacer(modifier = Modifier.size(FAMILY_MEMBER_SPACER_DP.dp))
        FamilyMemberInfo(name = name, role = role, theme = theme)
    }
}

private const val FAMILY_MEMBER_SPACER_DP = 12

@Composable
private fun FamilyMemberAvatar(name: String, avatarData: String, theme: BaseAppTheme) {
    val isCustomAvatar = avatarData.startsWith("data:image/") && avatarData.contains("base64,")
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(theme.containerColors.primary.copy(alpha = 0.1f))
            .border(
                width = 2.dp,
                color = theme.containerColors.primary,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isCustomAvatar) {
            val base64Data = avatarData.substringAfter("base64,")
            val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val imageBitmap: ImageBitmap? = bitmap?.asImageBitmap()
            imageBitmap?.let {
                androidx.compose.foundation.Image(
                    bitmap = it,
                    contentDescription = "$name's avatar",
                    modifier = Modifier.size(32.dp),
                )
            }
        } else {
            val resourceId = when (avatarData) {
                "mario_child" -> R.drawable.avatar_mario
                "luigi_child" -> R.drawable.avatar_luigi
                "peach_child" -> R.drawable.avatar_peach
                "toad_child" -> R.drawable.avatar_toad
                "koopa_child" -> R.drawable.avatar_koopa
                "goomba_child" -> R.drawable.avatar_goomba
                "star_child" -> R.drawable.avatar_star
                "mushroom_child" -> R.drawable.avatar_mushroom
                "avatar_caregiver", "default_caregiver" -> R.drawable.avatar_caregiver
                else -> R.drawable.default_avatar
            }
            Icon(
                painter = painterResource(resourceId),
                contentDescription = "$name's avatar",
                modifier = Modifier.size(32.dp),
                tint = androidx.compose.ui.graphics.Color.Unspecified,
            )
        }
    }
}

@Composable
private fun FamilyMemberInfo(name: String, role: String, theme: BaseAppTheme) {
    Column {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = theme.textColors.primary,
            ),
        )
        Text(
            text = role,
            style = MaterialTheme.typography.bodySmall,
            color = theme.textColors.secondary,
        )
    }
}

@Composable
private fun NextStepItem(text: String) {
    val theme = LocalBaseTheme.current

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = theme.textColors.secondary,
        modifier = Modifier.padding(vertical = 2.dp),
    )
}
