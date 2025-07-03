package com.arthurslife.app.infrastructure.user

import com.arthurslife.app.domain.auth.PIN
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserDataSource
import com.arthurslife.app.domain.user.UserRole
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUserDataSource
    @Inject
    constructor() : UserDataSource {
        private val mutex = Mutex()
        private val users = mutableListOf<User>()

        companion object {
            private const val DEMO_PARENT_PIN = "1234"
            private const val DEMO_CHILD_PIN = "2468"
            private const val DEMO_PARENT_TOKENS = 1000
            private const val DEMO_CHILD_TOKENS = 50
        }

        init {
            // Initialize with sample users for demo - in production, these would come from database
            val parentUser =
                User(
                    id = "parent-1",
                    name = "Parent",
                    role = UserRole.CAREGIVER,
                    tokenBalance = TokenBalance.create(DEMO_PARENT_TOKENS),
                    pin = PIN.create(DEMO_PARENT_PIN),
                )

            val childUser =
                User(
                    id = "child-1",
                    name = "Arthur",
                    role = UserRole.CHILD,
                    tokenBalance = TokenBalance.create(DEMO_CHILD_TOKENS),
                    pin = PIN.create(DEMO_CHILD_PIN),
                )

            users.addAll(listOf(parentUser, childUser))
        }

        override suspend fun findByPin(pin: String): User? =
            mutex.withLock {
                users.find { user ->
                    user.pin?.verify(pin) == true
                }
            }

        override suspend fun findById(id: String): User? =
            mutex.withLock {
                users.find { it.id == id }
            }

        override suspend fun findByRole(role: UserRole): User? =
            mutex.withLock {
                users.find { it.role == role }
            }

        override suspend fun getAllUsers(): List<User> =
            mutex.withLock {
                users.toList()
            }

        override suspend fun saveUser(user: User): Unit =
            mutex.withLock {
                val existingIndex = users.indexOfFirst { it.id == user.id }
                if (existingIndex >= 0) {
                    users[existingIndex] = user
                } else {
                    users.add(user)
                }
            }
    }
