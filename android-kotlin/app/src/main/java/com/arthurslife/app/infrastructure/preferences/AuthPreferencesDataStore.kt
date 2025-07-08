package com.arthurslife.app.infrastructure.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arthurslife.app.domain.user.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthPreferencesDataStore(
    private val context: Context,
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "auth_preferences",
        )

        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val CURRENT_ROLE = stringPreferencesKey("current_role")
        private val IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
    }

    val currentUserId: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[CURRENT_USER_ID]
        }

    val currentRole: Flow<UserRole?> =
        context.dataStore.data.map { preferences ->
            preferences[CURRENT_ROLE]?.let { UserRole.valueOf(it) }
        }

    val isAuthenticated: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[IS_AUTHENTICATED] ?: false
        }

    suspend fun setCurrentUser(
        userId: String,
        role: UserRole,
    ) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
            preferences[CURRENT_ROLE] = role.name
            preferences[IS_AUTHENTICATED] = true
        }
    }

    suspend fun clearCurrentUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
            preferences.remove(CURRENT_ROLE)
            preferences[IS_AUTHENTICATED] = false
        }
    }
}
