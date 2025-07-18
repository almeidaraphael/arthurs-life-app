package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.PresentationException
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for AchievementViewModel initialization scenarios.
 *
 * Tests cover:
 * - Default state initialization
 * - Child user and achievement loading on init
 * - Missing child user handling
 * - Child user loading error handling
 * - Achievement loading error handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AchievementViewModel Initialization Tests")
class AchievementViewModelInitializationTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAchievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var mockAchievementRepository: AchievementRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockAchievementEventManager: AchievementEventManager

    private val testChild = TestDataFactory.createChildUser(id = "child-1", name = "Test Child")
    private val testAchievements = listOf(
        TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            userId = "child-1",
            isUnlocked = true,
        ),
        TestDataFactory.createAchievement(
            type = AchievementType.CENTURY_CLUB,
            userId = "child-1",
            isUnlocked = false,
        ),
        TestDataFactory.createAchievement(
            type = AchievementType.THREE_DAY_STREAK,
            userId = "child-1",
            isUnlocked = true,
        ),
    )

    @BeforeEach
    fun setUpAchievementViewModelInitialization() {
        MockKAnnotations.init(this, relaxUnitFun = false)
        mockAchievementTrackingUseCase = mockk()
        mockAchievementRepository = mockk()
        mockUserRepository = mockk()
        mockAchievementEventManager = mockk {
            every { achievementUpdates } returns MutableSharedFlow()
        }
        coEvery { mockUserRepository.findByRole(UserRole.CHILD) } returns testChild
        coEvery { mockAchievementRepository.initializeAchievementsForUser(testChild.id) } returns Unit
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns testAchievements
        coEvery { mockUserRepository.findById(any()) } returns testChild
    }

    @AfterEach
    fun tearDownAchievementViewModelInitialization() {
        unmockkAll()
    }

    private fun createViewModel(): AchievementViewModel {
        return AchievementViewModel(
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            achievementRepository = mockAchievementRepository,
            userRepository = mockUserRepository,
            achievementEventManager = mockAchievementEventManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )
    }

    @Test
    fun `should initialize with correct default state`() {
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertEquals(false, uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `should load child user and achievements on initialization`() {
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        coVerify { mockUserRepository.findByRole(UserRole.CHILD) }
        coVerify { mockAchievementRepository.initializeAchievementsForUser(testChild.id) }
        coVerify { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) }
        assertEquals(testAchievements, viewModel.achievements.first())
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `should handle missing child user gracefully`() {
        coEvery { mockUserRepository.findByRole(UserRole.CHILD) } returns null
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        coVerify(exactly = 0) { mockAchievementRepository.initializeAchievementsForUser(any()) }
        coVerify(exactly = 0) { mockAchievementTrackingUseCase.getAllAchievements(any()) }
        val uiState = viewModel.uiState.first()
        assertEquals("No child user found", uiState.error)
        assertFalse(uiState.isLoading)
        assertTrue(viewModel.achievements.first().isEmpty())
    }

    @Test
    fun `should handle child user loading error`() {
        val errorMessage = "Database connection failed"
        coEvery { mockUserRepository.findByRole(UserRole.CHILD) } throws PresentationException(errorMessage)
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertEquals(errorMessage, uiState.error)
        assertFalse(uiState.isLoading)
        assertTrue(viewModel.achievements.first().isEmpty())
    }

    @Test
    fun `should handle achievement loading error`() {
        val errorMessage = "Failed to load achievements"
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException(errorMessage)
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertEquals(errorMessage, uiState.error)
        assertFalse(uiState.isLoading)
        assertTrue(viewModel.achievements.first().isEmpty())
    }
}
