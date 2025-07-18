package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.presentation.theme.BaseAppTheme

@Composable
fun ThemeAwareTokenCard(
    theme: BaseAppTheme,
    tokenBalance: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.primaryContainer,
        ),
        shape = theme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Token Balance",
                    style = MaterialTheme.typography.titleMedium,
                    color = theme.colorScheme.onPrimaryContainer,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ThemeAwareIcon(
                        semanticType = SemanticIconType.TOKEN,
                        theme = theme,
                        modifier = Modifier.size(24.dp),
                        tint = if (theme.useOriginalIconColors) null else theme.colorScheme.primary,
                        contentDescription = theme.displayName,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tokenBalance.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = theme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            // Character avatar
            ThemeAwareAvatar(
                theme = theme,
                size = 60.dp,
            )
        }
    }
}
