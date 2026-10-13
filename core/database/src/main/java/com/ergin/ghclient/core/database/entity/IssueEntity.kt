package com.ergin.ghclient.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issues")
data class IssueEntity(
    @PrimaryKey val id: Long,
    val repoOwner: String,
    val repoName: String,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val createdAt: String,
    val commentsCount: Int
)
