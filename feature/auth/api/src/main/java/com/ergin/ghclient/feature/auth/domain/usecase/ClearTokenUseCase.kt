package com.ergin.ghclient.feature.auth.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ClearTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit, DomainError.Local> {
        return authRepository.clearToken()
    }
}
