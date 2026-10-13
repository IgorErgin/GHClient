package com.ergin.ghclient.feature.auth.impl.data.repository

import com.ergin.ghclient.core.datastore.TokenStorage
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.model.AccessToken
import com.ergin.ghclient.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override val isAuthorized: Flow<Boolean> = tokenStorage.tokenFlow.map { token ->
        token != null && token.value.isNotBlank()
    }

    override suspend fun saveToken(token: AccessToken): Result<Unit, DomainError.Local> {
        return try {
            tokenStorage.saveToken(token)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(DomainError.Local.IO_EXCEPTION)
        }
    }

    override suspend fun clearToken(): Result<Unit, DomainError.Local> {
        return try {
            tokenStorage.clearToken()
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(DomainError.Local.IO_EXCEPTION)
        }
    }
}
