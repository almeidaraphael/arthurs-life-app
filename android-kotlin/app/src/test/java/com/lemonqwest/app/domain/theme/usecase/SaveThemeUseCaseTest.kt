package com.lemonqwest.app.domain.theme.usecase

import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.theme.repository.ThemeRepository
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("SaveThemeUseCase Tests")
@Execution(ExecutionMode.SAME_THREAD)
class SaveThemeUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var themeRepository: ThemeRepository
    private lateinit var saveThemeUseCase: SaveThemeUseCase

    @BeforeEach
    fun setUp() {
        themeRepository = mockk()
        saveThemeUseCase = SaveThemeUseCase(themeRepository)
        // Set up default mock behavior
        coEvery { themeRepository.saveTheme(any(), any()) } returns Unit
    }

    @Test
    @DisplayName("Should save theme for user by ID")
    fun shouldSaveThemeForUserById() = runTest {
        // Given
        val userId = "child-user-123"
        val theme = AppTheme.MARIO_CLASSIC
        coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

        // When
        saveThemeUseCase(userId, theme)

        // Then
        coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
    }

    @Test
    @DisplayName("Should save theme for different user ID")
    fun shouldSaveThemeForDifferentUserId() = runTest {
        // Given
        val userId = "caregiver-user-456"
        val theme = AppTheme.MATERIAL_DARK
        coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

        // When
        saveThemeUseCase(userId, theme)

        // Then
        coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
    }

    @Test
    @DisplayName("Should handle saving all available themes")
    fun shouldHandleSavingAllAvailableThemes() = runTest {
        // Given
        val userId = "child-user-123"
        val allThemes = AppTheme.values().toList()

        allThemes.forEach { theme ->
            coEvery { themeRepository.saveTheme(userId, theme) } returns Unit
        }

        // When
        allThemes.forEach { theme ->
            saveThemeUseCase(userId, theme)
        }

        // Then
        allThemes.forEach { theme ->
            coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
        }
    }

    @Test
    @DisplayName("Should handle multiple saves for same user role")
    fun shouldHandleMultipleSavesForSameUserRole() = runTest {
        // Given
        val userId = "caregiver-user-456"
        val themes = listOf(
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
            AppTheme.MARIO_CLASSIC,
        )

        themes.forEach { theme ->
            coEvery { themeRepository.saveTheme(userId, theme) } returns Unit
        }

        // When
        themes.forEach { theme ->
            saveThemeUseCase(userId, theme)
        }

        // Then
        themes.forEach { theme ->
            coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
        }
    }

    @Test
    @DisplayName("Should delegate to repository correctly")
    fun shouldDelegateToRepositoryCorrectly() = runTest {
        // Given
        val userId = "child-user-123"
        val theme = AppTheme.MARIO_CLASSIC
        coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

        // When
        saveThemeUseCase(userId, theme)

        // Then
        coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
    }

    @Test
    @DisplayName("Should support operator invoke syntax")
    fun shouldSupportOperatorInvokeSyntax() = runTest {
        // Given
        val userId = "caregiver-user-456"
        val theme = AppTheme.MATERIAL_LIGHT
        coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

        // When
        saveThemeUseCase.invoke(userId, theme)

        // Then
        coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
    }

    @Test
    @DisplayName("Should handle theme changes for different users")
    fun shouldHandleThemeChangesForDifferentUsers() = runTest {
        // Given
        val scenarios = listOf(
            "child-user-1" to AppTheme.MARIO_CLASSIC,
            "child-user-2" to AppTheme.MATERIAL_LIGHT,
            "caregiver-user-1" to AppTheme.MATERIAL_DARK,
            "caregiver-user-2" to AppTheme.MARIO_CLASSIC,
        )

        scenarios.forEach { (userId, theme) ->
            coEvery { themeRepository.saveTheme(userId, theme) } returns Unit
        }

        // When
        scenarios.forEach { (userId, theme) ->
            saveThemeUseCase(userId, theme)
        }

        // Then
        scenarios.forEach { (userId, theme) ->
            coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
        }
    }
}
