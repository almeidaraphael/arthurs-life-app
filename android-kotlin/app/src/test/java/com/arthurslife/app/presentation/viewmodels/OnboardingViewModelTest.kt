package com.arthurslife.app.presentation.viewmodels

import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("OnboardingViewModel Tests")
class OnboardingViewModelTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var viewModel: OnboardingViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clearMocks(userRepository)
        viewModel = OnboardingViewModel(userRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("Initial State Tests")
    inner class InitialStateTests {

        @Test
        @DisplayName("should initialize with default state values")
        fun shouldInitializeWithDefaultStateValues() {
            // When
            val initialState = viewModel.uiState

            // Then
            assertEquals("", initialState.caregiverName)
            assertEquals(AvatarType.PREDEFINED, initialState.caregiverAvatarType)
            assertEquals("avatar_caregiver", initialState.caregiverAvatarData)
            assertEquals("", initialState.caregiverPin)
            assertTrue(initialState.children.isEmpty())
            assertFalse(initialState.isLoading)
            assertNull(initialState.error)
            assertFalse(initialState.isCompleted)
        }
    }

    @Nested
    @DisplayName("Caregiver Setup Tests")
    inner class CaregiverSetupTests {

        @Test
        @DisplayName("should update caregiver name successfully")
        fun shouldUpdateCaregiverNameSuccessfully() {
            // Given
            val testName = "John Doe"

            // When
            viewModel.updateCaregiverName(testName)

            // Then
            assertEquals(testName, viewModel.uiState.caregiverName)
        }

        @Test
        @DisplayName("should update caregiver avatar successfully")
        fun shouldUpdateCaregiverAvatarSuccessfully() {
            // Given
            val avatarType = AvatarType.CUSTOM
            val avatarData = "custom_avatar_base64"

            // When
            viewModel.updateCaregiverAvatar(avatarType, avatarData)

            // Then
            assertEquals(avatarType, viewModel.uiState.caregiverAvatarType)
            assertEquals(avatarData, viewModel.uiState.caregiverAvatarData)
        }

        @Test
        @DisplayName("should update caregiver PIN successfully")
        fun shouldUpdateCaregiverPinSuccessfully() {
            // Given
            val testPin = "9176"

            // When
            viewModel.updateCaregiverPin(testPin)

            // Then
            assertEquals(testPin, viewModel.uiState.caregiverPin)
        }

        @Test
        @DisplayName("should validate caregiver setup with valid data")
        fun shouldValidateCaregiverSetupWithValidData() {
            // Given
            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")

            // When
            val isValid = viewModel.validateCaregiverSetup()

            // Then
            assertTrue(isValid)
        }

        @Test
        @DisplayName("should fail validation with empty name")
        fun shouldFailValidationWithEmptyName() {
            // Given
            viewModel.updateCaregiverName("")
            viewModel.updateCaregiverPin("9176")

            // When
            val isValid = viewModel.validateCaregiverSetup()

            // Then
            assertFalse(isValid)
        }

        @Test
        @DisplayName("should fail validation with short PIN")
        fun shouldFailValidationWithShortPin() {
            // Given
            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("12")

            // When
            val isValid = viewModel.validateCaregiverSetup()

            // Then
            assertFalse(isValid)
        }

        @Test
        @DisplayName("should fail validation with blank name containing only whitespace")
        fun shouldFailValidationWithBlankName() {
            // Given
            viewModel.updateCaregiverName("   ")
            viewModel.updateCaregiverPin("9176")

            // When
            val isValid = viewModel.validateCaregiverSetup()

            // Then
            assertFalse(isValid)
        }
    }

    @Nested
    @DisplayName("Child Management Tests")
    inner class ChildManagementTests {

        @Test
        @DisplayName("should add child successfully")
        fun shouldAddChildSuccessfully() {
            // Given
            val childName = "Alice"
            val avatarType = AvatarType.PREDEFINED
            val avatarData = "mario_child"

            // When
            viewModel.addChild(childName, avatarType, avatarData)

            // Then
            assertEquals(1, viewModel.uiState.children.size)
            val addedChild = viewModel.uiState.children.first()
            assertEquals(childName, addedChild.name)
            assertEquals(avatarType, addedChild.avatarType)
            assertEquals(avatarData, addedChild.avatarData)
        }

        @Test
        @DisplayName("should remove child successfully")
        fun shouldRemoveChildSuccessfully() {
            // Given
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")
            viewModel.addChild("Bob", AvatarType.PREDEFINED, "luigi_child")
            assertEquals(2, viewModel.uiState.children.size)

            // When
            viewModel.removeChild(0)

            // Then
            assertEquals(1, viewModel.uiState.children.size)
            assertEquals("Bob", viewModel.uiState.children.first().name)
        }

        @Test
        @DisplayName("should ignore remove child with invalid index")
        fun shouldIgnoreRemoveChildWithInvalidIndex() {
            // Given
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")
            assertEquals(1, viewModel.uiState.children.size)

            // When
            viewModel.removeChild(5) // Invalid index

            // Then
            assertEquals(1, viewModel.uiState.children.size)
            assertEquals("Alice", viewModel.uiState.children.first().name)
        }

        @Test
        @DisplayName("should update child name successfully")
        fun shouldUpdateChildNameSuccessfully() {
            // Given
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")
            val newName = "Alice Smith"

            // When
            viewModel.updateChildName(0, newName)

            // Then
            assertEquals(newName, viewModel.uiState.children.first().name)
        }

        @Test
        @DisplayName("should ignore update child name with invalid index")
        fun shouldIgnoreUpdateChildNameWithInvalidIndex() {
            // Given
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")
            val originalName = "Alice"

            // When
            viewModel.updateChildName(5, "New Name") // Invalid index

            // Then
            assertEquals(originalName, viewModel.uiState.children.first().name)
        }

        @Test
        @DisplayName("should update child avatar successfully")
        fun shouldUpdateChildAvatarSuccessfully() {
            // Given
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")
            val newAvatarType = AvatarType.CUSTOM
            val newAvatarData = "custom_avatar"

            // When
            viewModel.updateChildAvatar(0, newAvatarType, newAvatarData)

            // Then
            val updatedChild = viewModel.uiState.children.first()
            assertEquals(newAvatarType, updatedChild.avatarType)
            assertEquals(newAvatarData, updatedChild.avatarData)
        }

        @Test
        @DisplayName("should ignore update child avatar with invalid index")
        fun shouldIgnoreUpdateChildAvatarWithInvalidIndex() {
            // Given
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")
            val originalAvatarType = AvatarType.PREDEFINED
            val originalAvatarData = "mario_child"

            // When
            viewModel.updateChildAvatar(5, AvatarType.CUSTOM, "new_avatar") // Invalid index

            // Then
            val child = viewModel.uiState.children.first()
            assertEquals(originalAvatarType, child.avatarType)
            assertEquals(originalAvatarData, child.avatarData)
        }
    }

    @Nested
    @DisplayName("Onboarding Completion Tests")
    inner class OnboardingCompletionTests {

        @Test
        @DisplayName("should complete onboarding successfully with caregiver and children")
        fun shouldCompleteOnboardingSuccessfullyWithCaregiverAndChildren() = runTest {
            // Given
            val savedUsers = slot<List<User>>()
            coEvery { userRepository.saveUsers(capture(savedUsers)) } returns Unit
            coEvery { userRepository.getAllUsers() } returns emptyList()

            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176") // Secure PIN not in weak list
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")

            // When
            viewModel.completeOnboarding()

            // Then
            coVerify(exactly = 1) { userRepository.saveUsers(any()) }
            assertEquals(2, savedUsers.captured.size)

            // Find caregiver and child by role
            val savedCaregiver = savedUsers.captured.first { it.role == UserRole.CAREGIVER }
            val savedChild = savedUsers.captured.first { it.role == UserRole.CHILD }

            // Verify caregiver user
            assertEquals("John Doe", savedCaregiver.name)
            assertEquals(UserRole.CAREGIVER, savedCaregiver.role)
            assertEquals(TokenBalance.zero(), savedCaregiver.tokenBalance)
            assertNotNull(savedCaregiver.pin)
            assertEquals(AvatarType.PREDEFINED, savedCaregiver.avatarType)

            // Verify child user
            assertEquals("Alice", savedChild.name)
            assertEquals(UserRole.CHILD, savedChild.role)
            assertEquals(TokenBalance.zero(), savedChild.tokenBalance)
            assertNull(savedChild.pin)
            assertEquals(AvatarType.PREDEFINED, savedChild.avatarType)
            assertEquals("mario_child", savedChild.avatarData)

            // Verify final state
            assertFalse(viewModel.uiState.isLoading)
            assertTrue(viewModel.uiState.isCompleted)
            assertNull(viewModel.uiState.error)
        }

        @Test
        @DisplayName("should complete onboarding successfully with caregiver only")
        fun shouldCompleteOnboardingSuccessfullyWithCaregiverOnly() = runTest {
            // Given
            val savedUsers = slot<List<User>>()
            coEvery { userRepository.saveUsers(capture(savedUsers)) } returns Unit
            coEvery { userRepository.getAllUsers() } returns emptyList()

            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")
            // No children added

            // When
            viewModel.completeOnboarding()

            // Then
            coVerify(exactly = 1) { userRepository.saveUsers(any()) }

            val savedCaregiver = savedUsers.captured.first()
            assertEquals("John Doe", savedCaregiver.name)
            assertEquals(UserRole.CAREGIVER, savedCaregiver.role)
            assertNotNull(savedCaregiver.pin)

            assertTrue(viewModel.uiState.isCompleted)
            assertFalse(viewModel.uiState.isLoading)
        }

        @Test
        @DisplayName("should handle repository error during onboarding completion")
        fun shouldHandleRepositoryErrorDuringOnboardingCompletion() = runTest {
            // Given
            val errorMessage = "Database error"
            coEvery { userRepository.saveUsers(any()) } throws IllegalStateException(errorMessage)

            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")

            // When
            viewModel.completeOnboarding()

            // Then
            assertFalse(viewModel.uiState.isLoading)
            assertFalse(viewModel.uiState.isCompleted)
            assertTrue(viewModel.uiState.error!!.contains("Failed to create family"))
        }

        @Test
        @DisplayName("should handle security error during onboarding completion")
        fun shouldHandleSecurityErrorDuringOnboardingCompletion() = runTest {
            // Given
            coEvery { userRepository.saveUsers(any()) } throws SecurityException("Security violation")

            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")

            // When
            viewModel.completeOnboarding()

            // Then
            assertFalse(viewModel.uiState.isLoading)
            assertFalse(viewModel.uiState.isCompleted)
            assertEquals("Failed to create family: Security error", viewModel.uiState.error)
        }

        @Test
        @DisplayName("should create caregiver without PIN when PIN is empty")
        fun shouldCreateCaregiverWithoutPinWhenPinIsEmpty() = runTest {
            // Given
            val savedUsers = slot<List<User>>()
            coEvery { userRepository.saveUsers(capture(savedUsers)) } returns Unit
            coEvery { userRepository.getAllUsers() } returns emptyList()

            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("") // Empty PIN

            // When
            viewModel.completeOnboarding()

            // Then
            val savedCaregiver = savedUsers.captured.first()
            assertEquals("John Doe", savedCaregiver.name)
            assertNull(savedCaregiver.pin)
        }

        @Test
        @DisplayName("should set loading state during onboarding completion")
        fun shouldSetLoadingStateDuringOnboardingCompletion() = runTest {
            // Given
            coEvery { userRepository.saveUsers(any()) } returns Unit
            coEvery { userRepository.getAllUsers() } returns emptyList()
            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")

            // When
            viewModel.completeOnboarding()

            // Then - loading state should be false after completion
            assertFalse(viewModel.uiState.isLoading)
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("should clear error successfully")
        fun shouldClearErrorSuccessfully() = runTest {
            // Given - simulate an error state
            coEvery { userRepository.saveUsers(any()) } throws IllegalStateException("Test error")
            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")

            viewModel.completeOnboarding()

            assertNotNull(viewModel.uiState.error)

            // When
            viewModel.clearError()

            // Then
            assertNull(viewModel.uiState.error)
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    inner class EdgeCaseTests {

        @Test
        @DisplayName("should handle multiple children correctly")
        fun shouldHandleMultipleChildrenCorrectly() = runTest {
            // Given
            val savedUsers = slot<List<User>>()
            coEvery { userRepository.saveUsers(capture(savedUsers)) } returns Unit
            coEvery { userRepository.getAllUsers() } returns emptyList()

            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")
            viewModel.addChild("Alice", AvatarType.PREDEFINED, "mario_child")
            viewModel.addChild("Bob", AvatarType.PREDEFINED, "luigi_child")

            // When
            viewModel.completeOnboarding()

            // Then
            assertEquals(3, savedUsers.captured.size) // 1 caregiver + 2 children

            val caregiver = savedUsers.captured.find { it.role == UserRole.CAREGIVER }
            val children = savedUsers.captured.filter { it.role == UserRole.CHILD }

            assertNotNull(caregiver)
            assertEquals(2, children.size)
            assertEquals("Alice", children.find { it.avatarData == "mario_child" }?.name)
            assertEquals("Bob", children.find { it.avatarData == "luigi_child" }?.name)
        }

        @Test
        @DisplayName("should handle child name with leading and trailing whitespace during save")
        fun shouldHandleChildNameWithWhitespaceDuringSave() = runTest {
            // Given
            val savedUsers = slot<List<User>>()
            coEvery { userRepository.saveUsers(capture(savedUsers)) } returns Unit
            coEvery { userRepository.getAllUsers() } returns emptyList()

            viewModel.updateCaregiverName("John Doe")
            viewModel.updateCaregiverPin("9176")

            // Add child with name that should be trimmed when saved
            viewModel.addChild("  Alice  ", AvatarType.PREDEFINED, "mario_child")

            // When
            viewModel.completeOnboarding()

            // Then
            val savedChild = savedUsers.captured.find { it.role == UserRole.CHILD }
            assertEquals(
                "  Alice  ",
                savedChild?.name,
            ) // Currently saves as-is, this documents current behavior
        }
    }
}
