package com.ergin.ghclient.feature.favorites.domain.usecase

import com.ergin.ghclient.feature.favorites.domain.repository.FavoritesRepository
import com.ergin.ghclient.feature.search.domain.model.Repo
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(repo: Repo) {
        val isFav = favoritesRepository.isFavorite(repo.id)
        if (isFav) {
            favoritesRepository.removeFavorite(repo.id)
        } else {
            favoritesRepository.addFavorite(repo)
        }
    }
}
