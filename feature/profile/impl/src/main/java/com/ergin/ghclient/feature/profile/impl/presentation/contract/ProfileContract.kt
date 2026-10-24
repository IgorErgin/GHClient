package com.ergin.ghclient.feature.profile.impl.presentation.contract

import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.feature.profile.domain.model.UserProfile
import com.ergin.ghclient.feature.search.domain.model.Repo

data class ProfileState(
    val profile: UserProfile? = null,
    val userRepos: List<Repo> = emptyList(),
    val activities: List<UserActivity> = emptyList(),
    val isSafetyUnlocked: Boolean = false,
    val actionMessage: String? = null,
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface ProfileEvent : UiEvent {
    data object LoadProfile : ProfileEvent
    data object SafetyUnlockedToggled : ProfileEvent
    data object LogoutClicked : ProfileEvent
    data class RepoClicked(val repo: Repo) : ProfileEvent
    data class ToggleFavoriteClicked(val repo: Repo) : ProfileEvent
    data object HealActionClicked : ProfileEvent
    data object FeedActionClicked : ProfileEvent
    data object VacationActionClicked : ProfileEvent
    data object RetryClicked : ProfileEvent
}

sealed interface ProfileEffect : UiEffect {
    data object NavigateToAuth : ProfileEffect
    data class NavigateToRepoDetails(val owner: String, val repoName: String) : ProfileEffect
    data class ShowSnackbar(val message: String) : ProfileEffect
}
