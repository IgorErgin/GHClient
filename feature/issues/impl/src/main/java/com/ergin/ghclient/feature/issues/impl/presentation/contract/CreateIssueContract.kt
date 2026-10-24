package com.ergin.ghclient.feature.issues.impl.presentation.contract

import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState

data class CreateIssueState(
    val owner: String = "jetbrains",
    val repoName: String = "kotlin",
    val titleInput: String = "",
    val bodyInput: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface CreateIssueEvent : UiEvent {
    data class InitData(val owner: String, val repoName: String) : CreateIssueEvent
    data class TitleChanged(val title: String) : CreateIssueEvent
    data class BodyChanged(val body: String) : CreateIssueEvent
    data object SubmitClicked : CreateIssueEvent
    data object BackClicked : CreateIssueEvent
}

sealed interface CreateIssueEffect : UiEffect {
    data object NavigateBack : CreateIssueEffect
    data class ShowSnackbar(val message: String) : CreateIssueEffect
}
