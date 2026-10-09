package com.ergin.ghclient.feature.repository.impl.presentation.ui

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
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.core.ui.components.CrtScreenOverlay
import com.ergin.ghclient.core.ui.components.ErrorView
import com.ergin.ghclient.core.ui.components.LoadingView
import com.ergin.ghclient.core.ui.components.OfflineBanner
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails
import com.ergin.ghclient.feature.repository.impl.presentation.RepoDetailsViewModel
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoDetailsEffect
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoDetailsEvent
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoDetailsState
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoTab
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.BranchTelemetryBar
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.CreateIssueActionButton
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.FileCassettesCard
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.RepoCommitsCassette
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.RepoContentCassetteBlock
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.RepoDetailsTabs
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.RepoHardwareStatusStrip
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.RepoInspectorHeader
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.RepoMetricsGauges
import com.ergin.ghclient.feature.repository.impl.presentation.ui.components.RepoReleasesCassette
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RepoDetailsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToCreateIssue: (owner: String, repoName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RepoDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                RepoDetailsEffect.NavigateBack -> onNavigateBack()
                is RepoDetailsEffect.NavigateToCreateIssue -> onNavigateToCreateIssue(effect.owner, effect.repoName)
                is RepoDetailsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    RepoDetailsScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun RepoDetailsScreen(
    uiState: RepoDetailsState,
    onEvent: (RepoDetailsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column {
                    RepoInspectorHeader(
                        onBackClick = { onEvent(RepoDetailsEvent.BackClicked) }
                    )
                    if (uiState.isOffline) {
                        OfflineBanner(onSyncClick = { onEvent(RepoDetailsEvent.SyncClicked) })
                    }
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

                // Real Hardware Status Strip
                RepoHardwareStatusStrip(
                    owner = uiState.details?.ownerName?.value ?: uiState.owner,
                    repoName = uiState.details?.name ?: uiState.repoName
                )

                // Real Branch & Telemetry Bar
                BranchTelemetryBar(
                    selectedBranch = uiState.selectedBranch,
                    onBranchClick = { onEvent(RepoDetailsEvent.BranchSelected("master*")) },
                    language = uiState.details?.language
                )

                when {
                    uiState.isLoading -> {
                        LoadingView()
                    }
                    uiState.errorMessage != null -> {
                        ErrorView(
                            message = uiState.errorMessage,
                            onRetry = { onEvent(RepoDetailsEvent.RetryClicked) }
                        )
                    }
                    else -> {
                        val details = uiState.details
                        val stars = details?.stars ?: 0
                        val forks = details?.forks ?: 0
                        val issues = details?.openIssuesCount ?: 0
                        val desc = details?.description ?: "Описание репозитория отсутствует"

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Gauges Grid with Real API Data
                            item {
                                RepoMetricsGauges(
                                    starsCount = stars,
                                    forksCount = forks,
                                    issuesCount = issues
                                )
                            }

                            // Toggle Tabs
                            item {
                                RepoDetailsTabs(
                                    selectedTab = uiState.selectedTab,
                                    onTabSelected = { onEvent(RepoDetailsEvent.TabSelected(it)) }
                                )
                            }

                            // Dynamic Tab Content Display
                            item {
                                when (uiState.selectedTab) {
                                    RepoTab.README -> {
                                        RepoContentCassetteBlock(
                                            moduleTitle = "# ${details?.name ?: uiState.repoName}",
                                            descriptionText = desc,
                                            readmeText = uiState.readmeContent
                                        )
                                    }
                                    RepoTab.TREE -> {
                                        FileCassettesCard(fileNodes = uiState.fileNodes)
                                    }
                                    RepoTab.COMMITS -> {
                                        RepoCommitsCassette(commits = uiState.commits)
                                    }
                                    RepoTab.RELEASES -> {
                                        RepoReleasesCassette(pullRequests = uiState.pullRequests)
                                    }
                                }
                            }

                            // Create Issue Action Button
                            item {
                                CreateIssueActionButton(
                                    onCreateIssueClick = { onEvent(RepoDetailsEvent.CreateIssueClicked) }
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

@Preview(showBackground = true, name = "Repo Details - Real Data Success State")
@Composable
fun RepoDetailsScreenSuccessPreview() {
    GHClientTheme {
        RepoDetailsScreen(
            uiState = RepoDetailsState(
                owner = "jetbrains",
                repoName = "kotlin",
                details = RepoDetails(
                    id = RepoId(1L),
                    name = "kotlin",
                    description = "Язык программирования Kotlin. Корутины, мультиплатформа и K2 компилятор.",
                    language = "Kotlin",
                    stars = 47820,
                    forks = 5618,
                    openIssuesCount = 132,
                    ownerName = OwnerName("jetbrains"),
                    ownerAvatarUrl = "",
                    defaultBranch = "master"
                )
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Repo Details - Loading State")
@Composable
fun RepoDetailsScreenLoadingPreview() {
    GHClientTheme {
        RepoDetailsScreen(
            uiState = RepoDetailsState(
                owner = "jetbrains",
                repoName = "kotlin",
                isLoading = true
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Repo Details - Offline State")
@Composable
fun RepoDetailsScreenOfflinePreview() {
    GHClientTheme {
        RepoDetailsScreen(
            uiState = RepoDetailsState(
                owner = "jetbrains",
                repoName = "kotlin",
                isOffline = true
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Repo Details - Error State")
@Composable
fun RepoDetailsScreenErrorPreview() {
    GHClientTheme {
        RepoDetailsScreen(
            uiState = RepoDetailsState(
                owner = "jetbrains",
                repoName = "kotlin",
                errorMessage = "Ошибка подключения к сети ГОСТ"
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
