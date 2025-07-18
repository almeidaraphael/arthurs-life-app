package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareProgressCard(
    theme: BaseAppTheme,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val progressPercentage = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.secondaryContainer,
        ),
        shape = theme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = theme.progressTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onSecondaryContainer,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ThemeAwareProgressIndicator(
                progress = progress,
                theme = theme,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$progressPercentage% complete",
                style = MaterialTheme.typography.bodyMedium,
                color = theme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
