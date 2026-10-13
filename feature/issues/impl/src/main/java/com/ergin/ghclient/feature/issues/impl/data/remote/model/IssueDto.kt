package com.ergin.ghclient.feature.issues.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueDto(
    @SerialName("id") val id: Long,
    @SerialName("number") val number: Int,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String? = null,
    @SerialName("state") val state: String,
    @SerialName("comments") val comments: Int = 0,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user") val user: UserDto
)
