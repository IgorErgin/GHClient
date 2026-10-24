package com.ergin.ghclient.feature.issues.impl.presentation.ui

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
import com.ergin.ghclient.core.ui.components.CrtScreenOverlay
import com.ergin.ghclient.core.ui.components.ErrorView
import com.ergin.ghclient.core.ui.components.LoadingView
import com.ergin.ghclient.feature.issues.impl.presentation.CreateIssueViewModel
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueEffect
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueEvent
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueState
import com.ergin.ghclient.feature.issues.impl.presentation.ui.components.CreateIssueForm
import com.ergin.ghclient.feature.issues.impl.presentation.ui.components.CreateIssueHeader
import com.ergin.ghclient.feature.issues.impl.presentation.ui.components.SubmitIssueActionButton
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateIssueRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateIssueViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                CreateIssueEffect.NavigateBack -> onNavigateBack()
                is CreateIssueEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CreateIssueScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun CreateIssueScreen(
    uiState: CreateIssueState,
    onEvent: (CreateIssueEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CreateIssueHeader(
                    onBackClick = { onEvent(CreateIssueEvent.BackClicked) }
                )
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
                            onRetry = { onEvent(CreateIssueEvent.SubmitClicked) }
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                CreateIssueForm(
                                    owner = uiState.owner,
                                    repoName = uiState.repoName,
                                    titleInput = uiState.titleInput,
                                    onTitleInputChanged = { onEvent(CreateIssueEvent.TitleChanged(it)) },
                                    bodyInput = uiState.bodyInput,
                                    onBodyInputChanged = { onEvent(CreateIssueEvent.BodyChanged(it)) }
                                )
                            }

                            item {
                                SubmitIssueActionButton(
                                    onSubmitClick = { onEvent(CreateIssueEvent.SubmitClicked) },
                                    isLoading = uiState.isLoading
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// === COMPOSE PREVIEWS ===

@Preview(showBackground = true, name = "Create Issue - Form State")
@Composable
fun CreateIssueScreenFormPreview() {
    GHClientTheme {
        CreateIssueScreen(
            uiState = CreateIssueState(
                owner = "jetbrains",
                repoName = "kotlin",
                titleInput = "Ошибка компилятора K2",
                bodyInput = "# Описание сбоя\n- Шаг 1\n- Шаг 2"
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Create Issue - Loading State")
@Composable
fun CreateIssueScreenLoadingPreview() {
    GHClientTheme {
        CreateIssueScreen(
            uiState = CreateIssueState(isLoading = true),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Create Issue - Error State")
@Composable
fun CreateIssueScreenErrorPreview() {
    GHClientTheme {
        CreateIssueScreen(
            uiState = CreateIssueState(errorMessage = "Ошибка при формировании рапорта"),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
