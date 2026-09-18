package com.ergin.ghclient.feature.profile.domain.model

/**
 * Модель активности (событий) пользователя на GitHub.
 */
data class UserActivity(
    val id: String,
    val type: String,
    val repoName: String,
    val createdAt: String,
    val payloadAction: String?
)
