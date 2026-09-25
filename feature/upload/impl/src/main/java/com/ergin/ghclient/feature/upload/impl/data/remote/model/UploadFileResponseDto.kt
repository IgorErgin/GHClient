package com.ergin.ghclient.feature.upload.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadFileResponseDto(
    @SerialName("content") val content: FileNodeSummaryDto? = null,
    @SerialName("commit") val commit: CommitSummaryDto? = null
)
