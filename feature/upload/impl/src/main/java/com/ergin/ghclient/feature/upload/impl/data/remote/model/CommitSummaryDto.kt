package com.ergin.ghclient.feature.upload.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitSummaryDto(
    @SerialName("sha") val sha: String,
    @SerialName("url") val url: String? = null,
    @SerialName("message") val message: String? = null
)
