package com.trototrackapp.trototrack.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreference(context: Context) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveToken(token: String) {
        val currentTimeMillis = System.currentTimeMillis()
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[TOKEN_TIMESTAMP_KEY] = currentTimeMillis.toString()
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    val tokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    suspend fun isTokenExpired(): Boolean {
        val tokenTimestamp = dataStore.data.map { preferences ->
            preferences[TOKEN_TIMESTAMP_KEY]?.toLong() ?: 0
        }.first()

        val expirationTimeMillis = tokenTimestamp + (5 * 60 * 60 * 1000)

        return System.currentTimeMillis() >= expirationTimeMillis
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val TOKEN_TIMESTAMP_KEY = stringPreferencesKey("token_timestamp")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreference(context.applicationContext)
            }.also { INSTANCE = it }
        }
    }
}
