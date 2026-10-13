package com.ergin.ghclient.feature.search.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("owner") val owner: OwnerDto
)
