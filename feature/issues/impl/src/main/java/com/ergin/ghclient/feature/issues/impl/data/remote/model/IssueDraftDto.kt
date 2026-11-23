package com.ergin.ghclient.feature.issues.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueDraftDto(
    @SerialName("uid")
    val uid: String,
    @SerialName("owner")
    val owner: String,
    @SerialName("repo_name")
    val repoName: String,
    @SerialName("title")
    val title: String,
    @SerialName("body")
    val body: String,
    @SerialName("priority")
    val priority: String,
    @SerialName("updated_at")
    val updatedAt: Long
)
