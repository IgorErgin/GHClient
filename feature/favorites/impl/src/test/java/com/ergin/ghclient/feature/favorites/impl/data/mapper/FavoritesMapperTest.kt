package com.ergin.ghclient.feature.favorites.impl.data.mapper

import com.ergin.ghclient.core.database.entity.RepoEntity
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoId
import com.ergin.ghclient.feature.search.domain.model.Repo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesMapperTest {

    @Test
    fun `RepoEntity toDomain maps RepoEntity to Repo domain model`() {
        val entity = RepoEntity(
            id = 98765L,
            name = "FavoriteRepo",
            ownerName = "favoriteOwner",
            ownerAvatarUrl = "https://example.com/avatar.jpg",
            description = "A favorite repository description",
            language = "Kotlin",
            stars = 500,
            isFavorite = true
        )

        val domain: Repo = entity.toDomain()

        assertEquals(RepoId(98765L), domain.id)
        assertEquals("FavoriteRepo", domain.name)
        assertEquals("A favorite repository description", domain.description)
        assertEquals("Kotlin", domain.language)
        assertEquals(500, domain.stars)
        assertEquals(OwnerName("favoriteOwner"), domain.ownerName)
        assertEquals("https://example.com/avatar.jpg", domain.ownerAvatarUrl)
    }

    @Test
    fun `Repo toEntity maps Repo domain model to RepoEntity`() {
        val repo = Repo(
            id = RepoId(12345L),
            name = "AwesomeApp",
            description = "Awesome description",
            language = "Swift",
            stars = 120,
            ownerName = OwnerName("apple"),
            ownerAvatarUrl = "https://example.com/apple.png"
        )

        val entityDefault: RepoEntity = repo.toEntity()
        assertEquals(12345L, entityDefault.id)
        assertEquals("AwesomeApp", entityDefault.name)
        assertEquals("apple", entityDefault.ownerName)
        assertEquals("https://example.com/apple.png", entityDefault.ownerAvatarUrl)
        assertEquals("Awesome description", entityDefault.description)
        assertEquals("Swift", entityDefault.language)
        assertEquals(120, entityDefault.stars)
        assertTrue(entityDefault.isFavorite)

        val entityFalse: RepoEntity = repo.toEntity(isFavorite = false)
        assertFalse(entityFalse.isFavorite)
    }
}
