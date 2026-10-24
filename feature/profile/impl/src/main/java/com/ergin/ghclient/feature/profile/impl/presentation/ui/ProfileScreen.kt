package com.ergin.ghclient.feature.profile.impl.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ergin.ghclient.core.domain.model.UserId
import com.ergin.ghclient.core.ui.components.CrtScreenOverlay
import com.ergin.ghclient.core.ui.components.ErrorView
import com.ergin.ghclient.core.ui.components.LoadingView
import com.ergin.ghclient.core.ui.components.OfflineBanner
import com.ergin.ghclient.feature.profile.domain.model.UserProfile
import com.ergin.ghclient.feature.profile.impl.presentation.ProfileViewModel
import com.ergin.ghclient.feature.profile.impl.presentation.contract.ProfileEffect
import com.ergin.ghclient.feature.profile.impl.presentation.contract.ProfileEvent
import com.ergin.ghclient.feature.profile.impl.presentation.contract.ProfileState
import com.ergin.ghclient.feature.profile.impl.presentation.ui.components.ProfileContributionMatrix
import com.ergin.ghclient.feature.profile.impl.presentation.ui.components.ProfileDossierChassis
import com.ergin.ghclient.feature.profile.impl.presentation.ui.components.ProfileHeader
import com.ergin.ghclient.feature.profile.impl.presentation.ui.components.ProfileLogoutSection
import com.ergin.ghclient.feature.profile.impl.presentation.ui.components.ProfileUserReposCassette
import com.ergin.ghclient.feature.profile.impl.presentation.ui.components.ProfileVitalStrip
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileRoute(
    onNavigateToAuth: () -> Unit,
    onNavigateToDetails: (owner: String, repoName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.NavigateToAuth -> onNavigateToAuth()
                is ProfileEffect.NavigateToRepoDetails -> onNavigateToDetails(effect.owner, effect.repoName)
                is ProfileEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ProfileScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column {
                    ProfileHeader()
                    if (uiState.isOffline) {
                        OfflineBanner(onSyncClick = { onEvent(ProfileEvent.LoadProfile) })
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(2.dp))

                when {
                    uiState.isLoading -> {
                        LoadingView()
                    }
                    uiState.errorMessage != null -> {
                        ErrorView(
                            message = uiState.errorMessage,
                            onRetry = { onEvent(ProfileEvent.RetryClicked) }
                        )
                    }
                    else -> {
                        val profile = uiState.profile
                        val repos = profile?.publicRepos ?: 0
                        val followers = profile?.followers ?: 0
                        val following = profile?.following ?: 0

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                ProfileDossierChassis(
                                    profile = profile
                                )
                            }

                            item {
                                ProfileVitalStrip(
                                    reposCount = repos,
                                    followersCount = followers,
                                    followingCount = following
                                )
                            }

                            // Real User Repositories
                            item {
                                ProfileUserReposCassette(
                                    userRepos = uiState.userRepos,
                                    onRepoClick = { onEvent(ProfileEvent.RepoClicked(it)) },
                                    onFavoriteClick = { onEvent(ProfileEvent.ToggleFavoriteClicked(it)) }
                                )
                            }

                            // Real Activity Matrix (Commits / Events)
                            item {
                                ProfileContributionMatrix(
                                    activities = uiState.activities
                                )
                            }

                            item {
                                ProfileLogoutSection(
                                    isSafetyUnlocked = uiState.isSafetyUnlocked,
                                    onSafetyUnlockToggle = { onEvent(ProfileEvent.SafetyUnlockedToggled) },
                                    onLogoutClick = { onEvent(ProfileEvent.LogoutClicked) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// === PREVIEWS ===

@Preview(showBackground = true, name = "Profile Screen - Success State")
@Composable
fun ProfileScreenSuccessPreview() {
    GHClientTheme {
        ProfileScreen(
            uiState = ProfileState(
                profile = UserProfile(
                    id = UserId(101L),
                    login = "cyberdev",
                    name = "Алексей «CyberDev»",
                    avatarUrl = "",
                    bio = "Инженер-программист 1 категории. Корутины, KMP и компиляция байткода.",
                    publicRepos = 42,
                    followers = 1800,
                    following = 99
                )
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen - Loading State")
@Composable
fun ProfileScreenLoadingPreview() {
    GHClientTheme {
        ProfileScreen(
            uiState = ProfileState(isLoading = true),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen - Offline State")
@Composable
fun ProfileScreenOfflinePreview() {
    GHClientTheme {
        ProfileScreen(
            uiState = ProfileState(isOffline = true),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen - Error State")
@Composable
fun ProfileScreenErrorPreview() {
    GHClientTheme {
        ProfileScreen(
            uiState = ProfileState(errorMessage = "Ошибка чтения формуляра оператора"),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
