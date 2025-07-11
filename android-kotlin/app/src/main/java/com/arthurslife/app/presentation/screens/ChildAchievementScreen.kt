package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.presentation.viewmodels.AchievementUiState
import com.arthurslife.app.presentation.viewmodels.AchievementViewModel

/**
 * Child achievement viewing screen for Arthur's Life MVP.
 *
 * This screen allows children to:
 * - View all their achievements with progress
 * - See which achievements are unlocked vs locked
 * - Understand their progress toward unlocking new achievements
 * - Celebrate their accomplishments
 */
@Composable
fun ChildAchievementScreen(
    viewModel: AchievementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    // Refresh achievements whenever this screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold { paddingValues ->
        achievementScreenContent(
            uiState = uiState,
            achievements = achievements,
            paddingValues = paddingValues,
        )
    }
}

@Composable
private fun achievementScreenContent(
    uiState: AchievementUiState,
    achievements: List<Achievement>,
    paddingValues: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            uiState.error != null -> {
                achievementErrorState(
                    error = uiState.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            else -> {
                achievementsList(achievements = achievements)
            }
        }
    }
}

@Composable
private fun achievementErrorState(
    error: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Card component for displaying individual achievements.
 */
@Composable
private fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (achievement.isUnlocked) 4.dp else 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            achievementIcon(achievement)
            achievementInfo(
                achievement = achievement,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun achievementIcon(achievement: Achievement) {
    Icon(
        imageVector = if (achievement.isUnlocked) {
            Icons.Default.EmojiEvents
        } else {
            Icons.Default.Lock
        },
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        tint = if (achievement.isUnlocked) {
            MaterialTheme.colorScheme.secondary // Achievement highlight color
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
    )
}

@Composable
private fun achievementInfo(achievement: Achievement, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = achievement.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = achievement.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        achievementProgress(achievement)
        achievementStatus(achievement)
    }
}

@Composable
private fun achievementProgress(achievement: Achievement) {
    // Progress indicator for locked achievements
    if (!achievement.isUnlocked && achievement.target > 1) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${achievement.progress}/${achievement.target}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { achievement.progressPercentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
private fun achievementStatus(achievement: Achievement) {
    // Unlocked status
    if (achievement.isUnlocked) {
        Text(
            text = "🎉 Unlocked!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
private fun achievementsList(achievements: List<Achievement>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Your Achievements",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                val unlockedCount = achievements.count { it.isUnlocked }
                Text(
                    text = "$unlockedCount of ${achievements.size} unlocked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Achievement Cards or Empty state
        if (achievements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "🏆",
                            style = MaterialTheme.typography.displayLarge,
                        )
                        Text(
                            text = "No achievements yet!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Complete tasks to unlock your first achievement.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        } else {
            items(achievements) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}
