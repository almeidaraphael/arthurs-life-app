package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.domain.shouldBeCaregiver
import com.lemonqwest.app.domain.shouldBeChild
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for User creation and initialization.
 *
 * Tests cover:
 * - User creation with default values
 * - Custom parameter initialization
 * - ID generation and uniqueness
 * - Factory method validation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("User Creation Tests")
class UserCreationTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should create child user with default values")
    fun shouldCreateChildUserWithDefaults() {
        val user = TestDataFactory.createChildUser()

        user.shouldBeChild()
        assertNotNull(user.id, "User should have an ID")
        assertTrue(user.id.isNotBlank(), "User ID should not be blank")
        assertEquals("LemonQwest Kid", user.name, "Default child name should be LemonQwest Kid")
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
