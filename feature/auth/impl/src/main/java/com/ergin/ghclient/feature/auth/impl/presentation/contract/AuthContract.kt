package com.ergin.ghclient.feature.auth.impl.presentation.contract

import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState

data class AuthState(
    val tokenInput: String = "ghp_9A8174b09C883dE11",
    val isOfflineModeEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isAuthorized: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface AuthEvent : UiEvent {
    data class TokenInputChanged(val token: String) : AuthEvent
    data class OfflineModeToggled(val enabled: Boolean) : AuthEvent
    data class OAuthCodeReceived(val code: String) : AuthEvent
    data object SubmitTokenClicked : AuthEvent
    data object LoginWithBrowserClicked : AuthEvent
    data object GuestModeClicked : AuthEvent
}

sealed interface AuthEffect : UiEffect {
    data object NavigateToHome : AuthEffect
    data class OpenOAuthWebPage(val url: String) : AuthEffect
    data class ShowSnackbar(val message: String) : AuthEffect
}
