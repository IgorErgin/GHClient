package com.ergin.ghclient.feature.issues.impl.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ergin.ghclient.core.ui.components.CrtScreenOverlay
import com.ergin.ghclient.core.ui.components.LoadingView
import com.ergin.ghclient.feature.issues.domain.model.DraftPriority
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.impl.presentation.IssueDraftsViewModel
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsEffect
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsEvent
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsState
import com.ergin.ghclient.feature.issues.impl.presentation.ui.components.IssueDraftCard
import com.ergin.ghclient.feature.issues.impl.presentation.ui.components.IssueDraftsHeader
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun IssueDraftsRoute(
    onNavigateToEditor: (owner: String, repoName: String, draftUid: String?) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IssueDraftsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is IssueDraftsEffect.NavigateToEditor -> {
                    onNavigateToEditor(effect.owner, effect.repoName, effect.draftUid)
                }
                IssueDraftsEffect.NavigateBack -> onNavigateBack()
                is IssueDraftsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    IssueDraftsScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun IssueDraftsScreen(
    uiState: IssueDraftsState,
    onEvent: (IssueDraftsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                Column {
                    IssueDraftsHeader(
                        onBackClick = { onEvent(IssueDraftsEvent.BackClicked) },
                        onRefreshClick = { onEvent(IssueDraftsEvent.RefreshClicked) }
                    )
                    if (uiState.isRefreshing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onEvent(IssueDraftsEvent.CreateDraftClicked) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CutCornerShape(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Создать черновик"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp)
            ) {
                if (uiState.isLoading) {
                    LoadingView()
                } else if (uiState.drafts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = CutCornerShape(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "[ НЕТ СОХРАНЁННЫХ ЧЕРНОВИКОВ ]",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { onEvent(IssueDraftsEvent.CreateDraftClicked) },
                                    shape = CutCornerShape(0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("+ СОЗДАТЬ ЧЕРНОВИК")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = uiState.drafts,
                            key = { it.uid }
                        ) { draft ->
                            IssueDraftCard(
                                draft = draft,
                                onClick = { onEvent(IssueDraftsEvent.DraftClicked(draft.uid)) },
                                onDeleteClick = { onEvent(IssueDraftsEvent.DeleteDraftClicked(draft.uid)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// === PREVIEWS ===

@Preview(showBackground = true, name = "Issue Drafts - Filled State")
@Composable
fun IssueDraftsScreenFilledPreview() {
    GHClientTheme {
        IssueDraftsScreen(
            uiState = IssueDraftsState(
                owner = "jetbrains",
                repoName = "kotlin",
                isLoading = false,
                drafts = listOf(
                    IssueDraft(
                        uid = "1",
                        owner = "jetbrains",
                        repoName = "kotlin",
                        title = "Ошибка компилятора K2 в лямбдах",
                        body = "Подроное описание сбоя генерации байткода...",
                        priority = DraftPriority.CRITICAL,
                        updatedAt = System.currentTimeMillis()
                    ),
                    IssueDraft(
                        uid = "2",
                        owner = "jetbrains",
                        repoName = "kotlin",
                        title = "Обновление документации Coroutines",
                        body = "Добавить примеры работы с SharedFlow",
                        priority = DraftPriority.NORMAL,
                        updatedAt = System.currentTimeMillis() - 3600000
                    )
                )
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Issue Drafts - Empty State")
@Composable
fun IssueDraftsScreenEmptyPreview() {
    GHClientTheme {
        IssueDraftsScreen(
            uiState = IssueDraftsState(
                owner = "jetbrains",
                repoName = "kotlin",
                isLoading = false,
                drafts = emptyList()
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
