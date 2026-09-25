package com.ergin.ghclient.feature.issues.impl.data.mapper

import com.ergin.ghclient.feature.issues.domain.model.Issue
import com.ergin.ghclient.feature.issues.domain.model.IssueId
import com.ergin.ghclient.feature.issues.domain.model.IssueNumber
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDto
import com.ergin.ghclient.feature.issues.impl.data.remote.model.UserDto
import org.junit.Assert.assertEquals
import org.junit.Test

class IssueMapperTest {

    @Test
    fun `IssueDto toDomain maps state open to Issue IssueState OPEN`() {
        val dto = IssueDto(
            id = 101L,
            number = 1,
            title = "Bug in login flow",
            body = "Description of the bug",
            state = "open",
            comments = 3,
            createdAt = "2026-03-30T14:00:00Z",
            user = UserDto(
                login = "reporter",
                avatarUrl = "https://avatars.githubusercontent.com/u/101"
            )
        )

        val domain: Issue = dto.toDomain()

        assertEquals(IssueId(101L), domain.id)
        assertEquals(IssueNumber(1), domain.number)
        assertEquals("Bug in login flow", domain.title)
        assertEquals("Description of the bug", domain.body)
        assertEquals(Issue.IssueState.OPEN, domain.state)
        assertEquals("reporter", domain.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/101", domain.authorAvatarUrl)
        assertEquals("2026-03-30T14:00:00Z", domain.createdAt)
        assertEquals(3, domain.commentsCount)
    }

    @Test
    fun `IssueDto toDomain maps state closed to Issue IssueState CLOSED`() {
        val dto = IssueDto(
            id = 202L,
            number = 2,
            title = "Resolved issue",
            body = null,
            state = "closed",
            comments = 0,
            createdAt = "2026-03-29T10:00:00Z",
            user = UserDto(
                login = "assignee",
                avatarUrl = "https://avatars.githubusercontent.com/u/202"
            )
        )

        val domain: Issue = dto.toDomain()

        assertEquals(IssueId(202L), domain.id)
        assertEquals(IssueNumber(2), domain.number)
        assertEquals("Resolved issue", domain.title)
        assertEquals(null, domain.body)
        assertEquals(Issue.IssueState.CLOSED, domain.state)
        assertEquals("assignee", domain.authorName)
        assertEquals("https://avatars.githubusercontent.com/u/202", domain.authorAvatarUrl)
        assertEquals("2026-03-29T10:00:00Z", domain.createdAt)
        assertEquals(0, domain.commentsCount)
    }

    @Test
    fun `IssueDto toDomain maps state case insensitively`() {
        val closedUpperDto = IssueDto(
            id = 303L,
            number = 3,
            title = "UPPERCASE STATE",
            body = null,
            state = "CLOSED",
            comments = 1,
            createdAt = "2026-03-28T08:00:00Z",
            user = UserDto(login = "user", avatarUrl = "https://example.com/avatar")
        )

        assertEquals(Issue.IssueState.CLOSED, closedUpperDto.toDomain().state)

        val unknownStateDto = closedUpperDto.copy(state = "some_unknown_state")
        assertEquals(Issue.IssueState.OPEN, unknownStateDto.toDomain().state)
    }
}
