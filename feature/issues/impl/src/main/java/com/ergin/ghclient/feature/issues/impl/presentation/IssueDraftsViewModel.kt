package com.ergin.ghclient.feature.issues.impl.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.feature.issues.domain.usecase.DeleteIssueDraftUseCase
import com.ergin.ghclient.feature.issues.domain.usecase.ObserveIssueDraftsUseCase
import com.ergin.ghclient.feature.issues.domain.usecase.RefreshIssueDraftsUseCase
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsEffect
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsEvent
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsState
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
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class IssueDraftsViewModel @Inject constructor(
    private val observeIssueDraftsUseCase: ObserveIssueDraftsUseCase,
    private val refreshIssueDraftsUseCase: RefreshIssueDraftsUseCase,
    private val deleteIssueDraftUseCase: DeleteIssueDraftUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ownerParam: String = savedStateHandle["owner"] ?: ""
    private val repoNameParam: String = savedStateHandle["repoName"] ?: ""

    private val _uiState = MutableStateFlow(
        IssueDraftsState(owner = ownerParam, repoName = repoNameParam)
    )
    val uiState: StateFlow<IssueDraftsState> = _uiState.asStateFlow()

    private val _effect = Channel<IssueDraftsEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeIssueDraftsUseCase().collectLatest { allDrafts ->
                val filtered = if (ownerParam.isNotBlank() && repoNameParam.isNotBlank()) {
                    allDrafts.filter { it.owner.equals(ownerParam, ignoreCase = true) && it.repoName.equals(repoNameParam, ignoreCase = true) }
                } else {
                    allDrafts
                }
                _uiState.update {
                    it.copy(
                        drafts = filtered,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: IssueDraftsEvent) {
        when (event) {
            IssueDraftsEvent.RefreshClicked -> refreshDrafts()
            is IssueDraftsEvent.DraftClicked -> {
                viewModelScope.launch {
                    val state = _uiState.value
                    _effect.send(IssueDraftsEffect.NavigateToEditor(state.owner, state.repoName, event.uid))
                }
            }
            IssueDraftsEvent.CreateDraftClicked -> {
                viewModelScope.launch {
                    val state = _uiState.value
                    _effect.send(IssueDraftsEffect.NavigateToEditor(state.owner, state.repoName, null))
                }
            }
            is IssueDraftsEvent.DeleteDraftClicked -> deleteDraft(event.uid)
            IssueDraftsEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(IssueDraftsEffect.NavigateBack)
                }
            }
        }
    }

    private fun refreshDrafts() {
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                refreshIssueDraftsUseCase()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                if (e is IOException) {
                    _effect.send(IssueDraftsEffect.ShowSnackbar("Ошибка сети ГОСТ: ${e.message ?: "Сбой соединения"}"))
                } else {
                    _effect.send(IssueDraftsEffect.ShowSnackbar("Ошибка обновления: ${e.localizedMessage}"))
                }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun deleteDraft(uid: String) {
        viewModelScope.launch {
            try {
                deleteIssueDraftUseCase(uid)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                if (e is IOException) {
                    _effect.send(IssueDraftsEffect.ShowSnackbar("Ошибка при удалении на сервере ГОСТ: ${e.message}"))
                } else {
                    _effect.send(IssueDraftsEffect.ShowSnackbar("Ошибка удаления"))
                }
            }
        }
    }
}
