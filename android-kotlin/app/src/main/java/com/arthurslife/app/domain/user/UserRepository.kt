package com.arthurslife.app.domain.user

interface UserRepository {
    suspend fun findById(id: String): User?

    suspend fun findByRole(role: UserRole): User?

    suspend fun getAllUsers(): List<User>

    suspend fun saveUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun deleteUser(userId: String)
}
