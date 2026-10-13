package com.ergin.ghclient.feature.profile.impl.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PayloadDto(
    @SerialName("action") val action: String? = null
)
