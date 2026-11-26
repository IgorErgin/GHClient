package com.ergin.ghclient.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issue_drafts")
data class IssueDraftEntity(
    @PrimaryKey
    val uid: String,
    val owner: String,
    val repoName: String,
    val title: String,
    val body: String,
    val priority: Int,
    val updatedAt: Long
)
