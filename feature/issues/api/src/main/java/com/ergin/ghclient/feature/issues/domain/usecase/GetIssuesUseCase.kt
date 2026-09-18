package com.ergin.ghclient.feature.issues.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.issues.domain.model.Issue
import com.ergin.ghclient.feature.issues.domain.repository.IssueRepository
import javax.inject.Inject

class GetIssuesUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(owner: String, repo: String): Result<List<Issue>, DomainError.Network> {
        return issueRepository.getIssues(owner, repo)
    }
}
