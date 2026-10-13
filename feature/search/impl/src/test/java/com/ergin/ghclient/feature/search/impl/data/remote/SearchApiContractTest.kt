package com.ergin.ghclient.feature.search.impl.data.remote

import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.search.impl.data.remote.model.SearchResponseDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SearchApiContractTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parse GET search repositories contract JSON to SearchResponseDto and map to Repo list`() {
        val contractJson = """
            {
              "total_count": 40,
              "incomplete_results": false,
              "items": [
                {
                  "id": 1296269,
                  "name": "Hello-World",
                  "full_name": "octocat/Hello-World",
                  "description": "This your first repo!",
                  "language": "Kotlin",
                  "stargazers_count": 80,
                  "owner": {
                    "login": "octocat",
                    "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4"
                  }
                }
              ]
            }
        """.trimIndent()

        val responseDto: SearchResponseDto = json.decodeFromString(contractJson)

        assertEquals(40, responseDto.totalCount)
        assertFalse(responseDto.incompleteResults)
        assertEquals(1, responseDto.items.size)

        val domainList: List<Repo> = responseDto.items.map { it.toDomain() }
        assertEquals(1, domainList.size)

        val repo = domainList.first()
        assertEquals(RepoId(1296269L), repo.id)
        assertEquals("Hello-World", repo.name)
        assertEquals("This your first repo!", repo.description)
        assertEquals("Kotlin", repo.language)
        assertEquals(80, repo.stars)
        assertEquals(OwnerName("octocat"), repo.ownerName)
        assertEquals("https://avatars.githubusercontent.com/u/583231?v=4", repo.ownerAvatarUrl)
    }
}
