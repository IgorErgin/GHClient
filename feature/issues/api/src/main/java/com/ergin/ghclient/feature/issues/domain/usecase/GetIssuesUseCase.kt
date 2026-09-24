package com.ergin.ghclient.feature.issues.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.issues.domain.model.Issue
import com.ergin.ghclient.feature.issues.domain.repository.IssueRepository
import javax.inject.Inject

class GetIssuesUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(owner: OwnerName, repo: RepoName): Result<List<Issue>, DomainError.Network> {
        return issueRepository.getIssues(owner, repo)
    }
}
