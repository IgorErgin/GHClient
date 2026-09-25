package com.ergin.ghclient.feature.upload.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadFileRequestDto(
    @SerialName("message") val message: String,
    @SerialName("content") val content: String,
    @SerialName("branch") val branch: String
)
