package com.ergin.ghclient.feature.upload.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName

interface UploadRepository {
    suspend fun uploadFile(
        owner: OwnerName,
        repo: RepoName,
        path: String,
        message: String,
        base64Content: String
    ): Result<Unit, DomainError.Network>
}
