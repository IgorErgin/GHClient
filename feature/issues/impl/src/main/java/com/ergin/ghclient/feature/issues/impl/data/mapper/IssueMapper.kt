package com.ergin.ghclient.feature.issues.impl.data.mapper

import com.ergin.ghclient.core.database.entity.IssueEntity
import com.ergin.ghclient.feature.issues.domain.model.Issue
import com.ergin.ghclient.feature.issues.domain.model.IssueId
import com.ergin.ghclient.feature.issues.domain.model.IssueNumber
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDto

fun IssueDto.toDomain(): Issue {
    return Issue(
        id = IssueId(id),
        number = IssueNumber(number),
        title = title,
        body = body,
        state = if (state.equals("closed", ignoreCase = true)) Issue.IssueState.CLOSED else Issue.IssueState.OPEN,
        authorName = user.login,
        authorAvatarUrl = user.avatarUrl,
        createdAt = createdAt,
        commentsCount = comments
    )
}

fun IssueEntity.toDomain(): Issue {
    return Issue(
        id = IssueId(id),
        number = IssueNumber(number),
        title = title,
        body = body,
        state = if (state.equals("closed", ignoreCase = true)) Issue.IssueState.CLOSED else Issue.IssueState.OPEN,
        authorName = authorName,
        authorAvatarUrl = authorAvatarUrl,
        createdAt = createdAt,
        commentsCount = commentsCount
    )
}

fun Issue.toEntity(repoOwner: String, repoName: String): IssueEntity {
    return IssueEntity(
        id = id.value,
        repoOwner = repoOwner,
        repoName = repoName,
        number = number.value,
        title = title,
        body = body,
        state = state.name,
        authorName = authorName,
        authorAvatarUrl = authorAvatarUrl,
        createdAt = createdAt,
        commentsCount = commentsCount
    )
}
