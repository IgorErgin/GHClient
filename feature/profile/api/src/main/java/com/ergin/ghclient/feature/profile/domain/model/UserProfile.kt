package com.ergin.ghclient.feature.profile.domain.model

data class UserProfile(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val publicRepos: Int,
    val followers: Int,
    val following: Int
)
