package com.ergin.ghclient.feature.issues.domain.model

import java.util.UUID

/**
 * Доменная модель локального черновика рапорта (IssueDraft).
 */
data class IssueDraft(
    val uid: String = UUID.randomUUID().toString(),
    val owner: String,
    val repoName: String,
    val title: String,
    val body: String,
    val priority: DraftPriority = DraftPriority.NORMAL,
    val updatedAt: Long = System.currentTimeMillis()
)
