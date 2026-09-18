package com.ergin.ghclient.feature.search.domain.model

data class Repo(
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val ownerName: String,
    val ownerAvatarUrl: String
)
