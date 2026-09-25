package com.ergin.ghclient.feature.repository.impl.data.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.core.network.safeApiCall
import com.ergin.ghclient.feature.repository.domain.model.Commit
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails
import com.ergin.ghclient.feature.repository.domain.repository.RepoDetailsRepository
import com.ergin.ghclient.feature.repository.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.repository.impl.data.remote.RepositoryApi
import javax.inject.Inject

class RepoDetailsRepositoryImpl @Inject constructor(
    private val repositoryApi: RepositoryApi
) : RepoDetailsRepository {

    override suspend fun getRepoDetails(
        owner: OwnerName,
        repo: RepoName
    ): Result<RepoDetails, DomainError.Network> {
        val result = safeApiCall {
            repositoryApi.getRepoDetails(owner.value, repo.value)
        }
        return when (result) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getRepoReadme(
        owner: OwnerName,
        repo: RepoName
    ): Result<String, DomainError.Network> {
        val result = safeApiCall {
            repositoryApi.getRepoReadme(owner.value, repo.value)
        }
        return when (result) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getRepoContents(
        owner: OwnerName,
        repo: RepoName,
        path: String
    ): Result<List<FileNode>, DomainError.Network> {
        val result = safeApiCall {
            repositoryApi.getRepoContents(owner.value, repo.value, path)
        }
        return when (result) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getCommits(
        owner: OwnerName,
        repo: RepoName
    ): Result<List<Commit>, DomainError.Network> {
        val result = safeApiCall {
            repositoryApi.getCommits(owner.value, repo.value)
        }
        return when (result) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getPullRequests(
        owner: OwnerName,
        repo: RepoName
    ): Result<List<PullRequest>, DomainError.Network> {
        val result = safeApiCall {
            repositoryApi.getPullRequests(owner.value, repo.value)
        }
        return when (result) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Error -> Result.Error(result.error)
        }
    }
}
