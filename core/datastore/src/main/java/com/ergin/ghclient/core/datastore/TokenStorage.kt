package com.ergin.ghclient.core.datastore

import com.ergin.ghclient.core.domain.model.AccessToken
import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    val tokenFlow: Flow<AccessToken?>
    suspend fun saveToken(token: AccessToken)
    suspend fun clearToken()
    fun getTokenSync(): AccessToken?
}
