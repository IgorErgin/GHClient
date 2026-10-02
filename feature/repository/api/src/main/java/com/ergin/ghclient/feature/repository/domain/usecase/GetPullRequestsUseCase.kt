package com.ergin.ghclient.feature.repository.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.repository.RepoDetailsRepository
import javax.inject.Inject

class GetPullRequestsUseCase @Inject constructor(
    private val repository: RepoDetailsRepository
) {
    suspend operator fun invoke(owner: OwnerName, repo: RepoName): Result<List<PullRequest>, DomainError.Network> {
        return repository.getPullRequests(owner, repo)
    }
}
