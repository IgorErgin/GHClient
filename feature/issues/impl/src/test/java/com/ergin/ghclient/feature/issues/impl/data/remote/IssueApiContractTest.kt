package com.ergin.ghclient.feature.issues.impl.data.remote

import com.ergin.ghclient.feature.issues.domain.model.Issue
import com.ergin.ghclient.feature.issues.domain.model.IssueId
import com.ergin.ghclient.feature.issues.domain.model.IssueNumber
import com.ergin.ghclient.feature.issues.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class IssueApiContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parse GET issues contract JSON to IssueDto list and map to Issue list`() {
        val contractJson = """
            [
              {
                "id": 13471161,
                "number": 1347,
                "title": "Found a bug in search flow",
                "body": "Having a problem when search query is blank.",
                "state": "open",
                "comments": 3,
                "created_at": "2026-03-10T10:00:00Z",
                "user": {
                  "login": "octocat",
                  "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4"
                }
              }
            ]
        """.trimIndent()

        val dtoList: List<IssueDto> = json.decodeFromString(contractJson)
        val domainList: List<Issue> = dtoList.map { it.toDomain() }

        assertEquals(1, domainList.size)

        val issue = domainList.first()
        assertEquals(IssueId(13471161L), issue.id)
        assertEquals(IssueNumber(1347), issue.number)
        assertEquals("Found a bug in search flow", issue.title)
        assertEquals("Having a problem when search query is blank.", issue.body)
        assertEquals(Issue.IssueState.OPEN, issue.state)
        assertEquals("octocat", issue.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", issue.authorAvatarUrl)
        assertEquals("2026-03-10T10:00:00Z", issue.createdAt)
        assertEquals(3, issue.commentsCount)
    }

    @Test
    fun `parse POST issue response contract JSON to IssueDto and map to Issue`() {
        val contractJson = """
            {
              "id": 13471162,
              "number": 1348,
              "title": "Bug in user profile avatar",
              "body": "Avatar is not updating on network change.",
              "state": "open",
              "comments": 0,
              "created_at": "2026-03-10T15:00:00Z",
              "user": {
                "login": "octocat",
                "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4"
              }
            }
        """.trimIndent()

        val dto: IssueDto = json.decodeFromString(contractJson)
        val issue: Issue = dto.toDomain()

        assertEquals(IssueId(13471162L), issue.id)
        assertEquals(IssueNumber(1348), issue.number)
        assertEquals("Bug in user profile avatar", issue.title)
        assertEquals("Avatar is not updating on network change.", issue.body)
        assertEquals(Issue.IssueState.OPEN, issue.state)
        assertEquals("octocat", issue.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", issue.authorAvatarUrl)
        assertEquals("2026-03-10T15:00:00Z", issue.createdAt)
        assertEquals(0, issue.commentsCount)
    }
}
