package com.ergin.ghclient.feature.upload.impl.presentation.contract

import android.net.Uri
import com.ergin.ghclient.core.ui.mvi.UiEffect
import com.ergin.ghclient.core.ui.mvi.UiEvent
import com.ergin.ghclient.core.ui.mvi.UiState

data class UploadState(
    val owner: String = "jetbrains",
    val repoName: String = "kotlin",
    val pathInput: String = "docs/spec_v2.md",
    val messageInput: String = "Добавление документации в реестр",
    val selectedFileUri: Uri? = null,
    val selectedFileName: String? = null,
    val selectedFileSizeKb: Long = 0,
    val isEnqueued: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

sealed interface UploadEvent : UiEvent {
    data class FileSelected(val uri: Uri, val name: String?, val sizeBytes: Long) : UploadEvent
    data class PathInputChanged(val path: String) : UploadEvent
    data class MessageInputChanged(val message: String) : UploadEvent
    data object SubmitClicked : UploadEvent
    data object BackClicked : UploadEvent
}

sealed interface UploadEffect : UiEffect {
    data object NavigateBack : UploadEffect
    data class ShowSnackbar(val message: String) : UploadEffect
}
