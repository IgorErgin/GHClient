package com.ergin.ghclient.feature.auth.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.model.AccessToken
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthorized: Flow<Boolean>
    
    suspend fun saveToken(token: AccessToken): Result<Unit, DomainError.Local>
    suspend fun clearToken(): Result<Unit, DomainError.Local>
}
