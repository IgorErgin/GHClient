package com.ergin.ghclient.feature.upload.impl.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.feature.upload.impl.domain.usecase.EnqueueFileUploadUseCase
import com.ergin.ghclient.feature.upload.impl.presentation.contract.UploadEffect
import com.ergin.ghclient.feature.upload.impl.presentation.contract.UploadEvent
import com.ergin.ghclient.feature.upload.impl.presentation.contract.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val enqueueFileUploadUseCase: EnqueueFileUploadUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ownerParam: String = savedStateHandle["owner"] ?: "jetbrains"
    private val repoNameParam: String = savedStateHandle["repoName"] ?: "kotlin"

    private val _uiState = MutableStateFlow(UploadState(owner = ownerParam, repoName = repoNameParam))
    val uiState: StateFlow<UploadState> = _uiState.asStateFlow()

    private val _effect = Channel<UploadEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: UploadEvent) {
        when (event) {
            is UploadEvent.FileSelected -> {
                _uiState.update {
                    it.copy(
                        selectedFileUri = event.uri,
                        selectedFileName = event.name ?: "file_dump.bin",
                        selectedFileSizeKb = event.sizeBytes / 1024,
                        errorMessage = null
                    )
                }
            }
            is UploadEvent.PathInputChanged -> {
                _uiState.update { it.copy(pathInput = event.path) }
            }
            is UploadEvent.MessageInputChanged -> {
                _uiState.update { it.copy(messageInput = event.message) }
            }
            UploadEvent.SubmitClicked -> submitUpload()
            UploadEvent.BackClicked -> {
                viewModelScope.launch { _effect.send(UploadEffect.NavigateBack) }
            }
        }
    }

    private fun submitUpload() {
        val state = _uiState.value
        val uri = state.selectedFileUri
        if (uri == null) {
            _uiState.update { it.copy(errorMessage = "Выберите дамп-файл для передачи") }
            return
        }
        if (state.pathInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Укажите целевой путь сохранения в репозитории") }
            return
        }

        viewModelScope.launch {
            try {
                enqueueFileUploadUseCase(
                    owner = state.owner,
                    repo = state.repoName,
                    path = state.pathInput.trim(),
                    message = state.messageInput.ifBlank { "Upload file via GHClient WorkManager" },
                    fileUriString = uri.toString()
                )

                _uiState.update { it.copy(isEnqueued = true, errorMessage = null) }
                _effect.send(UploadEffect.ShowSnackbar("Задача передачи поставлена в очередь WorkManager!"))
                _effect.send(UploadEffect.NavigateBack)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Ошибка постановки задачи в очередь WorkManager") }
            }
        }
    }
}
