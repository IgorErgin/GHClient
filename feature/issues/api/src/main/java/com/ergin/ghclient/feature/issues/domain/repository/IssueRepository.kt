package com.ergin.ghclient.feature.issues.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.issues.domain.model.Issue

interface IssueRepository {
    suspend fun createIssue(owner: OwnerName, repo: RepoName, title: String, body: String): Result<Unit, DomainError.Network>
    suspend fun getIssues(owner: OwnerName, repo: RepoName): Result<List<Issue>, DomainError.Network>
}
