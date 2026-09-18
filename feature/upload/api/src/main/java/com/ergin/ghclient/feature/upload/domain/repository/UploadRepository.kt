package com.ergin.ghclient.feature.upload.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result

interface UploadRepository {
    suspend fun uploadFile(
        owner: String, 
        repo: String, 
        path: String, 
        base64Content: String, 
        message: String, 
        branch: String
    ): Result<Unit, DomainError.Network>
}
