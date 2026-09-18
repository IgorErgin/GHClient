package com.ergin.ghclient.feature.profile.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.profile.domain.model.UserProfile
import com.ergin.ghclient.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile, DomainError.Network> {
        return profileRepository.getAuthenticatedUser()
    }
}
