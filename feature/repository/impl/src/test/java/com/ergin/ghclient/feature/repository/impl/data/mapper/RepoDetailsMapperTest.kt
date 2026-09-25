package com.ergin.ghclient.feature.repository.impl.data.mapper

import com.ergin.ghclient.core.database.entity.RepoDetailsEntity
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.core.domain.model.Sha
import com.ergin.ghclient.feature.repository.domain.model.Commit
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.model.PullRequestId
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails
import com.ergin.ghclient.feature.repository.impl.data.remote.model.CommitAuthorDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.CommitDetailDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.CommitDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.FileNodeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.PullRequestDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.ReadmeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.RepoDetailsDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Base64

class RepoDetailsMapperTest {

    @Test
    fun `RepoDetailsDto toDomain maps to RepoDetails correctly`() {
        val dto = RepoDetailsDto(
            id = 1001L,
            name = "GHClient",
            description = "Detailed description",
            language = "Kotlin",
            stargazersCount = 50,
            forksCount = 10,
            openIssuesCount = 5,
            defaultBranch = "main",
            owner = UserDto(
                login = "ergin",
                avatarUrl = "https://avatars.githubusercontent.com/u/1001"
            )
        )

        val domain: RepoDetails = dto.toDomain()

        assertEquals(RepoId(1001L), domain.id)
        assertEquals("GHClient", domain.name)
        assertEquals("Detailed description", domain.description)
        assertEquals("Kotlin", domain.language)
        assertEquals(50, domain.stars)
        assertEquals(10, domain.forks)
        assertEquals(5, domain.openIssuesCount)
        assertEquals("main", domain.defaultBranch)
        assertEquals(OwnerName("ergin"), domain.ownerName)
        assertEquals("https://avatars.githubusercontent.com/u/1001", domain.ownerAvatarUrl)
    }

    @Test
    fun `RepoDetailsEntity toDomain and RepoDetails toEntity map bidirectionally`() {
        val entity = RepoDetailsEntity(
            repoId = 1001L,
            name = "GHClient",
            ownerName = "ergin",
            ownerAvatarUrl = "https://avatars.githubusercontent.com/u/1001",
            description = "Detailed description",
            language = "Kotlin",
            stars = 50,
            forks = 10,
            openIssuesCount = 5,
            defaultBranch = "main",
            readmeContent = "# Readme"
        )

        val domain = entity.toDomain()

        assertEquals(RepoId(1001L), domain.id)
        assertEquals("GHClient", domain.name)
        assertEquals(OwnerName("ergin"), domain.ownerName)

        val mappedEntity = domain.toEntity(readmeContent = "# Readme")
        assertEquals(entity, mappedEntity)
    }

    @Test
    fun `ReadmeDto toDomain decodes Base64 content correctly`() {
        val originalText = "# Welcome to GHClient\nThis is a test README file."
        val encodedBase64 = Base64.getEncoder().encodeToString(originalText.toByteArray(Charsets.UTF_8))

        val dtoWithBase64 = ReadmeDto(
            name = "README.md",
            path = "README.md",
            sha = "abc123sha",
            content = "$encodedBase64\n",
            encoding = "base64"
        )

        val decoded: String = dtoWithBase64.toDomain()
        assertEquals(originalText, decoded)
    }

    @Test
    fun `ReadmeDto toDomain returns plain string or empty content when not base64 encoded or null`() {
        val plainDto = ReadmeDto(
            name = "README.md",
            path = "README.md",
            sha = "abc123sha",
            content = "Plain text content",
            encoding = "utf-8"
        )
        assertEquals("Plain text content", plainDto.toDomain())

        val nullDto = ReadmeDto(
            name = "README.md",
            path = "README.md",
            sha = "abc123sha",
            content = null,
            encoding = null
        )
        assertEquals("", nullDto.toDomain())
    }

    @Test
    fun `FileNodeDto toDomain maps type dir and file to FileNode FileType DIRECTORY and FILE`() {
        val dirDto = FileNodeDto(
            name = "src",
            path = "src",
            sha = "shaDir123",
            size = 0L,
            type = "dir",
            downloadUrl = null
        )

        val dirDomain: FileNode = dirDto.toDomain()
        assertEquals("src", dirDomain.name)
        assertEquals("src", dirDomain.path)
        assertEquals(FileNode.FileType.DIRECTORY, dirDomain.type)
        assertEquals(0L, dirDomain.size)
        assertNull(dirDomain.downloadUrl)
        assertEquals(Sha("shaDir123"), dirDomain.sha)

        val fileDto = FileNodeDto(
            name = "build.gradle.kts",
            path = "build.gradle.kts",
            sha = "shaFile456",
            size = 1024L,
            type = "file",
            downloadUrl = "https://raw.githubusercontent.com/file"
        )

        val fileDomain: FileNode = fileDto.toDomain()
        assertEquals("build.gradle.kts", fileDomain.name)
        assertEquals(FileNode.FileType.FILE, fileDomain.type)
        assertEquals(1024L, fileDomain.size)
        assertEquals("https://raw.githubusercontent.com/file", fileDomain.downloadUrl)
        assertEquals(Sha("shaFile456"), fileDomain.sha)

        val symlinkDto = fileDto.copy(type = "symlink")
        assertEquals(FileNode.FileType.SYMLINK, symlinkDto.toDomain().type)

        val submoduleDto = fileDto.copy(type = "submodule")
        assertEquals(FileNode.FileType.SUBMODULE, submoduleDto.toDomain().type)
    }

    @Test
    fun `CommitDto toDomain maps to Commit with Sha`() {
        val dto = CommitDto(
            sha = "f00bar123456789",
            commit = CommitDetailDto(
                message = "Initial commit",
                author = CommitAuthorDto(
                    name = "Developer Name",
                    date = "2026-03-30T10:00:00Z"
                )
            ),
            author = UserDto(
                login = "octocat",
                avatarUrl = "https://avatars.githubusercontent.com/u/123"
            )
        )

        val domain: Commit = dto.toDomain()

        assertEquals(Sha("f00bar123456789"), domain.sha)
        assertEquals("Initial commit", domain.message)
        assertEquals("octocat", domain.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/123", domain.authorAvatarUrl)
        assertEquals("2026-03-30T10:00:00Z", domain.date)
    }

    @Test
    fun `CommitDto toDomain falls back to commit author name when author user is null`() {
        val dto = CommitDto(
            sha = "1234567890",
            commit = CommitDetailDto(
                message = "Commit without user",
                author = CommitAuthorDto(
                    name = "Git Author",
                    date = "2026-03-30T10:00:00Z"
                )
            ),
            author = null
        )

        val domain: Commit = dto.toDomain()

        assertEquals("Git Author", domain.authorName)
        assertNull(domain.authorAvatarUrl)
    }

    @Test
    fun `PullRequestDto toDomain maps to PullRequest with PullRequestId`() {
        val dto = PullRequestDto(
            id = 5555L,
            number = 42,
            title = "Add new feature",
            state = "open",
            createdAt = "2026-03-30T12:00:00Z",
            user = UserDto(
                login = "contributor",
                avatarUrl = "https://avatars.githubusercontent.com/u/5555"
            )
        )

        val domain: PullRequest = dto.toDomain()

        assertEquals(PullRequestId(5555L), domain.id)
        assertEquals(42, domain.number)
        assertEquals("Add new feature", domain.title)
        assertEquals("open", domain.state)
        assertEquals("contributor", domain.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/5555", domain.authorAvatarUrl)
        assertEquals("2026-03-30T12:00:00Z", domain.createdAt)
    }
}
