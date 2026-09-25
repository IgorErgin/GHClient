package com.ergin.ghclient.feature.upload.impl.data.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.core.network.safeApiCall
import com.ergin.ghclient.feature.upload.domain.repository.UploadRepository
import com.ergin.ghclient.feature.upload.impl.data.remote.UploadApi
import com.ergin.ghclient.feature.upload.impl.data.remote.model.UploadFileRequestDto
import javax.inject.Inject

class UploadRepositoryImpl @Inject constructor(
    private val uploadApi: UploadApi
) : UploadRepository {

    override suspend fun uploadFile(
        owner: OwnerName,
        repo: RepoName,
        path: String,
        base64Content: String,
        message: String,
        branch: String
    ): Result<Unit, DomainError.Network> {
        val result = safeApiCall {
            uploadApi.uploadFile(
                owner = owner.value,
                repo = repo.value,
                path = path,
                request = UploadFileRequestDto(
                    message = message,
                    content = base64Content,
                    branch = branch
                )
            )
        }
        return when (result) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> Result.Error(result.error)
        }
    }
}
