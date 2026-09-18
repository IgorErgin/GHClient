package com.ergin.ghclient.feature.repository.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.repository.domain.model.Commit
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails

interface RepoDetailsRepository {
    suspend fun getRepoDetails(owner: String, repo: String): Result<RepoDetails, DomainError.Network>
    suspend fun getRepoReadme(owner: String, repo: String): Result<String, DomainError.Network>
    suspend fun getRepoContents(owner: String, repo: String, path: String = ""): Result<List<FileNode>, DomainError.Network>
    suspend fun getCommits(owner: String, repo: String): Result<List<Commit>, DomainError.Network>
    suspend fun getPullRequests(owner: String, repo: String): Result<List<PullRequest>, DomainError.Network>
}
