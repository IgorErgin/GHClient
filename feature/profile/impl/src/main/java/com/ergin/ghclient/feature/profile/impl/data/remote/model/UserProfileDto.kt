package com.ergin.ghclient.feature.profile.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("id") val id: Long,
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("name") val name: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("public_repos") val publicRepos: Int,
    @SerialName("followers") val followers: Int,
    @SerialName("following") val following: Int
)
