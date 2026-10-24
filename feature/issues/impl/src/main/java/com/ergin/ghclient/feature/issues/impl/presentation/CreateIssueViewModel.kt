package com.ergin.ghclient.feature.issues.impl.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.issues.domain.usecase.CreateIssueUseCase
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueEffect
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueEvent
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
class CreateIssueViewModel @Inject constructor(
    private val createIssueUseCase: CreateIssueUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ownerParam: String = savedStateHandle["owner"] ?: "jetbrains"
    private val repoNameParam: String = savedStateHandle["repoName"] ?: "kotlin"

    private val _uiState = MutableStateFlow(
        CreateIssueState(owner = ownerParam, repoName = repoNameParam)
    )
    val uiState: StateFlow<CreateIssueState> = _uiState.asStateFlow()

    private val _effect = Channel<CreateIssueEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: CreateIssueEvent) {
        when (event) {
            is CreateIssueEvent.InitData -> {
                _uiState.update { it.copy(owner = event.owner, repoName = event.repoName) }
            }
            is CreateIssueEvent.TitleChanged -> {
                _uiState.update { it.copy(titleInput = event.title) }
            }
            is CreateIssueEvent.BodyChanged -> {
                _uiState.update { it.copy(bodyInput = event.body) }
            }
            CreateIssueEvent.SubmitClicked -> submitIssue()
            CreateIssueEvent.BackClicked -> {
                viewModelScope.launch { _effect.send(CreateIssueEffect.NavigateBack) }
            }
        }
    }

    private fun submitIssue() {
        val title = _uiState.value.titleInput.trim()
        val body = _uiState.value.bodyInput.trim()

        if (title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите заголовок рапорта (Title)") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val owner = OwnerName(_uiState.value.owner)
                val repo = RepoName(_uiState.value.repoName)

                when (val result = createIssueUseCase(owner, repo, title, body)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                        _effect.send(CreateIssueEffect.ShowSnackbar("Рапорт успешно сформирован и отправлен!"))
                        _effect.send(CreateIssueEffect.NavigateBack)
                    }
                    is Result.Error -> {
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
                        errorMessage = "Ошибка при формировании рапорта"
                    )
                }
            }
        }
    }

    private fun mapErrorToMessage(error: DomainError.Network): String {
        return when (error) {
            DomainError.Network.NO_INTERNET -> "Отсутствует подключение к сети ГОСТ"
            DomainError.Network.TIMEOUT -> "Превышено время ожидания сервера"
            DomainError.Network.UNAUTHORIZED -> "Требуется авторизация для создания рапорта"
            DomainError.Network.NOT_FOUND -> "Репозиторий не найден"
            DomainError.Network.SERVER_ERROR -> "Ошибка сервера GitHub"
            DomainError.Network.UNKNOWN -> "Неизвестная ошибка сети"
        }
    }
}
