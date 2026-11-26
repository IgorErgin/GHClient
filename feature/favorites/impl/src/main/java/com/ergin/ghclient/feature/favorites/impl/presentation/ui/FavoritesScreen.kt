package com.ergin.ghclient.feature.favorites.impl.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.ergin.ghclient.feature.favorites.impl.presentation.FavoritesViewModel
import com.ergin.ghclient.feature.favorites.impl.presentation.contract.FavoritesEffect
import com.ergin.ghclient.feature.favorites.impl.presentation.contract.FavoritesEvent
import com.ergin.ghclient.feature.favorites.impl.presentation.contract.FavoritesState
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.impl.presentation.ui.components.PunchcardRepoCard
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FavoritesRoute(
    onNavigateToDetails: (owner: String, repoName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FavoritesEffect.NavigateToDetails -> {
                    onNavigateToDetails(effect.owner, effect.repoName)
                }
                is FavoritesEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    FavoritesScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun FavoritesScreen(
    uiState: FavoritesState,
    onEvent: (FavoritesEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    shape = CutCornerShape(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "[02] ПЗУ // КЭШ-СЕКТОР-02",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "[ ЭВМ-86 ]",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Text(
                            text = "СОХРАНЁННЫЕ РЕПОЗИТОРИИ ДЛЯ АВТОНОМНОЙ РАБОТЫ В СЕТИ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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

                when {
                    uiState.isLoading -> {
                        LoadingView()
                    }
                    uiState.errorMessage != null -> {
                        ErrorView(
                            message = uiState.errorMessage,
                            onRetry = { onEvent(FavoritesEvent.LoadFavorites) }
                        )
                    }
                    uiState.favoriteRepos.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Память ПЗУ пуста. Добавьте репозитории в избранное.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.favoriteRepos,
                                key = { it.id.value }
                            ) { repo ->
                                PunchcardRepoCard(
                                    repo = repo,
                                    onClick = { onEvent(FavoritesEvent.RepoClicked(repo)) },
                                    onFavoriteClick = { onEvent(FavoritesEvent.RemoveFavoriteClicked(repo)) }
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

@Preview(showBackground = true, name = "Favorites Screen - Success State")
@Composable
fun FavoritesScreenSuccessPreview() {
    GHClientTheme {
        FavoritesScreen(
            uiState = FavoritesState(
                favoriteRepos = listOf(
                    Repo(
                        id = RepoId(1L),
                        name = "kotlin",
                        description = "Кэшированный репозиторий в ПЗУ памяти.",
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

@Preview(showBackground = true, name = "Favorites Screen - Empty State")
@Composable
fun FavoritesScreenEmptyPreview() {
    GHClientTheme {
        FavoritesScreen(
            uiState = FavoritesState(favoriteRepos = emptyList()),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
