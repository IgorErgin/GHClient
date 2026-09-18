package com.ergin.ghclient.feature.issues.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.issues.domain.repository.IssueRepository
import javax.inject.Inject

class CreateIssueUseCase @Inject constructor(
    private val issueRepository: IssueRepository
) {
    suspend operator fun invoke(owner: String, repo: String, title: String, body: String): Result<Unit, DomainError.Network> {
        if (title.isBlank()) return Result.Error(DomainError.Network.UNKNOWN)
        return issueRepository.createIssue(owner, repo, title, body)
    }
}
