package com.ergin.ghclient.feature.search.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.search.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchRepositories(query: String, page: Int, perPage: Int): Result<List<Repo>, DomainError.Network>
    
    fun getCachedRepositories(): Flow<List<Repo>>
}
