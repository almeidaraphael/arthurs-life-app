package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Comprehensive test suite for the User domain entity.
 *
 * Tests cover:
 * - User basic entity behavior
 * - User domain logic
 * - User role management
 * - Core user functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("User Domain Entity Tests")
class UserTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var childUser: User
    private lateinit var caregiverUser: User

    @BeforeEach
    fun setUp() {
        childUser = TestDataFactory.createChildUser()
        caregiverUser = TestDataFactory.createCaregiverUser()
    }

    @Test
    @DisplayName("Should have valid user entity")
    fun shouldHaveValidUserEntity() = runTest {
        assertNotNull(childUser.id, "User should have an ID")
        assertTrue(childUser.id.isNotBlank(), "User ID should not be blank")
        assertNotNull(childUser.name, "User should have a name")
        assertTrue(childUser.name.isNotBlank(), "User name should not be blank")
        assertNotNull(childUser.role, "User should have a role")
        assertNotNull(childUser.tokenBalance, "User should have a token balance")
    }

    @Test
    @DisplayName("Should handle child user correctly")
    fun shouldHandleChildUserCorrectly() = runTest {
        assertEquals(UserRole.CHILD, childUser.role, "Child user should have CHILD role")
        assertNull(childUser.pin, "Child user should not have a PIN")
        assertFalse(childUser.isAdmin, "Child user should not be admin")
        assertEquals(
            AvatarType.PREDEFINED,
            childUser.avatarType,
            "Child should have predefined avatar",
        )
        assertEquals(
            User.DEFAULT_AVATAR_ID,
            childUser.avatarData,
            "Child should have default avatar ID",
        )
    }

    @Test
    @DisplayName("Should handle caregiver user correctly")
    fun shouldHandleCaregiverUserCorrectly() = runTest {
        assertEquals(
            UserRole.CAREGIVER,
            caregiverUser.role,
            "Caregiver user should have CAREGIVER role",
        )
        assertNotNull(caregiverUser.pin, "Caregiver user should have a PIN")
        assertTrue(
            caregiverUser.tokenBalance.getValue() >= 0,
            "Token balance should be non-negative",
        )
    }

    @Test
    @DisplayName("Should handle token balance correctly")
    fun shouldHandleTokenBalanceCorrectly() = runTest {
        assertTrue(childUser.tokenBalance.getValue() >= 0, "Token balance should be non-negative")
        assertTrue(
            caregiverUser.tokenBalance.getValue() >= 0,
            "Token balance should be non-negative",
        )

        val userWithTokens = TestDataFactory.createChildUser(
            tokenBalance = TokenBalance.create(100),
        )
        assertEquals(
            100,
            userWithTokens.tokenBalance.getValue(),
            "User should have correct token balance",
        )
    }

    @Test
    @DisplayName("Should handle user avatar settings correctly")
    fun shouldHandleUserAvatarSettingsCorrectly() = runTest {
        assertEquals(
            AvatarType.PREDEFINED,
            childUser.avatarType,
            "Default avatar type should be PREDEFINED",
        )
        assertEquals(
            User.DEFAULT_AVATAR_ID,
            childUser.avatarData,
            "Default avatar data should be default ID",
        )

        val customAvatarUser = TestDataFactory.createChildUser(
            avatarType = AvatarType.CUSTOM,
            avatarData = "custom_avatar_data",
        )
        assertEquals(
            AvatarType.CUSTOM,
            customAvatarUser.avatarType,
            "Custom avatar type should be CUSTOM",
        )
        assertEquals(
            "custom_avatar_data",
            customAvatarUser.avatarData,
            "Custom avatar data should match",
        )
    }

    // Note: Detailed test coverage is provided by specialized test files:
    // - UserCreationTest.kt: User creation and initialization
    // - UserRoleTest.kt: Role-based access control
    // - UserTokenBalanceTest.kt: Token balance integration
    // - UserPinAuthenticationTest.kt: PIN authentication
    // - UserValidationTest.kt: Data validation and business rules
    // - UserBusinessRulesTest.kt: Business rule validation
    // - UserEdgeCasesTest.kt: Edge cases and error conditions
}
