package com.ergin.ghclient.feature.search.impl.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.core.ui.components.CrtScreenOverlay
import com.ergin.ghclient.core.ui.components.ErrorView
import com.ergin.ghclient.core.ui.components.LoadingView
import com.ergin.ghclient.core.ui.components.OfflineBanner
import com.ergin.ghclient.core.ui.components.phosphorGlow
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.impl.presentation.SearchViewModel
import com.ergin.ghclient.feature.search.impl.presentation.contract.SearchEffect
import com.ergin.ghclient.feature.search.impl.presentation.contract.SearchEvent
import com.ergin.ghclient.feature.search.impl.presentation.contract.SearchState
import com.ergin.ghclient.feature.search.impl.presentation.ui.components.CommandLineSearchBar
import com.ergin.ghclient.feature.search.impl.presentation.ui.components.DiagnosticSubstrip
import com.ergin.ghclient.feature.search.impl.presentation.ui.components.FilterSwitchesRow
import com.ergin.ghclient.feature.search.impl.presentation.ui.components.MagneticTapeFooter
import com.ergin.ghclient.feature.search.impl.presentation.ui.components.PunchcardRepoCard
import com.ergin.ghclient.feature.search.impl.presentation.ui.components.TerminalTopHeader
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchRoute(
    onNavigateToDetails: (owner: String, repoName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SearchEffect.NavigateToDetails -> {
                    onNavigateToDetails(effect.owner, effect.repoName)
                }
                is SearchEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    SearchScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun SearchScreen(
    uiState: SearchState,
    onEvent: (SearchEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column {
                    TerminalTopHeader()
                    if (uiState.isOffline) {
                        OfflineBanner(onSyncClick = { onEvent(SearchEvent.SyncClicked) })
                    }
                    DiagnosticSubstrip()
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Command Line Search Bar
                CommandLineSearchBar(
                    modifier = Modifier.phosphorGlow(),
                    query = uiState.query,
                    onQueryChanged = { onEvent(SearchEvent.QueryChanged(it)) },
                    onSearchClicked = { onEvent(SearchEvent.SearchClicked) },
                    onClearClicked = { onEvent(SearchEvent.QueryChanged("")) }
                )

                // Language Filter Switches
                FilterSwitchesRow(
                    availableLanguages = uiState.availableLanguages,
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageSelected = { onEvent(SearchEvent.LanguageFilterSelected(it)) }
                )

                when {
                    uiState.isLoading -> {
                        LoadingView()
                    }
                    uiState.errorMessage != null -> {
                        ErrorView(
                            message = uiState.errorMessage,
                            onRetry = { onEvent(SearchEvent.RetryClicked) }
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.repos,
                                key = { it.id.value }
                            ) { repo ->
                                PunchcardRepoCard(
                                    repo = repo,
                                    onClick = { onEvent(SearchEvent.RepoClicked(repo)) },
                                    onFavoriteClick = { onEvent(SearchEvent.ToggleFavoriteClicked(repo)) }
                                )
                            }

                            if (uiState.repos.isNotEmpty()) {
                                item {
                                    MagneticTapeFooter()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// === COMPOSE PREVIEWS FOR ALL SCREEN STATES ===

@Preview(showBackground = true, name = "CRT Cathode Terminal - Success State")
@Composable
fun SearchScreenSuccessPreview() {
    GHClientTheme {
        SearchScreen(
            uiState = SearchState(
                query = "FIND_REPO kotlin",
                repos = listOf(
                    Repo(
                        id = RepoId(1L),
                        name = "kotlin",
                        description = "Язык Kotlin. Мультиплатформенный компилятор и стандартная библиотека.",
                        language = "Kotlin",
                        stars = 47800,
                        ownerName = OwnerName("JetBrains"),
                        ownerAvatarUrl = ""
                    ),
                    Repo(
                        id = RepoId(2L),
                        name = "linux",
                        description = "Исходный код ядра Linux.",
                        language = "C",
                        stars = 176000,
                        ownerName = OwnerName("torvalds"),
                        ownerAvatarUrl = ""
                    )
                ),
                selectedLanguage = "KOTLIN"
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "CRT Cathode Terminal - Loading State")
@Composable
fun SearchScreenLoadingPreview() {
    GHClientTheme {
        SearchScreen(
            uiState = SearchState(
                query = "FIND_REPO rust",
                isLoading = true
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "CRT Cathode Terminal - Offline State")
@Composable
fun SearchScreenOfflinePreview() {
    GHClientTheme {
        SearchScreen(
            uiState = SearchState(
                query = "FIND_REPO",
                isOffline = true,
                repos = listOf(
                    Repo(
                        id = RepoId(1L),
                        name = "kotlin",
                        description = "Кэшированный репозиторий.",
                        language = "Kotlin",
                        stars = 47800,
                        ownerName = OwnerName("JetBrains"),
                        ownerAvatarUrl = ""
                    )
                )
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "CRT Cathode Terminal - Error State")
@Composable
fun SearchScreenErrorPreview() {
    GHClientTheme {
        SearchScreen(
            uiState = SearchState(
                query = "FIND_REPO invalid_query",
                errorMessage = "Отсутствует подключение к интернету"
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
