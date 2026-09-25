package com.ergin.ghclient.feature.profile.impl.data.mapper

import com.ergin.ghclient.core.domain.model.UserId
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.feature.profile.domain.model.UserProfile
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserActivityDto
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserProfileDto

fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        id = UserId(id),
        login = login,
        avatarUrl = avatarUrl,
        name = name,
        bio = bio,
        publicRepos = publicRepos,
        followers = followers,
        following = following
    )
}

fun UserActivityDto.toDomain(): UserActivity {
    return UserActivity(
        id = id,
        type = type,
        repoName = repo.name,
        createdAt = createdAt,
        payloadAction = payload?.action
    )
}
