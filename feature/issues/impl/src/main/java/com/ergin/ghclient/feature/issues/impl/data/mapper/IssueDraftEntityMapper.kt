package com.ergin.ghclient.feature.issues.impl.data.mapper

import com.ergin.ghclient.core.database.entity.IssueDraftEntity
import com.ergin.ghclient.feature.issues.domain.model.DraftPriority
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft

fun IssueDraftEntity.toDomain(): IssueDraft {
    val domainPriority = when (priority) {
        0 -> DraftPriority.LOW
        2 -> DraftPriority.CRITICAL
        else -> DraftPriority.NORMAL
    }

    return IssueDraft(
        uid = uid,
        owner = owner,
        repoName = repoName,
        title = title,
        body = body,
        priority = domainPriority,
        updatedAt = updatedAt
    )
}

fun IssueDraft.toEntity(): IssueDraftEntity {
    val intPriority = when (priority) {
        DraftPriority.LOW -> 0
        DraftPriority.NORMAL -> 1
        DraftPriority.CRITICAL -> 2
    }

    return IssueDraftEntity(
        uid = uid,
        owner = owner,
        repoName = repoName,
        title = title,
        body = body,
        priority = intPriority,
        updatedAt = updatedAt
    )
}
