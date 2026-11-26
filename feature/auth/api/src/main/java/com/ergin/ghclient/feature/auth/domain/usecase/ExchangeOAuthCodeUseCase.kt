package com.ergin.ghclient.feature.auth.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.model.AccessToken
import com.ergin.ghclient.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ExchangeOAuthCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(code: String): Result<AccessToken, DomainError.Network> {
        return authRepository.exchangeCodeForToken(code)
    }
}
