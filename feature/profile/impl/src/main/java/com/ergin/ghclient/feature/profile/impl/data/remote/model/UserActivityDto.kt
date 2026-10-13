package com.ergin.ghclient.feature.profile.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserActivityDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("actor") val actor: UserDto,
    @SerialName("repo") val repo: RepoSummaryDto,
    @SerialName("created_at") val createdAt: String,
    @SerialName("payload") val payload: PayloadDto? = null
)
