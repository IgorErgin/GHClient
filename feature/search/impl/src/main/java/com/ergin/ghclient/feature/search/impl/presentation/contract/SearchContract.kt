package com.ergin.ghclient.feature.search.impl.presentation.contract

import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState
import com.ergin.ghclient.feature.search.domain.model.Repo

data class SearchState(
    val query: String = "FIND_REPO --FILTER=ALL",
    val repos: List<Repo> = emptyList(),
    val selectedLanguage: String? = null,
    val availableLanguages: List<String> = listOf("ВСЕ", "KOTLIN", "RUST", "TYPESCRIPT", "C/ASM"),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface SearchEvent : UiEvent {
    data class QueryChanged(val query: String) : SearchEvent
    data class LanguageFilterSelected(val language: String?) : SearchEvent
    data object SearchClicked : SearchEvent
    data class RepoClicked(val repo: Repo) : SearchEvent
    data class ToggleFavoriteClicked(val repo: Repo) : SearchEvent
    data object RetryClicked : SearchEvent
    data object SyncClicked : SearchEvent
}

sealed interface SearchEffect : UiEffect {
    data class NavigateToDetails(val owner: String, val repoName: String) : SearchEffect
    data class ShowSnackbar(val message: String) : SearchEffect
}
