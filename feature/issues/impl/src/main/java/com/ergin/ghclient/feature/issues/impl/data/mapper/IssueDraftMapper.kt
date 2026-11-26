package com.ergin.ghclient.feature.issues.impl.data.mapper

import com.ergin.ghclient.feature.issues.domain.model.DraftPriority
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto

fun IssueDraftDto.toDomain(): IssueDraft {
    val domainPriority = try {
        DraftPriority.valueOf(priority.uppercase())
    } catch (_: Exception) {
        DraftPriority.NORMAL
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

fun IssueDraft.toDto(): IssueDraftDto {
    return IssueDraftDto(
        uid = uid,
        owner = owner,
        repoName = repoName,
        title = title,
        body = body,
        priority = priority.name,
        updatedAt = updatedAt
    )
}
