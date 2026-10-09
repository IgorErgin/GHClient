package com.ergin.ghclient.feature.auth.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.model.AccessToken
import com.ergin.ghclient.feature.auth.domain.usecase.ExchangeOAuthCodeUseCase
import com.ergin.ghclient.feature.auth.domain.usecase.ObserveAuthStateUseCase
import com.ergin.ghclient.feature.auth.domain.usecase.SaveTokenUseCase
import com.ergin.ghclient.feature.auth.impl.BuildConfig
import com.ergin.ghclient.feature.auth.impl.presentation.contract.AuthEffect
import com.ergin.ghclient.feature.auth.impl.presentation.contract.AuthEvent
import com.ergin.ghclient.feature.auth.impl.presentation.contract.AuthState
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
class AuthViewModel @Inject constructor(
    private val saveTokenUseCase: SaveTokenUseCase,
    private val exchangeOAuthCodeUseCase: ExchangeOAuthCodeUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _effect = Channel<AuthEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeAuthStateUseCase().collectLatest { isAuth ->
                _uiState.update { it.copy(isAuthorized = isAuth) }
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.TokenInputChanged -> {
                _uiState.update { it.copy(tokenInput = event.token) }
            }
            is AuthEvent.OfflineModeToggled -> {
                _uiState.update { it.copy(isOfflineModeEnabled = event.enabled) }
            }
            is AuthEvent.OAuthCodeReceived -> {
                exchangeOAuthCode(event.code)
            }
            AuthEvent.SubmitTokenClicked -> {
                submitToken()
            }
            AuthEvent.LoginWithBrowserClicked -> {
                val clientId = BuildConfig.GITHUB_CLIENT_ID
                val oauthUrl = "https://github.com/login/oauth/authorize?client_id=$clientId&scope=repo,user,read:org&redirect_uri=ghclient://oauth/callback"
                viewModelScope.launch {
                    _effect.send(AuthEffect.OpenOAuthWebPage(oauthUrl))
                }
            }
            AuthEvent.GuestModeClicked -> {
                viewModelScope.launch {
                    _effect.send(AuthEffect.NavigateToHome)
                }
            }
        }
    }

    private fun exchangeOAuthCode(code: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                when (exchangeOAuthCodeUseCase(code)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, isAuthorized = true) }
                        _effect.send(AuthEffect.NavigateToHome)
                    }
                    is Result.Error -> {
                        val errorText = if (BuildConfig.GITHUB_CLIENT_ID.contains("YOUR_")) {
                            "Укажите реальный Client ID & Secret в local.properties!"
                        } else {
                            "Ошибка обмена кода OAuth на токен"
                        }
                        _uiState.update { it.copy(isLoading = false, errorMessage = errorText) }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update { it.copy(isLoading = false, errorMessage = "Ошибка OAuth авторизации") }
            }
        }
    }

    private fun submitToken() {
        val token = _uiState.value.tokenInput.trim()
        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите маркер доступа (token)") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                when (saveTokenUseCase(AccessToken(token))) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, isAuthorized = true) }
                        _effect.send(AuthEffect.NavigateToHome)
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Ошибка сохранения токена") }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update { it.copy(isLoading = false, errorMessage = "Ошибка авторизации") }
            }
        }
    }
}
