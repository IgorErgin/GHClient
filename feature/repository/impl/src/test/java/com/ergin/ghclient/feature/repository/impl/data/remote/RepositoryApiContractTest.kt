package com.ergin.ghclient.feature.repository.impl.data.remote

import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.core.domain.model.Sha
import com.ergin.ghclient.feature.repository.domain.model.Commit
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.feature.repository.domain.model.PullRequest
import com.ergin.ghclient.feature.repository.domain.model.PullRequestId
import com.ergin.ghclient.feature.repository.domain.model.RepoDetails
import com.ergin.ghclient.feature.repository.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.repository.impl.data.remote.model.CommitDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.FileNodeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.PullRequestDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.ReadmeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.RepoDetailsDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryApiContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parse GET repo details contract JSON to RepoDetailsDto and map to RepoDetails`() {
        val contractJson = """
            {
              "id": 1296269,
              "name": "Hello-World",
              "full_name": "octocat/Hello-World",
              "description": "This your first repo!",
              "language": "Kotlin",
              "stargazers_count": 80,
              "forks_count": 9,
              "open_issues_count": 2,
              "default_branch": "main",
              "owner": {
                "login": "octocat",
                "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4"
              }
            }
        """.trimIndent()

        val dto: RepoDetailsDto = json.decodeFromString(contractJson)
        val domain: RepoDetails = dto.toDomain()

        assertEquals(RepoId(1296269L), domain.id)
        assertEquals("Hello-World", domain.name)
        assertEquals("This your first repo!", domain.description)
        assertEquals("Kotlin", domain.language)
        assertEquals(80, domain.stars)
        assertEquals(9, domain.forks)
        assertEquals(2, domain.openIssuesCount)
        assertEquals(OwnerName("octocat"), domain.ownerName)
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", domain.ownerAvatarUrl)
        assertEquals("main", domain.defaultBranch)
    }

    @Test
    fun `parse GET readme contract JSON to ReadmeDto and map to String content`() {
        val contractJson = """
            {
              "name": "README.md",
              "path": "README.md",
              "sha": "45b983be0dbb73c6aa1e156618c264d5e0d2eed8",
              "size": 536,
              "content": "IyBIZWxsbyBXb3JsZAoKVGhpcyBpcyBhIHNhbXBsZSBSRUFETUUgZmlsZS4=\n",
              "encoding": "base64"
            }
        """.trimIndent()

        val dto: ReadmeDto = json.decodeFromString(contractJson)
        val decodedContent: String = dto.toDomain()

        assertEquals("# Hello World\n\nThis is a sample README file.", decodedContent)
    }

    @Test
    fun `parse GET contents contract JSON to FileNodeDto list and map to FileNode list`() {
        val contractJson = """
            [
              {
                "name": "src",
                "path": "src",
                "sha": "9a38f382f6f59bfda012bf0e43d9203a958a74e5",
                "size": 0,
                "type": "dir",
                "download_url": null
              },
              {
                "name": "build.gradle.kts",
                "path": "build.gradle.kts",
                "sha": "7d686987f170f209581f181f0efa3ef5f299f0f9",
                "size": 1200,
                "type": "file",
                "download_url": "https://raw.githubusercontent.com/octocat/Hello-World/main/build.gradle.kts"
              }
            ]
        """.trimIndent()

        val dtoList: List<FileNodeDto> = json.decodeFromString(contractJson)
        val domainList: List<FileNode> = dtoList.map { it.toDomain() }

        assertEquals(2, domainList.size)

        val dirNode = domainList[0]
        assertEquals("src", dirNode.name)
        assertEquals("src", dirNode.path)
        assertEquals(FileNode.FileType.DIRECTORY, dirNode.type)
        assertEquals(0L, dirNode.size)
        assertNull(dirNode.downloadUrl)
        assertEquals(Sha("9a38f382f6f59bfda012bf0e43d9203a958a74e5"), dirNode.sha)

        val fileNode = domainList[1]
        assertEquals("build.gradle.kts", fileNode.name)
        assertEquals("build.gradle.kts", fileNode.path)
        assertEquals(FileNode.FileType.FILE, fileNode.type)
        assertEquals(1200L, fileNode.size)
        assertEquals("https://raw.githubusercontent.com/octocat/Hello-World/main/build.gradle.kts", fileNode.downloadUrl)
        assertEquals(Sha("7d686987f170f209581f181f0efa3ef5f299f0f9"), fileNode.sha)
    }

    @Test
    fun `parse GET commits contract JSON to CommitDto list and map to Commit list`() {
        val contractJson = """
            [
              {
                "sha": "6dcb09b5b57875f334f61aebed695e2e4193db5e",
                "commit": {
                  "message": "Fix layout bug in MainActivity",
                  "author": {
                    "name": "Monalisa Octocat",
                    "date": "2026-03-10T12:00:00Z"
                  }
                },
                "author": {
                  "login": "octocat",
                  "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4"
                }
              }
            ]
        """.trimIndent()

        val dtoList: List<CommitDto> = json.decodeFromString(contractJson)
        val domainList: List<Commit> = dtoList.map { it.toDomain() }

        assertEquals(1, domainList.size)

        val commit = domainList.first()
        assertEquals(Sha("6dcb09b5b57875f334f61aebed695e2e4193db5e"), commit.sha)
        assertEquals("Fix layout bug in MainActivity", commit.message)
        assertEquals("octocat", commit.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", commit.authorAvatarUrl)
        assertEquals("2026-03-10T12:00:00Z", commit.date)
    }

    @Test
    fun `parse GET pulls contract JSON to PullRequestDto list and map to PullRequest list`() {
        val contractJson = """
            [
              {
                "id": 13471161,
                "number": 1347,
                "title": "Fix search navigation bug",
                "state": "open",
                "created_at": "2026-03-10T14:00:00Z",
                "user": {
                  "login": "octocat",
                  "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4"
                }
              }
            ]
        """.trimIndent()

        val dtoList: List<PullRequestDto> = json.decodeFromString(contractJson)
        val domainList: List<PullRequest> = dtoList.map { it.toDomain() }

        assertEquals(1, domainList.size)

        val pr = domainList.first()
        assertEquals(PullRequestId(13471161L), pr.id)
        assertEquals(1347, pr.number)
        assertEquals("Fix search navigation bug", pr.title)
        assertEquals("open", pr.state)
        assertEquals("octocat", pr.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", pr.authorAvatarUrl)
        assertEquals("2026-03-10T14:00:00Z", pr.createdAt)
    }
}
