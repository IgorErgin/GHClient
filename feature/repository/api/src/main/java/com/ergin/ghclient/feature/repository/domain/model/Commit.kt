package com.ergin.ghclient.feature.repository.domain.model

/**
 * Модель коммита в репозитории.
 */
data class Commit(
    val sha: String,
    val message: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val date: String
)
