package com.ergin.ghclient.feature.repository.impl.data.mapper

import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.core.domain.model.Sha
import com.ergin.ghclient.feature.repository.domain.model.Commit
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.model.PullRequestId
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails
import com.ergin.ghclient.feature.repository.impl.data.remote.model.CommitDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.FileNodeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.PullRequestDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.ReadmeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.RepoDetailsDto
import java.util.Base64

fun RepoDetailsDto.toDomain(): RepoDetails {
    return RepoDetails(
        id = RepoId(id),
        name = name,
        description = description,
        language = language,
        stars = stargazersCount,
        forks = forksCount,
        openIssuesCount = openIssuesCount,
        ownerName = OwnerName(owner.login),
        ownerAvatarUrl = owner.avatarUrl,
        defaultBranch = defaultBranch
    )
}

fun ReadmeDto.toDomain(): String {
    val rawContent = content ?: return ""
    val cleanContent = rawContent.replace("\n", "").replace("\r", "")
    return try {
        val decodedBytes = Base64.getDecoder().decode(cleanContent)
        String(decodedBytes, Charsets.UTF_8)
    } catch (e: Exception) {
        rawContent
    }
}

fun FileNodeDto.toDomain(): FileNode {
    val fileType = when (type.lowercase()) {
        "file" -> FileNode.FileType.FILE
        "dir" -> FileNode.FileType.DIRECTORY
        "symlink" -> FileNode.FileType.SYMLINK
        "submodule" -> FileNode.FileType.SUBMODULE
        else -> FileNode.FileType.FILE
    }
    return FileNode(
        name = name,
        path = path,
        type = fileType,
        size = size,
        downloadUrl = downloadUrl,
        sha = Sha(sha)
    )
}

fun CommitDto.toDomain(): Commit {
    return Commit(
        sha = Sha(sha),
        message = commit.message,
        authorName = author?.login ?: commit.author?.name ?: "Unknown",
        authorAvatarUrl = author?.avatarUrl,
        date = commit.author?.date ?: ""
    )
}

fun PullRequestDto.toDomain(): PullRequest {
    return PullRequest(
        id = PullRequestId(id),
        number = number,
        title = title,
        state = state,
        authorName = user?.login ?: "Unknown",
        authorAvatarUrl = user?.avatarUrl ?: "",
        createdAt = createdAt
    )
}
