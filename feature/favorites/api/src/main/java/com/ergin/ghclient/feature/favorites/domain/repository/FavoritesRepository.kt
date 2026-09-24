package com.ergin.ghclient.feature.favorites.domain.repository

import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.feature.search.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteRepos(): Flow<List<Repo>>
    
    suspend fun addFavorite(repo: Repo)
    suspend fun removeFavorite(repoId: RepoId)
    suspend fun isFavorite(repoId: RepoId): Boolean
}
