package com.ergin.ghclient.feature.profile.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.auth.domain.usecase.ClearTokenUseCase
import com.ergin.ghclient.feature.profile.domain.usecase.GetUserActivityUseCase
import com.ergin.ghclient.feature.profile.domain.usecase.GetUserProfileUseCase
import com.ergin.ghclient.feature.profile.impl.presentation.contract.ProfileEffect
import com.ergin.ghclient.feature.profile.impl.presentation.contract.ProfileEvent
import com.ergin.ghclient.feature.profile.impl.presentation.contract.ProfileState
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
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserActivityUseCase: GetUserActivityUseCase,
    private val clearTokenUseCase: ClearTokenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private val _effect = Channel<ProfileEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile, ProfileEvent.RetryClicked -> loadProfile()
            ProfileEvent.SafetyUnlockedToggled -> {
                _uiState.update { it.copy(isSafetyUnlocked = !it.isSafetyUnlocked) }
            }
            ProfileEvent.LogoutClicked -> logout()
            ProfileEvent.HealActionClicked -> {
                _uiState.update { it.copy(actionMessage = "ЛЕЧЕНИЕ ЗАВЕРШЕНО") }
            }
            ProfileEvent.FeedActionClicked -> {
                _uiState.update { it.copy(actionMessage = "ПИТАНИЕ ПОДАНО") }
            }
            ProfileEvent.VacationActionClicked -> {
                _uiState.update { it.copy(actionMessage = "ОТКАЗ: СРОКИ!") }
            }
        }
    }

    private fun loadProfile() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                when (val result = getUserProfileUseCase()) {
                    is Result.Success -> {
                        val profile = result.data
                        _uiState.update {
                            it.copy(
                                profile = profile,
                                isLoading = false,
                                isOffline = false,
                                errorMessage = null
                            )
                        }

                        // Load Real User Activity / Commits from GitHub API
                        val activityResult = getUserActivityUseCase(profile.login)
                        if (activityResult is Result.Success) {
                            _uiState.update { it.copy(activities = activityResult.data) }
                        }
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
                        errorMessage = "Ошибка загрузки профиля"
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                clearTokenUseCase()
                _effect.send(ProfileEffect.NavigateToAuth)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            }
        }
    }

    private fun mapErrorToMessage(error: DomainError.Network): String {
        return when (error) {
            DomainError.Network.NO_INTERNET -> "Отсутствует подключение к сети ГОСТ"
            DomainError.Network.TIMEOUT -> "Превышено время ожидания"
            DomainError.Network.UNAUTHORIZED -> "Ошибка авторизации профиля"
            DomainError.Network.NOT_FOUND -> "Профиль оператора не найден"
            DomainError.Network.SERVER_ERROR -> "Ошибка сервера GitHub"
            DomainError.Network.UNKNOWN -> "Неизвестная ошибка профиля"
        }
    }
}
