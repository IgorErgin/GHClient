package com.ergin.ghclient.feature.upload.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.upload.domain.repository.UploadRepository
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val uploadRepository: UploadRepository
) {
    suspend operator fun invoke(
        owner: OwnerName,
        repo: RepoName,
        path: String,
        base64Content: String,
        message: String,
        branch: String
    ): Result<Unit, DomainError.Network> {
        if (path.isBlank() || base64Content.isBlank()) return Result.Error(DomainError.Network.UNKNOWN)
        return uploadRepository.uploadFile(owner, repo, path, base64Content, message, branch)
    }
}
