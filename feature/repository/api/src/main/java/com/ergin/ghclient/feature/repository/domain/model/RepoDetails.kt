package com.ergin.ghclient.feature.repository.domain.model

data class RepoDetails(
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val openIssuesCount: Int,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val defaultBranch: String
)
