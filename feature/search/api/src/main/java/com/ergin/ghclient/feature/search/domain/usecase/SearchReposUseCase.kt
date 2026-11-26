package com.ergin.ghclient.feature.search.domain.usecase

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchReposUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String, page: Int, perPage: Int = 30): Result<List<Repo>, DomainError.Network> {
        if (query.isBlank()) return Result.Success(emptyList())
        return searchRepository.searchRepositories(query, page, perPage)
    }

    fun getCachedRepositories(): Flow<List<Repo>> {
        return searchRepository.getCachedRepositories()
    }
}
