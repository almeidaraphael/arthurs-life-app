package com.lemonqwest.app.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.ThemeManager
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.presentation.theme.components.SemanticIconType
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIcon
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIconButton
import com.lemonqwest.app.presentation.theme.components.ThemeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val availableThemes by themeViewModel.availableThemes.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ThemeSettingsTopBar(
                currentTheme = currentTheme,
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        ThemeSettingsContent(
            currentTheme = currentTheme,
            availableThemes = availableThemes,
            paddingValues = paddingValues,
            onThemeSelected = { theme ->
                ThemeManager.getAppThemeKey(theme)?.let { appTheme ->
                    themeViewModel.saveTheme(appTheme)
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSettingsTopBar(
    currentTheme: BaseAppTheme,
    onBackClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Theme & Display",
                style = currentTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.onSurface,
            )
        },
        navigationIcon = {
            ThemeAwareIconButton(
                onClick = onBackClick,
            ) {
                ThemeAwareIcon(
                    semanticType = SemanticIconType.BACK_ARROW,
                    theme = currentTheme,
                    contentDescription = "Back",
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = currentTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun ThemeSettingsContent(
    currentTheme: BaseAppTheme,
    availableThemes: List<BaseAppTheme>,
    paddingValues: PaddingValues,
    onThemeSelected: (BaseAppTheme) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ThemeSelector(
                currentTheme = currentTheme,
                availableThemes = availableThemes,
                onThemeSelected = onThemeSelected,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
