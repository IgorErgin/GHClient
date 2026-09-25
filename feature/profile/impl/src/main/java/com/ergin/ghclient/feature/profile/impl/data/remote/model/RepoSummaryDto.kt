package com.ergin.ghclient.feature.profile.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoSummaryDto(
    @SerialName("id") val id: Long = 0L,
    @SerialName("name") val name: String
)
