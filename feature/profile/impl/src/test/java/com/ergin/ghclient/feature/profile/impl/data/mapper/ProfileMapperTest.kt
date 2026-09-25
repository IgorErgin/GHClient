package com.ergin.ghclient.feature.profile.impl.data.mapper

import com.ergin.ghclient.core.domain.model.UserId
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.feature.profile.domain.model.UserProfile
import com.ergin.ghclient.feature.profile.impl.data.remote.model.PayloadDto
import com.ergin.ghclient.feature.profile.impl.data.remote.model.RepoSummaryDto
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserActivityDto
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserDto
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserProfileDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileMapperTest {

    @Test
    fun `UserProfileDto toDomain maps to UserProfile with UserId`() {
        val dto = UserProfileDto(
            id = 777L,
            login = "johndoe",
            avatarUrl = "https://avatars.githubusercontent.com/u/777",
            name = "John Doe",
            bio = "Android Developer",
            publicRepos = 25,
            followers = 100,
            following = 50
        )

        val domain: UserProfile = dto.toDomain()

        assertEquals(UserId(777L), domain.id)
        assertEquals("johndoe", domain.login)
        assertEquals("https://avatars.githubusercontent.com/u/777", domain.avatarUrl)
        assertEquals("John Doe", domain.name)
        assertEquals("Android Developer", domain.bio)
        assertEquals(25, domain.publicRepos)
        assertEquals(100, domain.followers)
        assertEquals(50, domain.following)
    }

    @Test
    fun `UserActivityDto toDomain maps to UserActivity correctly`() {
        val dto = UserActivityDto(
            id = "event-12345",
            type = "WatchEvent",
            actor = UserDto(
                login = "johndoe",
                avatarUrl = "https://avatars.githubusercontent.com/u/777"
            ),
            repo = RepoSummaryDto(
                id = 999L,
                name = "owner/repo-name"
            ),
            createdAt = "2026-03-30T15:30:00Z",
            payload = PayloadDto(action = "started")
        )

        val domain: UserActivity = dto.toDomain()

        assertEquals("event-12345", domain.id)
        assertEquals("WatchEvent", domain.type)
        assertEquals("owner/repo-name", domain.repoName)
        assertEquals("2026-03-30T15:30:00Z", domain.createdAt)
        assertEquals("started", domain.payloadAction)
    }

    @Test
    fun `UserActivityDto toDomain handles null payload correctly`() {
        val dto = UserActivityDto(
            id = "event-67890",
            type = "PushEvent",
            actor = UserDto(
                login = "johndoe",
                avatarUrl = "https://avatars.githubusercontent.com/u/777"
            ),
            repo = RepoSummaryDto(
                id = 888L,
                name = "owner/other-repo"
            ),
            createdAt = "2026-03-30T16:00:00Z",
            payload = null
        )

        val domain: UserActivity = dto.toDomain()

        assertEquals("event-67890", domain.id)
        assertEquals("PushEvent", domain.type)
        assertEquals("owner/other-repo", domain.repoName)
        assertEquals("2026-03-30T16:00:00Z", domain.createdAt)
        assertNull(domain.payloadAction)
    }
}
