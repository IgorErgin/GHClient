package com.ergin.ghclient.feature.repository.impl.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.feature.issues.domain.usecase.GetIssuesUseCase
import com.ergin.ghclient.feature.repository.domain.usecase.GetCommitsUseCase
import com.ergin.ghclient.feature.repository.domain.usecase.GetPullRequestsUseCase
import com.ergin.ghclient.feature.repository.domain.usecase.GetRepoContentsUseCase
import com.ergin.ghclient.feature.repository.domain.usecase.GetRepoDetailsUseCase
import com.ergin.ghclient.feature.repository.domain.usecase.GetRepoReadmeUseCase
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoDetailsEffect
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoDetailsEvent
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoDetailsState
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
class RepoDetailsViewModel @Inject constructor(
    private val getRepoDetailsUseCase: GetRepoDetailsUseCase,
    private val getRepoReadmeUseCase: GetRepoReadmeUseCase,
    private val getRepoContentsUseCase: GetRepoContentsUseCase,
    private val getCommitsUseCase: GetCommitsUseCase,
    private val getIssuesUseCase: GetIssuesUseCase,
    private val getPullRequestsUseCase: GetPullRequestsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ownerParam: String = savedStateHandle["owner"] ?: "jetbrains"
    private val repoNameParam: String = savedStateHandle["repoName"] ?: "kotlin"

    private val _uiState = MutableStateFlow(RepoDetailsState(owner = ownerParam, repoName = repoNameParam))
    val uiState: StateFlow<RepoDetailsState> = _uiState.asStateFlow()

    private val _effect = Channel<RepoDetailsEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    init {
        loadData(ownerParam, repoNameParam)
    }

    fun onEvent(event: RepoDetailsEvent) {
        when (event) {
            is RepoDetailsEvent.LoadDetails -> {
                loadData(event.owner, event.repoName)
            }
            is RepoDetailsEvent.TabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tab) }
            }
            is RepoDetailsEvent.BranchSelected -> {
                _uiState.update { it.copy(selectedBranch = event.branch) }
            }
            RepoDetailsEvent.BackClicked -> {
                viewModelScope.launch { _effect.send(RepoDetailsEffect.NavigateBack) }
            }
            RepoDetailsEvent.CreateIssueClicked -> {
                viewModelScope.launch {
                    _effect.send(
                        RepoDetailsEffect.NavigateToCreateIssue(
                            owner = _uiState.value.owner,
                            repoName = _uiState.value.repoName
                        )
                    )
                }
            }
            RepoDetailsEvent.RetryClicked, RepoDetailsEvent.SyncClicked -> {
                loadData(_uiState.value.owner, _uiState.value.repoName)
            }
        }
    }

    private fun loadData(ownerStr: String, repoStr: String) {
        if (ownerStr.isBlank() || repoStr.isBlank()) return

        val owner = OwnerName(ownerStr)
        val repo = RepoName(repoStr)

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isOffline = false) }

        viewModelScope.launch {
            try {
                when (val detailsResult = getRepoDetailsUseCase(owner, repo)) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                details = detailsResult.data,
                                selectedBranch = "${detailsResult.data.defaultBranch}*",
                                isLoading = false,
                                isOffline = false,
                                errorMessage = null
                            )
                        }

                        // Load Readme in background
                        val readmeResult = getRepoReadmeUseCase(owner, repo)
                        if (readmeResult is Result.Success) {
                            _uiState.update { it.copy(readmeContent = readmeResult.data) }
                        }

                        // Load Contents in background
                        val contentsResult = getRepoContentsUseCase(owner, repo, "")
                        if (contentsResult is Result.Success) {
                            _uiState.update { it.copy(fileNodes = contentsResult.data) }
                        }

                        // Load Commits in background
                        val commitsResult = getCommitsUseCase(owner, repo)
                        if (commitsResult is Result.Success) {
                            _uiState.update { it.copy(commits = commitsResult.data) }
                        }

                        // Load Issues in background
                        val issuesResult = getIssuesUseCase(owner, repo)
                        if (issuesResult is Result.Success) {
                            _uiState.update { it.copy(issues = issuesResult.data) }
                        }

                        // Load Pull Requests / Releases in background
                        val prsResult = getPullRequestsUseCase(owner, repo)
                        if (prsResult is Result.Success) {
                            _uiState.update { it.copy(pullRequests = prsResult.data) }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = mapErrorToMessage(detailsResult.error)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Произошла ошибка при загрузке данных"
                    )
                }
            }
        }
    }

    private fun mapErrorToMessage(error: DomainError.Network): String {
        return when (error) {
            DomainError.Network.NO_INTERNET -> "Отсутствует подключение к сети ГОСТ"
            DomainError.Network.TIMEOUT -> "Превышено время ожидания ответа"
            DomainError.Network.UNAUTHORIZED -> "Ошибка авторизации"
            DomainError.Network.NOT_FOUND -> "Репозиторий не найден"
            DomainError.Network.SERVER_ERROR -> "Ошибка сервера GitHub"
            DomainError.Network.UNKNOWN -> "Неизвестная ошибка сети"
        }
    }
}
