package com.ergin.ghclient.feature.search.impl.data.mapper

import com.ergin.ghclient.core.database.entity.RepoEntity
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.feature.search.domain.model.Repo
import com.ergin.ghclient.feature.search.impl.data.remote.model.OwnerDto
import com.ergin.ghclient.feature.search.impl.data.remote.model.RepoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RepoMapperTest {

    @Test
    fun `RepoDto toDomain maps primitive fields to Repo domain model correctly`() {
        val dto = RepoDto(
            id = 12345L,
            name = "GHClient",
            description = "Android GitHub client app",
            language = "Kotlin",
            stargazersCount = 99,
            owner = OwnerDto(
                login = "ergin",
                avatarUrl = "https://avatars.githubusercontent.com/u/12345"
            )
        )

        val domain: Repo = dto.toDomain()

        assertEquals(RepoId(12345L), domain.id)
        assertEquals("GHClient", domain.name)
        assertEquals("Android GitHub client app", domain.description)
        assertEquals("Kotlin", domain.language)
        assertEquals(99, domain.stars)
        assertEquals(OwnerName("ergin"), domain.ownerName)
        assertEquals("https://avatars.githubusercontent.com/u/12345", domain.ownerAvatarUrl)
    }

    @Test
    fun `RepoDto toEntity maps to Room RepoEntity with isFavorite flag`() {
        val dto = RepoDto(
            id = 67890L,
            name = "SampleRepo",
            description = "Description sample",
            language = "Java",
            stargazersCount = 42,
            owner = OwnerDto(
                login = "octocat",
                avatarUrl = "https://avatars.githubusercontent.com/u/67890"
            )
        )

        val entityTrue: RepoEntity = dto.toEntity(isFavorite = true)
        assertEquals(67890L, entityTrue.id)
        assertEquals("SampleRepo", entityTrue.name)
        assertEquals("octocat", entityTrue.ownerName)
        assertEquals("https://avatars.githubusercontent.com/u/67890", entityTrue.ownerAvatarUrl)
        assertEquals("Description sample", entityTrue.description)
        assertEquals("Java", entityTrue.language)
        assertEquals(42, entityTrue.stars)
        assertTrue(entityTrue.isFavorite)

        val entityFalse: RepoEntity = dto.toEntity(isFavorite = false)
        assertFalse(entityFalse.isFavorite)
    }

    @Test
    fun `RepoEntity toDomain maps RepoEntity back to Repo domain model`() {
        val entity = RepoEntity(
            id = 1111L,
            name = "KotlinApp",
            ownerName = "developer",
            ownerAvatarUrl = "https://example.com/avatar.png",
            description = "Kotlin app description",
            language = "Kotlin",
            stars = 150,
            isFavorite = true
        )

        val domain: Repo = entity.toDomain()

        assertEquals(RepoId(1111L), domain.id)
        assertEquals("KotlinApp", domain.name)
        assertEquals("Kotlin app description", domain.description)
        assertEquals("Kotlin", domain.language)
        assertEquals(150, domain.stars)
        assertEquals(OwnerName("developer"), domain.ownerName)
        assertEquals("https://example.com/avatar.png", domain.ownerAvatarUrl)
    }
}
