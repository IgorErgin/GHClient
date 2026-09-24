package com.ergin.ghclient.feature.repository.domain.model

import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId

data class RepoDetails(
    val id: RepoId,
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val openIssuesCount: Int,
    val ownerName: OwnerName,
    val ownerAvatarUrl: String,
    val defaultBranch: String
)
