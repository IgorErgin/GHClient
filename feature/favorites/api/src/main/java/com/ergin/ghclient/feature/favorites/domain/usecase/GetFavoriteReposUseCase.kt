package com.ergin.ghclient.feature.favorites.domain.usecase

import com.ergin.ghclient.feature.favorites.domain.repository.FavoritesRepository
import com.ergin.ghclient.feature.search.domain.model.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteReposUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<Repo>> {
        return favoritesRepository.getFavoriteRepos()
    }
}
