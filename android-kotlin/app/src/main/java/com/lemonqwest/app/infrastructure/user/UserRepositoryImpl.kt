package com.lemonqwest.app.infrastructure.user

import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserDataSource
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
@Inject
constructor(
    private val userDataSource: UserDataSource,
) : UserRepository {
    override suspend fun findById(id: String): User? = userDataSource.findById(id)

    override suspend fun findByRole(role: UserRole): User? = userDataSource.findByRole(role)

    override suspend fun getAllUsers(): List<User> = userDataSource.getAllUsers()

    override suspend fun saveUser(user: User) = userDataSource.saveUser(user)

    override suspend fun saveUsers(users: List<User>) = userDataSource.saveUsers(users)

    override suspend fun updateUser(user: User) = userDataSource.saveUser(user)

    override suspend fun deleteUser(userId: String) {
        // For in-memory implementation, we can't delete users
        // In a real database implementation, this would remove the user
        throw UnsupportedOperationException(
            "Delete operation not supported in current implementation",
        )
    }
}
