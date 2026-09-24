package com.ergin.ghclient.feature.repository.domain.model

import com.ergin.ghclient.core.domain.model.Sha

/**
 * Модель коммита в репозитории.
 */
data class Commit(
    val sha: Sha,
    val message: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val date: String
)
