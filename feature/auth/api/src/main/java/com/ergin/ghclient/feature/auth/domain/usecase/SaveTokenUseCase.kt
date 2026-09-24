package com.ergin.ghclient.feature.auth.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.model.AccessToken
import com.ergin.ghclient.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: AccessToken): Result<Unit, DomainError.Local> {
        if (token.value.isBlank()) return Result.Error(DomainError.Local.IO_EXCEPTION)
        return authRepository.saveToken(token)
    }
}
