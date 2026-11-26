package com.ergin.ghclient.feature.profile.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.profile.domain.repository.ProfileRepository
import com.ergin.ghclient.feature.search.domain.model.Repo
import javax.inject.Inject

class GetUserReposUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(username: String): Result<List<Repo>, DomainError.Network> {
        if (username.isBlank()) return Result.Success(emptyList())
        return profileRepository.getUserRepos(username)
    }
}
