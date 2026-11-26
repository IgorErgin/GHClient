package com.ergin.ghclient.feature.favorites.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.feature.favorites.domain.usecase.GetFavoriteReposUseCase
import com.ergin.ghclient.feature.favorites.domain.usecase.ToggleFavoriteUseCase
import com.ergin.ghclient.feature.favorites.impl.presentation.contract.FavoritesEffect
import com.ergin.ghclient.feature.favorites.impl.presentation.contract.FavoritesEvent
import com.ergin.ghclient.feature.favorites.impl.presentation.contract.FavoritesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteReposUseCase: GetFavoriteReposUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState: StateFlow<FavoritesState> = _uiState.asStateFlow()

    private val _effect = Channel<FavoritesEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    init {
        observeFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            FavoritesEvent.LoadFavorites -> observeFavorites()
            is FavoritesEvent.RepoClicked -> {
                viewModelScope.launch {
                    _effect.send(
                        FavoritesEffect.NavigateToDetails(
                            owner = event.repo.ownerName.value,
                            repoName = event.repo.name
                        )
                    )
                }
            }
            is FavoritesEvent.RemoveFavoriteClicked -> {
                viewModelScope.launch {
                    try {
                        toggleFavoriteUseCase(event.repo)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                    }
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            try {
                getFavoriteReposUseCase().collectLatest { repos ->
                    _uiState.update { it.copy(favoriteRepos = repos, isLoading = false) }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            }
        }
    }
}
