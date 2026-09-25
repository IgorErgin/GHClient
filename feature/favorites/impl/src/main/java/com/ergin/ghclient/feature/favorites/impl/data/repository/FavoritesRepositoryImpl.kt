package com.ergin.ghclient.feature.favorites.impl.data.repository

import com.ergin.ghclient.core.database.dao.RepoDao
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.feature.favorites.domain.repository.FavoritesRepository
import com.ergin.ghclient.feature.favorites.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.favorites.impl.data.mapper.toEntity
import com.ergin.ghclient.feature.search.domain.model.Repo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val repoDao: RepoDao
) : FavoritesRepository {

    override fun getFavoriteRepos(): Flow<List<Repo>> {
        return repoDao.getFavoriteRepos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavorite(repo: Repo) {
        repoDao.insertRepos(listOf(repo.toEntity()))
    }

    override suspend fun removeFavorite(repoId: RepoId) {
        repoDao.updateFavorite(repoId.value, false)
    }

    override suspend fun isFavorite(repoId: RepoId): Boolean {
        return repoDao.isFavorite(repoId.value)
    }
}
