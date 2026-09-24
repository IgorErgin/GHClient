package com.ergin.ghclient.feature.search.domain.model

import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId

data class Repo(
    val id: RepoId,
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val ownerName: OwnerName,
    val ownerAvatarUrl: String
)
