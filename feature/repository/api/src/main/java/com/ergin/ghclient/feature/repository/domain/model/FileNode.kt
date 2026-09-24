package com.ergin.ghclient.feature.repository.domain.model

import com.ergin.ghclient.core.domain.model.Sha

/**
 * Элемент дерева файлов и папок репозитория.
 */
data class FileNode(
    val name: String,
    val path: String,
    val type: FileType,
    val size: Long,
    val downloadUrl: String?,
    val sha: Sha
) {
    enum class FileType {
        FILE, DIRECTORY, SYMLINK, SUBMODULE
    }
}
