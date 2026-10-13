package com.ergin.ghclient.feature.auth.impl.data.repository

import com.ergin.ghclient.core.database.dao.RepoDao
import com.ergin.ghclient.core.datastore.TokenStorage
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.model.AccessToken
import com.ergin.ghclient.feature.auth.domain.repository.AuthRepository
import com.ergin.ghclient.feature.auth.impl.BuildConfig
import com.ergin.ghclient.feature.auth.impl.data.remote.OAuthApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val oAuthApi: OAuthApi,
    private val repoDao: RepoDao
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

    override suspend fun exchangeCodeForToken(code: String): Result<AccessToken, DomainError.Network> {
        return try {
            val response = oAuthApi.exchangeCodeForToken(
                clientId = BuildConfig.GITHUB_CLIENT_ID,
                clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
                code = code
            )

            if (response.error != null) {
                return Result.Error(DomainError.Network.UNAUTHORIZED)
            }

            val tokenStr = response.accessToken
            if (!tokenStr.isNullOrBlank()) {
                val token = AccessToken(tokenStr)
                tokenStorage.saveToken(token)
                Result.Success(token)
            } else {
                Result.Error(DomainError.Network.UNAUTHORIZED)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(DomainError.Network.UNKNOWN)
        }
    }

    override suspend fun clearToken(): Result<Unit, DomainError.Local> {
        return try {
            repoDao.clearNonFavoriteRepos()
            tokenStorage.clearToken()
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(DomainError.Local.IO_EXCEPTION)
        }
    }
}
