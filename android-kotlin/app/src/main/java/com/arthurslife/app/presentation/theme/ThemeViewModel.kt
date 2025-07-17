package com.arthurslife.app.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.theme.usecase.GetAvailableThemesUseCase
import com.arthurslife.app.domain.theme.usecase.GetThemeUseCase
import com.arthurslife.app.domain.theme.usecase.SaveThemeUseCase
import com.arthurslife.app.domain.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    private val getAvailableThemesUseCase: GetAvailableThemesUseCase,
) : ViewModel() {

    private val _currentTheme = MutableStateFlow(AppTheme.MATERIAL_LIGHT)
    val currentTheme: StateFlow<BaseAppTheme> = _currentTheme.asStateFlow().map {
        ThemeManager.getTheme(
            it,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ThemeManager.getTheme(_currentTheme.value),
    )

    val currentAppTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _availableThemes = MutableStateFlow<List<AppTheme>>(emptyList())
    val availableThemes: StateFlow<List<BaseAppTheme>> = _availableThemes.asStateFlow().map {
        it.map { ThemeManager.getTheme(it) }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

    private var currentUserRole: UserRole? = null

    init {
        loadAvailableThemes()
    }

    fun loadTheme(userRole: UserRole) {
        currentUserRole = userRole

        // Immediately set the default theme for this user role
        val defaultTheme = when (userRole) {
            UserRole.CHILD -> AppTheme.MATERIAL_LIGHT
            UserRole.CAREGIVER -> AppTheme.MATERIAL_LIGHT
        }
        _currentTheme.value = defaultTheme

        // Then load the saved preference (which may override the default)
        viewModelScope.launch {
            getThemeUseCase(userRole).collect { theme ->
                _currentTheme.value = theme
            }
        }
    }

    fun saveTheme(theme: AppTheme) {
        currentUserRole?.let { userRole ->
            viewModelScope.launch {
                saveThemeUseCase(userRole, theme)
                _currentTheme.value = theme
            }
        }
    }

    private fun loadAvailableThemes() {
        _availableThemes.value = getAvailableThemesUseCase()
    }
}
