package com.ergin.ghclient.feature.favorites.impl.presentation.contract

import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState
import com.ergin.ghclient.feature.search.domain.model.Repo

data class FavoritesState(
    val favoriteRepos: List<Repo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface FavoritesEvent : UiEvent {
    data object LoadFavorites : FavoritesEvent
    data class RepoClicked(val repo: Repo) : FavoritesEvent
    data class RemoveFavoriteClicked(val repo: Repo) : FavoritesEvent
}

sealed interface FavoritesEffect : UiEffect {
    data class NavigateToDetails(val owner: String, val repoName: String) : FavoritesEffect
    data class ShowSnackbar(val message: String) : FavoritesEffect
}
