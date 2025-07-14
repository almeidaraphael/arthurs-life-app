package com.arthurslife.app.domain.auth

import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserDataSource
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.infrastructure.preferences.AuthPreferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationDomainService
@Inject
constructor(
    private val userDataSource: UserDataSource,
    private val authPreferencesDataStore: AuthPreferencesDataStore,
) : AuthenticationService,
    AuthenticationSessionService {
    override suspend fun authenticateWithPin(pin: String): AuthResult {
        val user = userDataSource.findByPin(pin)

        return if (user != null) {
            authPreferencesDataStore.setCurrentUser(user.id, user.role)
            AuthResult.Success(user)
        } else {
            AuthResult.InvalidPin
        }
    }

    override suspend fun switchRole(targetRole: UserRole): AuthResult {
        val currentUser = getCurrentUser()

        return if (currentUser != null && currentUser.role == UserRole.CAREGIVER) {
            // Only caregivers can switch roles
            val targetUser = userDataSource.findByRole(targetRole)
            if (targetUser != null) {
                authPreferencesDataStore.setCurrentUser(targetUser.id, targetUser.role)
                AuthResult.Success(targetUser)
            } else {
                AuthResult.UserNotFound
            }
        } else {
            AuthResult.UserNotFound
        }
    }

    override suspend fun authenticateAsChild(): AuthResult {
        // Direct authentication as child without PIN
        val childUser = userDataSource.findByRole(UserRole.CHILD)
        return if (childUser != null) {
            authPreferencesDataStore.setCurrentUser(childUser.id, childUser.role)
            AuthResult.Success(childUser)
        } else {
            AuthResult.UserNotFound
        }
    }

    /**
     * Authenticates as a specific child user.
     * This method is used when a specific child is selected from the role selector.
     */
    suspend fun authenticateAsSpecificChild(childUser: User): AuthResult {
        return if (childUser.role == UserRole.CHILD) {
            authPreferencesDataStore.setCurrentUser(childUser.id, childUser.role)
            AuthResult.Success(childUser)
        } else {
            AuthResult.UserNotFound
        }
    }

    override suspend fun logout() {
        authPreferencesDataStore.clearCurrentUser()
    }

    override suspend fun getCurrentUser(): User? {
        val currentUserId = authPreferencesDataStore.currentUserId.firstOrNull()
        return currentUserId?.let { userDataSource.findById(it) }
    }

    override suspend fun isAuthenticated(): Boolean = getCurrentUser() != null
}
