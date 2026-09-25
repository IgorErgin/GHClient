package com.ergin.ghclient.feature.search.impl.data.repository

import com.ergin.ghclient.core.database.dao.RepoDao
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.network.safeApiCall
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.domain.repository.SearchRepository
import com.ergin.ghclient.feature.search.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.search.impl.data.mapper.toEntity
import com.ergin.ghclient.feature.search.impl.data.remote.SearchApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchApi: SearchApi,
    private val repoDao: RepoDao
) : SearchRepository {

    override suspend fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int
    ): Result<List<Repo>, DomainError.Network> {
        val result = safeApiCall {
            searchApi.searchRepositories(query = query, page = page, perPage = perPage)
        }
        return when (result) {
            is Result.Success -> {
                val dtoList = result.data.items
                val entities = dtoList.map { dto ->
                    val isFav = repoDao.isFavorite(dto.id)
                    dto.toEntity(isFavorite = isFav)
                }
                repoDao.insertRepos(entities)
                Result.Success(dtoList.map { it.toDomain() })
            }
            is Result.Error -> {
                if (result.error == DomainError.Network.NO_INTERNET) {
                    val cachedEntities = repoDao.getCachedReposList()
                    if (cachedEntities.isNotEmpty()) {
                        return Result.Success(cachedEntities.map { it.toDomain() })
                    }
                }
                Result.Error(result.error)
            }
        }
    }

    override fun getCachedRepositories(): Flow<List<Repo>> {
        return repoDao.getAllCachedRepos().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
