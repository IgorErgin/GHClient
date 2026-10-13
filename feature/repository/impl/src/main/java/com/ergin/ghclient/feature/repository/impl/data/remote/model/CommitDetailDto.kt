package com.ergin.ghclient.feature.repository.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitDetailDto(
    @SerialName("message") val message: String,
    @SerialName("author") val author: CommitAuthorDto? = null
)

@Serializable
data class CommitAuthorDto(
    @SerialName("name") val name: String? = null,
    @SerialName("date") val date: String? = null
)
