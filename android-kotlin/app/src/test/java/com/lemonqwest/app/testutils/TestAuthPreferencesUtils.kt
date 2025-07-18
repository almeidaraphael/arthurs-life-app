package com.lemonqwest.app.testutils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lemonqwest.app.domain.user.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Shared test utility for DataStore and AuthPreferencesDataStore test implementations.
 */
class TestDataStore : DataStore<Preferences> {
    private val dataFlow = MutableStateFlow(emptyPreferences())

    override val data: Flow<Preferences> = dataFlow

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val currentData = dataFlow.value
        val newData = transform(currentData)
        dataFlow.value = newData
        return newData
    }

    suspend fun clear() {
        dataFlow.value = emptyPreferences()
    }
}

class TestAuthPreferencesDataStore(
    private val testDataStore: TestDataStore,
) {
    private val userIdKey = stringPreferencesKey("current_user_id")
    private val roleKey = stringPreferencesKey("current_role")
    private val authKey = booleanPreferencesKey("is_authenticated")

    val currentUserId: Flow<String?> = testDataStore.data.map { preferences ->
        preferences[userIdKey]
    }

    val currentRole: Flow<UserRole?> = testDataStore.data.map { preferences ->
        preferences[roleKey]?.let { UserRole.valueOf(it) }
    }

    val isAuthenticated: Flow<Boolean> = testDataStore.data.map { preferences ->
        preferences[authKey] ?: false
    }

    suspend fun setCurrentUser(userId: String, role: UserRole) {
        testDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(userIdKey, userId)
                set(roleKey, role.name)
                set(authKey, true)
            }
        }
    }

    suspend fun clearCurrentUser() {
        testDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                remove(userIdKey)
                remove(roleKey)
                set(authKey, false)
            }
        }
    }
}
