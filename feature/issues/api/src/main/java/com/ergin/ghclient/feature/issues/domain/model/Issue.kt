package com.ergin.ghclient.feature.issues.domain.model

/**
 * Модель Issue репозитория.
 */
data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: IssueState,
    val authorName: String,
    val authorAvatarUrl: String,
    val createdAt: String,
    val commentsCount: Int
) {
    enum class IssueState {
        OPEN, CLOSED
    }
}
