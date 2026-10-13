package com.ergin.ghclient.feature.repository.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)
