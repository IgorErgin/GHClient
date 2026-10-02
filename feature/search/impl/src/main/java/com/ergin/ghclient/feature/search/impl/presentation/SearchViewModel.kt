package com.ergin.ghclient.feature.search.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.favorites.domain.usecase.ToggleFavoriteUseCase
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.domain.usecase.SearchReposUseCase
import com.ergin.ghclient.feature.search.impl.presentation.contract.SearchEffect
import com.ergin.ghclient.feature.search.impl.presentation.contract.SearchEvent
import com.ergin.ghclient.feature.search.impl.presentation.contract.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchReposUseCase: SearchReposUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    private val _effect = Channel<SearchEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _uiState.update { it.copy(query = event.query) }
            }
            is SearchEvent.LanguageFilterSelected -> {
                _uiState.update { it.copy(selectedLanguage = event.language) }
                search()
            }
            SearchEvent.SearchClicked, SearchEvent.RetryClicked, SearchEvent.SyncClicked -> {
                search()
            }
            is SearchEvent.RepoClicked -> {
                viewModelScope.launch {
                    _effect.send(
                        SearchEffect.NavigateToDetails(
                            owner = event.repo.ownerName.value,
                            repoName = event.repo.name
                        )
                    )
                }
            }
            is SearchEvent.ToggleFavoriteClicked -> {
                toggleFavorite(event.repo)
            }
        }
    }

    private fun search() {
        val rawQuery = _uiState.value.query.trim()
        val queryToSearch = if (rawQuery.startsWith("FIND_REPO")) {
            rawQuery.removePrefix("FIND_REPO").removePrefix("--FILTER=ALL").trim().ifBlank { "kotlin" }
        } else {
            rawQuery.ifBlank { "kotlin" }
        }

        val lang = _uiState.value.selectedLanguage
        val finalQuery = if (!lang.isNullOrBlank() && lang != "ВСЕ") {
            "$queryToSearch language:${lang.lowercase()}"
        } else {
            queryToSearch
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isOffline = false) }

        viewModelScope.launch {
            try {
                when (val result = searchReposUseCase(query = finalQuery, page = 1)) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                repos = result.data,
                                isLoading = false,
                                isOffline = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        if (result.error == DomainError.Network.NO_INTERNET) {
                            val cachedRepos = searchReposUseCase.getCachedRepositories().first()
                            if (cachedRepos.isNotEmpty()) {
                                _uiState.update {
                                    it.copy(
                                        repos = cachedRepos,
                                        isLoading = false,
                                        isOffline = true,
                                        errorMessage = null
                                    )
                                }
                                return@launch
                            }
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = mapErrorToMessage(result.error)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Произошла непредвиденная ошибка"
                    )
                }
            }
        }
    }

    private fun toggleFavorite(repo: Repo) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(repo)
                search()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            }
        }
    }

    private fun mapErrorToMessage(error: DomainError.Network): String {
        return when (error) {
            DomainError.Network.NO_INTERNET -> "Отсутствует подключение к интернету"
            DomainError.Network.TIMEOUT -> "Превышено время ожидания ответа"
            DomainError.Network.UNAUTHORIZED -> "Ошибка авторизации"
            DomainError.Network.NOT_FOUND -> "Репозитории не найдены"
            DomainError.Network.SERVER_ERROR -> "Ошибка сервера GitHub"
            DomainError.Network.UNKNOWN -> "Неизвестная ошибка сети"
        }
    }
}
