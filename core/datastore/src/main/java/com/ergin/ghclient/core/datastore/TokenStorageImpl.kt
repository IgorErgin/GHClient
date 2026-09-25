package com.ergin.ghclient.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ergin.ghclient.core.domain.model.AccessToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TokenStorageImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenStorage {

    private val accessTokenKey = stringPreferencesKey(KEY_ACCESS_TOKEN)

    override val tokenFlow: Flow<AccessToken?> = dataStore.data.map { preferences ->
        preferences[accessTokenKey]?.let { AccessToken(it) }
    }

    override suspend fun saveToken(token: AccessToken) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = token.value
        }
    }

    override suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
        }
    }

    override fun getTokenSync(): AccessToken? = runBlocking {
        tokenFlow.firstOrNull()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}
