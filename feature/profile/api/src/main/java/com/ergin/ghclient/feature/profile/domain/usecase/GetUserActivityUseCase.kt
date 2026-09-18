package com.ergin.ghclient.feature.profile.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetUserActivityUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(username: String): Result<List<UserActivity>, DomainError.Network> {
        if (username.isBlank()) return Result.Success(emptyList())
        return profileRepository.getUserActivity(username)
    }
}
