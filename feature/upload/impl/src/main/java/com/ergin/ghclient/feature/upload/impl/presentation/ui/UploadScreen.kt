package com.ergin.ghclient.feature.upload.impl.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ergin.ghclient.core.ui.components.CrtScreenOverlay
import com.ergin.ghclient.core.ui.components.ErrorView
import com.ergin.ghclient.core.ui.components.LoadingView
import com.ergin.ghclient.feature.upload.impl.presentation.UploadViewModel
import com.ergin.ghclient.feature.upload.impl.presentation.contract.UploadEffect
import com.ergin.ghclient.feature.upload.impl.presentation.contract.UploadEvent
import com.ergin.ghclient.feature.upload.impl.presentation.contract.UploadState
import com.ergin.ghclient.feature.upload.impl.presentation.ui.components.UploadActionButton
import com.ergin.ghclient.feature.upload.impl.presentation.ui.components.UploadForm
import com.ergin.ghclient.feature.upload.impl.presentation.ui.components.UploadHeader
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UploadRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "file_dump.bin"
            val fileSize = try {
                context.contentResolver.openInputStream(uri)?.use { it.available().toLong() } ?: 0L
            } catch (_: Exception) {
                0L
            }
            viewModel.onEvent(
                UploadEvent.FileSelected(
                    uri = uri,
                    name = fileName,
                    sizeBytes = fileSize
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                UploadEffect.NavigateBack -> onNavigateBack()
                is UploadEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    UploadScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onSelectFileClick = { filePickerLauncher.launch("*/*") },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun UploadScreen(
    uiState: UploadState,
    onEvent: (UploadEvent) -> Unit,
    onSelectFileClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                UploadHeader(
                    onBackClick = { onEvent(UploadEvent.BackClicked) }
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
                            onRetry = { onEvent(UploadEvent.SubmitClicked) }
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                UploadForm(
                                    owner = uiState.owner,
                                    repoName = uiState.repoName,
                                    pathInput = uiState.pathInput,
                                    onPathInputChanged = { onEvent(UploadEvent.PathInputChanged(it)) },
                                    messageInput = uiState.messageInput,
                                    onMessageInputChanged = { onEvent(UploadEvent.MessageInputChanged(it)) },
                                    selectedFileName = uiState.selectedFileName,
                                    selectedFileSizeKb = uiState.selectedFileSizeKb,
                                    onSelectFileClick = onSelectFileClick
                                )
                            }

                            item {
                                UploadActionButton(
                                    onSubmitClick = { onEvent(UploadEvent.SubmitClicked) },
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

@Preview(showBackground = true, name = "Upload Screen - Form State")
@Composable
fun UploadScreenFormPreview() {
    GHClientTheme {
        UploadScreen(
            uiState = UploadState(
                owner = "jetbrains",
                repoName = "kotlin",
                pathInput = "docs/spec_v2.md",
                messageInput = "Добавление документации в реестр",
                selectedFileName = "spec_v2.md",
                selectedFileSizeKb = 14
            ),
            onEvent = {},
            onSelectFileClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
