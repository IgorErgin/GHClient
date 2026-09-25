package com.ergin.ghclient.feature.issues.impl.data.repository

import com.ergin.ghclient.core.database.dao.IssueDao
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.core.network.safeApiCall
import com.ergin.ghclient.feature.issues.domain.model.Issue
import com.ergin.ghclient.feature.issues.domain.repository.IssueRepository
import com.ergin.ghclient.feature.issues.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.issues.impl.data.mapper.toEntity
import com.ergin.ghclient.feature.issues.impl.data.remote.IssueApi
import com.ergin.ghclient.feature.issues.impl.data.remote.model.CreateIssueRequestDto
import javax.inject.Inject

class IssueRepositoryImpl @Inject constructor(
    private val issueApi: IssueApi,
    private val issueDao: IssueDao
) : IssueRepository {

    override suspend fun createIssue(
        owner: OwnerName,
        repo: RepoName,
        title: String,
        body: String
    ): Result<Unit, DomainError.Network> {
        val result = safeApiCall {
            issueApi.createIssue(
                owner = owner.value,
                repo = repo.value,
                body = CreateIssueRequestDto(title = title, body = body)
            )
        }
        return when (result) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun getIssues(
        owner: OwnerName,
        repo: RepoName
    ): Result<List<Issue>, DomainError.Network> {
        val result = safeApiCall {
            issueApi.getIssues(owner.value, repo.value)
        }
        return when (result) {
            is Result.Success -> {
                val issues = result.data.map { it.toDomain() }
                issueDao.insertIssues(issues.map { it.toEntity(owner.value, repo.value) })
                Result.Success(issues)
            }
            is Result.Error -> {
                if (result.error == DomainError.Network.NO_INTERNET) {
                    val cachedIssues = issueDao.getIssues(owner.value, repo.value)
                    if (cachedIssues.isNotEmpty()) {
                        return Result.Success(cachedIssues.map { it.toDomain() })
                    }
                }
                Result.Error(result.error)
            }
        }
    }
}
