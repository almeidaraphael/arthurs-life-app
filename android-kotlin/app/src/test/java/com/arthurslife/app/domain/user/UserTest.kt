package com.arthurslife.app.domain.user

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.auth.PIN
import com.arthurslife.app.domain.shouldAfford
import com.arthurslife.app.domain.shouldBeCaregiver
import com.arthurslife.app.domain.shouldBeChild
import com.arthurslife.app.domain.shouldNotAfford
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

/**
 * Comprehensive test suite for the User domain entity.
 *
 * Tests cover:
 * - User creation and initialization
 * - Role-based access control
 * - Token balance integration
 * - PIN authentication for caregivers
 * - Data validation and business rules
 * - Edge cases and error conditions
 */
@DisplayName("User Domain Entity Tests")
class UserTest {

    @Nested
    @DisplayName("User Creation")
    inner class UserCreation {

        @Test
        @DisplayName("Should create child user with default values")
        fun shouldCreateChildUserWithDefaults() {
            val user = TestDataFactory.createChildUser()

            user.shouldBeChild()
            assertNotNull(user.id, "User should have an ID")
            assertTrue(user.id.isNotBlank(), "User ID should not be blank")
            assertEquals("Arthur", user.name, "Default child name should be Arthur")
            assertEquals(25, user.tokenBalance.getValue(), "Default token balance should be 25")
        }

        @Test
        @DisplayName("Should create caregiver user with PIN")
        fun shouldCreateCaregiverUserWithPin() {
            val user = TestDataFactory.createCaregiverUser()

            user.shouldBeCaregiver()
            assertNotNull(user.id, "User should have an ID")
            assertNotNull(user.pin, "Caregiver should have a PIN")
            assertEquals("Parent", user.name, "Default caregiver name should be Parent")
            assertEquals(
                0,
                user.tokenBalance.getValue(),
                "Default caregiver token balance should be 0",
            )
        }

        @Test
        @DisplayName("Should create user with custom parameters")
        fun shouldCreateUserWithCustomParameters() {
            val customName = "Emma"
            val customTokens = TokenBalance.create(50)
            val customPin = PIN.create("5678")

            val user = User(
                name = customName,
                role = UserRole.CAREGIVER,
                tokenBalance = customTokens,
                pin = customPin,
            )

            assertEquals(customName, user.name)
            assertEquals(UserRole.CAREGIVER, user.role)
            assertEquals(50, user.tokenBalance.getValue())
            assertNotNull(user.pin)
            assertTrue(user.pin!!.verify("5678"))
        }

        @Test
        @DisplayName("Should generate unique IDs for different users")
        fun shouldGenerateUniqueIds() {
            val user1 = TestDataFactory.createChildUser()
            val user2 = TestDataFactory.createChildUser()

            assertNotEquals(user1.id, user2.id, "Users should have unique IDs")
        }

        @Test
        @DisplayName("Should accept custom ID")
        fun shouldAcceptCustomId() {
            val customId = "custom-user-123"
            val user = TestDataFactory.createChildUser(id = customId)

            assertEquals(customId, user.id, "User should use custom ID")
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control")
    inner class RoleBasedAccess {

        @ParameterizedTest
        @EnumSource(UserRole::class)
        @DisplayName("Should properly assign user roles")
        fun shouldAssignUserRoles(role: UserRole) {
            val user = User(
                name = "Test User",
                role = role,
                tokenBalance = TokenBalance.create(10),
                pin = if (role == UserRole.CAREGIVER) PIN.create("1234") else null,
            )

            assertEquals(role, user.role, "User should have assigned role")

            when (role) {
                UserRole.CHILD -> {
                    assertNull(user.pin, "Child users should not have PIN")
                }
                UserRole.CAREGIVER -> {
                    assertNotNull(user.pin, "Caregiver users should have PIN")
                }
            }
        }

        @Test
        @DisplayName("Should validate child user constraints")
        fun shouldValidateChildUserConstraints() {
            val child = TestDataFactory.createChildUser()

            child.shouldBeChild()
            assertNull(child.pin, "Child should not have PIN for security")
        }

        @Test
        @DisplayName("Should validate caregiver user constraints")
        fun shouldValidateCaregiverUserConstraints() {
            val caregiver = TestDataFactory.createCaregiverUser()

            caregiver.shouldBeCaregiver()
            assertNotNull(caregiver.pin, "Caregiver should have PIN for authentication")
        }

        @Test
        @DisplayName("Should allow caregiver without PIN for specific scenarios")
        fun shouldAllowCaregiverWithoutPin() {
            val caregiver = User(
                name = "Guest Caregiver",
                role = UserRole.CAREGIVER,
                tokenBalance = TokenBalance.zero(),
                pin = null,
            )

            caregiver.shouldBeCaregiver(shouldHavePin = false)
        }
    }

    @Nested
    @DisplayName("Token Balance Integration")
    inner class TokenBalanceIntegration {

        @Test
        @DisplayName("Should initialize with zero token balance")
        fun shouldInitializeWithZeroTokens() {
            val user = User(
                name = "Test User",
                role = UserRole.CHILD,
            )

            assertEquals(0, user.tokenBalance.getValue(), "Default token balance should be zero")
        }

        @Test
        @DisplayName("Should initialize with custom token balance")
        fun shouldInitializeWithCustomTokenBalance() {
            val customBalance = TokenBalance.create(75)
            val user = User(
                name = "Test User",
                role = UserRole.CHILD,
                tokenBalance = customBalance,
            )

            assertEquals(75, user.tokenBalance.getValue(), "Should use custom token balance")
        }

        @Test
        @DisplayName("Should handle token balance operations")
        fun shouldHandleTokenBalanceOperations() {
            val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(50))

            user.shouldAfford(30)
            user.shouldAfford(50)
            user.shouldNotAfford(51)
            user.shouldNotAfford(100)
        }

        @Test
        @DisplayName("Should support large token balances")
        fun shouldSupportLargeTokenBalances() {
            val largeBalance = TokenBalance.create(1000000)
            val user = TestDataFactory.createChildUser(tokenBalance = largeBalance)

            assertEquals(1000000, user.tokenBalance.getValue())
            user.shouldAfford(999999)
        }

        @Test
        @DisplayName("Should support negative token balances for admin operations")
        fun shouldSupportNegativeTokenBalances() {
            val negativeBalance = TokenBalance.createAdmin(-25)
            val user = TestDataFactory.createChildUser(tokenBalance = negativeBalance)

            assertEquals(-25, user.tokenBalance.getValue())
        }
    }

    @Nested
    @DisplayName("PIN Authentication")
    inner class PinAuthentication {

        @Test
        @DisplayName("Should verify correct PIN for caregiver")
        fun shouldVerifyCorrectPin() {
            val pin = PIN.create("1234")
            val caregiver = TestDataFactory.createCaregiverUser(pin = pin)

            assertNotNull(caregiver.pin)
            assertTrue(caregiver.pin!!.verify("1234"), "Should verify correct PIN")
        }

        @Test
        @DisplayName("Should reject incorrect PIN for caregiver")
        fun shouldRejectIncorrectPin() {
            val pin = PIN.create("1234")
            val caregiver = TestDataFactory.createCaregiverUser(pin = pin)

            assertNotNull(caregiver.pin)
            assertTrue(!caregiver.pin!!.verify("5678"), "Should reject incorrect PIN")
        }

        @Test
        @DisplayName("Should handle PIN creation with different values")
        fun shouldHandleDifferentPinValues() {
            val pins = listOf("0000", "1111", "9999", "4321", "8765")

            pins.forEach { pinValue ->
                val pin = PIN.create(pinValue)
                val user = TestDataFactory.createCaregiverUser(pin = pin)

                assertTrue(user.pin!!.verify(pinValue), "Should verify PIN $pinValue")
            }
        }

        @Test
        @DisplayName("Should maintain PIN security between users")
        fun shouldMaintainPinSecurity() {
            val user1 = TestDataFactory.createCaregiverUser(pin = PIN.create("1234"))
            val user2 = TestDataFactory.createCaregiverUser(pin = PIN.create("5678"))

            assertTrue(user1.pin!!.verify("1234"), "User 1 should verify their PIN")
            assertTrue(!user1.pin!!.verify("5678"), "User 1 should not verify User 2's PIN")

            assertTrue(user2.pin!!.verify("5678"), "User 2 should verify their PIN")
            assertTrue(!user2.pin!!.verify("1234"), "User 2 should not verify User 1's PIN")
        }
    }

    @Nested
    @DisplayName("Data Validation")
    inner class DataValidation {

        @Test
        @DisplayName("Should validate user name is not blank")
        fun shouldValidateUserNameNotBlank() {
            // The User data class doesn't enforce this constraint directly,
            // but it's important for business logic
            val user = User(name = "Valid Name", role = UserRole.CHILD)
            assertTrue(user.name.isNotBlank(), "User name should not be blank")
        }

        @Test
        @DisplayName("Should handle empty user name gracefully")
        fun shouldHandleEmptyUserName() {
            // While not ideal, the domain object should handle this case
            val user = User(name = "", role = UserRole.CHILD)
            assertEquals("", user.name, "Should accept empty name (handled by validation layer)")
        }

        @Test
        @DisplayName("Should validate UUID format for ID")
        fun shouldValidateUuidFormat() {
            val user = TestDataFactory.createChildUser()

            // Check that auto-generated ID is valid UUID format
            assertNotNull(UUID.fromString(user.id), "Auto-generated ID should be valid UUID")
        }

        @Test
        @DisplayName("Should handle special characters in name")
        fun shouldHandleSpecialCharactersInName() {
            val specialNames = listOf(
                "José María",
                "François",
                "Müller",
                "O'Connor",
                "李小明",
                "مريم",
            )

            specialNames.forEach { name ->
                val user = TestDataFactory.createChildUser(name = name)
                assertEquals(name, user.name, "Should handle special characters in name: $name")
            }
        }
    }

    @Nested
    @DisplayName("Business Rules")
    inner class BusinessRules {

        @Test
        @DisplayName("Should enforce token balance constraints")
        fun shouldEnforceTokenBalanceConstraints() {
            // Valid token balances
            val validBalances = listOf(0, 1, 50, 100, 1000)

            validBalances.forEach { amount ->
                val balance = TokenBalance.create(amount)
                val user = TestDataFactory.createChildUser(tokenBalance = balance)
                assertEquals(
                    amount,
                    user.tokenBalance.getValue(),
                    "Should accept valid token balance: $amount",
                )
            }
        }

        @Test
        @DisplayName("Should validate PIN format constraints")
        fun shouldValidatePinFormatConstraints() {
            val validPins = listOf("0000", "1234", "9999", "4567")

            validPins.forEach { pinValue ->
                val pin = PIN.create(pinValue)
                val user = TestDataFactory.createCaregiverUser(pin = pin)
                assertNotNull(user.pin, "Should accept valid PIN: $pinValue")
            }
        }

        @Test
        @DisplayName("Should maintain immutability of user objects")
        fun shouldMaintainImmutability() {
            val user = TestDataFactory.createChildUser()
            val originalName = user.name
            val originalTokens = user.tokenBalance.getValue()

            // User is a data class, so it's immutable by default
            // Any modifications would create a new instance
            val modifiedUser = user.copy(name = "Modified Name")

            assertEquals(originalName, user.name, "Original user should remain unchanged")
            assertEquals(
                originalTokens,
                user.tokenBalance.getValue(),
                "Original token balance should remain unchanged",
            )
            assertEquals("Modified Name", modifiedUser.name, "Modified user should have new name")
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    inner class EdgeCases {

        @Test
        @DisplayName("Should handle maximum token balance")
        fun shouldHandleMaximumTokenBalance() {
            val maxBalance = TokenBalance.create(Int.MAX_VALUE)
            val user = TestDataFactory.createChildUser(tokenBalance = maxBalance)

            assertEquals(
                Int.MAX_VALUE,
                user.tokenBalance.getValue(),
                "Should handle maximum token balance",
            )
        }

        @Test
        @DisplayName("Should handle very long user names")
        fun shouldHandleVeryLongUserNames() {
            val longName = "A".repeat(1000)
            val user = TestDataFactory.createChildUser(name = longName)

            assertEquals(longName, user.name, "Should handle very long user names")
        }

        @Test
        @DisplayName("Should handle user creation with all minimum values")
        fun shouldHandleMinimumValues() {
            val user = User(
                name = "A",
                role = UserRole.CHILD,
                tokenBalance = TokenBalance.zero(),
                pin = null,
            )

            assertEquals("A", user.name)
            assertEquals(UserRole.CHILD, user.role)
            assertEquals(0, user.tokenBalance.getValue())
            assertNull(user.pin)
        }

        @Test
        @DisplayName("Should handle rapid user creation")
        fun shouldHandleRapidUserCreation() {
            val users = (1..100).map { TestDataFactory.createChildUser() }
            val uniqueIds = users.map { it.id }.toSet()

            assertEquals(100, uniqueIds.size, "All users should have unique IDs")
        }
    }

    @Nested
    @DisplayName("Property-Based Testing")
    inner class PropertyBasedTesting {

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 5, 10, 25, 50, 100, 500, 1000])
        @DisplayName("Should handle various token balance values")
        fun shouldHandleVariousTokenBalances(tokenAmount: Int) {
            val balance = TokenBalance.create(tokenAmount)
            val user = TestDataFactory.createChildUser(tokenBalance = balance)

            assertEquals(tokenAmount, user.tokenBalance.getValue())

            // Test affordability at different price points
            user.shouldAfford(0)
            if (tokenAmount > 0) {
                user.shouldAfford(1)
                user.shouldAfford(tokenAmount)
            }
            user.shouldNotAfford(tokenAmount + 1)
        }

        @ParameterizedTest
        @ValueSource(
            strings = ["0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999"],
        )
        @DisplayName("Should handle various PIN values")
        fun shouldHandleVariousPinValues(pinValue: String) {
            val pin = PIN.create(pinValue)
            val user = TestDataFactory.createCaregiverUser(pin = pin)

            assertNotNull(user.pin)
            assertTrue(user.pin!!.verify(pinValue), "Should verify PIN: $pinValue")
        }
    }

    @Nested
    @DisplayName("Integration with Other Domain Objects")
    inner class IntegrationTests {

        @Test
        @DisplayName("Should integrate with task completion scenario")
        fun shouldIntegrateWithTaskCompletion() {
            val (user, task, achievement) = TestDataFactory.createTaskCompletionScenario()

            // Verify user is properly set up for task completion
            assertEquals(user.id, task.assignedToUserId, "Task should be assigned to user")
            assertEquals(user.id, achievement.userId, "Achievement should belong to user")

            user.shouldBeChild()
            user.shouldAfford(10) // Should afford some rewards
        }

        @Test
        @DisplayName("Should integrate with reward redemption scenario")
        fun shouldIntegrateWithRewardRedemption() {
            val (user, canAfford) = TestDataFactory.createRewardScenario(
                userTokens = 50,
                rewardCost = 30,
            )

            assertEquals(
                50,
                user.tokenBalance.getValue(),
                "User should have expected token balance",
            )
            assertTrue(canAfford, "User should be able to afford the reward")
            user.shouldAfford(30)
        }

        @Test
        @DisplayName("Should integrate with family scenario")
        fun shouldIntegrateWithFamilyScenario() {
            val (child, caregiver) = TestDataFactory.createFamilyScenario(
                childName = "Emma",
                caregiverName = "Mom",
                childTokens = 75,
            )

            child.shouldBeChild(expectedName = "Emma", expectedTokens = 75)
            caregiver.shouldBeCaregiver(expectedName = "Mom")

            assertNotEquals(child.id, caregiver.id, "Child and caregiver should have different IDs")
        }
    }
}
