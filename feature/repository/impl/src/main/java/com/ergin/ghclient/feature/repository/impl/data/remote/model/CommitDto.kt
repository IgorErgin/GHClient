package com.ergin.ghclient.feature.repository.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitDto(
    @SerialName("sha") val sha: String,
    @SerialName("commit") val commit: CommitDetailDto,
    @SerialName("author") val author: UserDto? = null
)
