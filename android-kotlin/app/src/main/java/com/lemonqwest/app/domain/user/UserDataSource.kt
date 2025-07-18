package com.lemonqwest.app.domain.user

interface UserDataSource {
    suspend fun findByPin(pin: String): User?

    suspend fun findById(id: String): User?

    suspend fun findByRole(role: UserRole): User?

    suspend fun getAllUsers(): List<User>

    suspend fun saveUser(user: User)

    suspend fun saveUsers(users: List<User>)
}
