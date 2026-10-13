package com.ergin.ghclient.feature.issues.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateIssueRequestDto(
    @SerialName("title") val title: String,
    @SerialName("body") val body: String
)
