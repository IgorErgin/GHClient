package com.ergin.ghclient.feature.profile.domain.model

import com.ergin.ghclient.core.domain.model.UserId

data class UserProfile(
    val id: UserId,
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val publicRepos: Int,
    val followers: Int,
    val following: Int
)
