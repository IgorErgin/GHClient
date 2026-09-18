package com.ergin.ghclient.feature.repository.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.repository.RepoDetailsRepository
import javax.inject.Inject

class GetRepoContentsUseCase @Inject constructor(
    private val repository: RepoDetailsRepository
) {
    suspend operator fun invoke(owner: String, repo: String, path: String = ""): Result<List<FileNode>, DomainError.Network> {
        return repository.getRepoContents(owner, repo, path)
    }
}
