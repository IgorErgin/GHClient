package com.ergin.ghclient.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repo_details")
data class RepoDetailsEntity(
    @PrimaryKey val repoId: Long,
    val name: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val openIssuesCount: Int,
    val defaultBranch: String,
    val readmeContent: String? = null
)
