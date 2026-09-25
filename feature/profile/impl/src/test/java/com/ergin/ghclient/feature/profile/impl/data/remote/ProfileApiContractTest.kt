package com.ergin.ghclient.feature.profile.impl.data.remote

import com.ergin.ghclient.core.domain.model.UserId
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.feature.profile.domain.model.UserProfile
import com.ergin.ghclient.feature.profile.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserActivityDto
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserProfileDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileApiContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parse GET user contract JSON to UserProfileDto and map to UserProfile`() {
        val contractJson = """
            {
              "id": 583231,
              "login": "octocat",
              "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4",
              "name": "The Octocat",
              "bio": "GitHub's mascot and developer helper.",
              "public_repos": 8,
              "followers": 9820,
              "following": 9
            }
        """.trimIndent()

        val dto: UserProfileDto = json.decodeFromString(contractJson)
        val domain: UserProfile = dto.toDomain()

        assertEquals(UserId(583231L), domain.id)
        assertEquals("octocat", domain.login)
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", domain.avatarUrl)
        assertEquals("The Octocat", domain.name)
        assertEquals("GitHub's mascot and developer helper.", domain.bio)
        assertEquals(8, domain.publicRepos)
        assertEquals(9820, domain.followers)
        assertEquals(9, domain.following)
    }

    @Test
    fun `parse GET user events contract JSON to UserActivityDto list and map to UserActivity list`() {
        val contractJson = """
            [
              {
                "id": "2489651047",
                "type": "PushEvent",
                "actor": {
                  "login": "octocat"
                },
                "repo": {
                  "name": "octocat/Hello-World"
                },
                "payload": {
                  "action": "started"
                },
                "created_at": "2026-03-10T14:30:00Z"
              }
            ]
        """.trimIndent()

        val dtoList: List<UserActivityDto> = json.decodeFromString(contractJson)
        val domainList: List<UserActivity> = dtoList.map { it.toDomain() }

        assertEquals(1, domainList.size)

        val activity = domainList.first()
        assertEquals("2489651047", activity.id)
        assertEquals("PushEvent", activity.type)
        assertEquals("octocat/Hello-World", activity.repoName)
        assertEquals("2026-03-10T14:30:00Z", activity.createdAt)
        assertEquals("started", activity.payloadAction)
    }
}
