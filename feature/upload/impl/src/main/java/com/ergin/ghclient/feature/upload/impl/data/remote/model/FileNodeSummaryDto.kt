package com.ergin.ghclient.feature.upload.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileNodeSummaryDto(
    @SerialName("name") val name: String,
    @SerialName("path") val path: String,
    @SerialName("sha") val sha: String,
    @SerialName("size") val size: Long = 0L
)
