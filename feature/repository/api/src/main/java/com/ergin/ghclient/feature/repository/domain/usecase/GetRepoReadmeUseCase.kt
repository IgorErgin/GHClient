package com.ergin.ghclient.feature.repository.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.repository.domain.repository.RepoDetailsRepository
import javax.inject.Inject

class GetRepoReadmeUseCase @Inject constructor(
    private val repository: RepoDetailsRepository
) {
    suspend operator fun invoke(owner: String, repo: String): Result<String, DomainError.Network> {
        return repository.getRepoReadme(owner, repo)
    }
}
