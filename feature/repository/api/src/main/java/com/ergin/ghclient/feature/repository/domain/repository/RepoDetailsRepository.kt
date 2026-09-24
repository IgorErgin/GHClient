package com.ergin.ghclient.feature.repository.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.repository.domain.model.Commit
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails

interface RepoDetailsRepository {
    suspend fun getRepoDetails(owner: OwnerName, repo: RepoName): Result<RepoDetails, DomainError.Network>
    suspend fun getRepoReadme(owner: OwnerName, repo: RepoName): Result<String, DomainError.Network>
    suspend fun getRepoContents(owner: OwnerName, repo: RepoName, path: String = ""): Result<List<FileNode>, DomainError.Network>
    suspend fun getCommits(owner: OwnerName, repo: RepoName): Result<List<Commit>, DomainError.Network>
    suspend fun getPullRequests(owner: OwnerName, repo: RepoName): Result<List<PullRequest>, DomainError.Network>
}
