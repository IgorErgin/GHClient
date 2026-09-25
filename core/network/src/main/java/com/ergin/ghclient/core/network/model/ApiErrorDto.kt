package com.ergin.ghclient.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    @SerialName("message") val message: String? = null,
    @SerialName("documentation_url") val documentationUrl: String? = null
)
