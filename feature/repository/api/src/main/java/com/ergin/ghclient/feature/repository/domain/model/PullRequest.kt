package com.ergin.ghclient.feature.repository.domain.model

/**
 * Модель Pull Request.
 */
data class PullRequest(
    val id: Long,
    val number: Int,
    val title: String,
    val state: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val createdAt: String
)
