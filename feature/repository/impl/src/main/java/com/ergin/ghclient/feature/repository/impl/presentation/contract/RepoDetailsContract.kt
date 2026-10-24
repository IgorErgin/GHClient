package com.ergin.ghclient.feature.repository.impl.presentation.contract

import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState
import com.ergin.ghclient.feature.issues.domain.model.Issue
import com.ergin.ghclient.feature.repository.domain.model.Commit
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails

enum class RepoTab(val label: String) {
    README("[README]"),
    TREE("[ДРЕВО]"),
    COMMITS("[КОММИТЫ]"),
    ISSUES("[ЗАДАЧИ]"),
    RELEASES("[РЕЛИЗЫ]")
}

data class RepoDetailsState(
    val owner: String = "",
    val repoName: String = "",
    val details: RepoDetails? = null,
    val readmeContent: String? = null,
    val fileNodes: List<FileNode> = emptyList(),
    val commits: List<Commit> = emptyList(),
    val issues: List<Issue> = emptyList(),
    val pullRequests: List<PullRequest> = emptyList(),
    val selectedTab: RepoTab = RepoTab.README,
    val selectedBranch: String = "master*",
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface RepoDetailsEvent : UiEvent {
    data class LoadDetails(val owner: String, val repoName: String) : RepoDetailsEvent
    data class TabSelected(val tab: RepoTab) : RepoDetailsEvent
    data class BranchSelected(val branch: String) : RepoDetailsEvent
    data object BackClicked : RepoDetailsEvent
    data object CreateIssueClicked : RepoDetailsEvent
    data object RetryClicked : RepoDetailsEvent
    data object SyncClicked : RepoDetailsEvent
}

sealed interface RepoDetailsEffect : UiEffect {
    data object NavigateBack : RepoDetailsEffect
    data class NavigateToCreateIssue(val owner: String, val repoName: String) : RepoDetailsEffect
    data class ShowSnackbar(val message: String) : RepoDetailsEffect
}
