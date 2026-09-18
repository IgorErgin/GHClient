package com.ergin.ghclient.feature.issues.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.issues.domain.model.Issue

interface IssueRepository {
    suspend fun createIssue(owner: String, repo: String, title: String, body: String): Result<Unit, DomainError.Network>
    suspend fun getIssues(owner: String, repo: String): Result<List<Issue>, DomainError.Network>
}
