package com.ergin.ghclient.feature.issues.impl.presentation.contract

import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft

data class IssueDraftsState(
    val owner: String = "",
    val repoName: String = "",
    val drafts: List<IssueDraft> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = true
) : UiState

sealed interface IssueDraftsEvent : UiEvent {
    data object RefreshClicked : IssueDraftsEvent
    data class DraftClicked(val uid: String) : IssueDraftsEvent
    data object CreateDraftClicked : IssueDraftsEvent
    data class DeleteDraftClicked(val uid: String) : IssueDraftsEvent
    data object BackClicked : IssueDraftsEvent
}

sealed interface IssueDraftsEffect : UiEffect {
    data class NavigateToEditor(val owner: String, val repoName: String, val draftUid: String? = null) : IssueDraftsEffect
    data object NavigateBack : IssueDraftsEffect
    data class ShowSnackbar(val message: String) : IssueDraftsEffect
}
