package com.ergin.ghclient.feature.repository.domain.model

@JvmInline
value class PullRequestId(val value: Long)

/**
 * Модель Pull Request.
 */
data class PullRequest(
    val id: PullRequestId,
    val number: Int,
    val title: String,
    val state: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val createdAt: String
)
