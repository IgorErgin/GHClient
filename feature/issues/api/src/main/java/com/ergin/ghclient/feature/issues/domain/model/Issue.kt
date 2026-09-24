package com.ergin.ghclient.feature.issues.domain.model

@JvmInline
value class IssueId(val value: Long)

@JvmInline
value class IssueNumber(val value: Int)

/**
 * Модель Issue репозитория.
 */
data class Issue(
    val id: IssueId,
    val number: IssueNumber,
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
