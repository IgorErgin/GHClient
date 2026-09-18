package com.ergin.ghclient.feature.favorites.domain.repository

import com.ergin.ghclient.feature.search.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteRepos(): Flow<List<Repo>>
    
    suspend fun addFavorite(repo: Repo)
    suspend fun removeFavorite(repoId: Long)
    suspend fun isFavorite(repoId: Long): Boolean
}
