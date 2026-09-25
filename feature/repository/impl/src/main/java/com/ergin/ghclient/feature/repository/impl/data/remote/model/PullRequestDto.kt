package com.ergin.ghclient.feature.repository.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequestDto(
    @SerialName("id") val id: Long,
    @SerialName("number") val number: Int,
    @SerialName("title") val title: String,
    @SerialName("state") val state: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user") val user: UserDto? = null
)
