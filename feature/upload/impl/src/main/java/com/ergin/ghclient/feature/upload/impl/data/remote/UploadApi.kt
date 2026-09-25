package com.ergin.ghclient.feature.upload.impl.data.remote

import com.ergin.ghclient.feature.upload.impl.data.remote.model.UploadFileRequestDto
import com.ergin.ghclient.feature.upload.impl.data.remote.model.UploadFileResponseDto
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface UploadApi {

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun uploadFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Body request: UploadFileRequestDto
    ): UploadFileResponseDto
}
