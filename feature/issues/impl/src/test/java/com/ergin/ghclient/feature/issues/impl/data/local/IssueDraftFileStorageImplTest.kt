package com.ergin.ghclient.feature.issues.impl.data.local

import android.content.Context
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class IssueDraftFileStorageImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var fileStorage: IssueDraftFileStorageImpl
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        context = mockk()
        every { context.filesDir } returns tempFolder.root

        fileStorage = IssueDraftFileStorageImpl(context, json)
    }

    @Test
    fun loadAll_whenFileNotExists_returnsEmptyList() = runTest {
        val result = fileStorage.loadAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun saveAndLoad_success() = runTest {
        val dto = IssueDraftDto(
            uid = "123",
            owner = "jetbrains",
            repoName = "kotlin",
            title = "Title",
            body = "Body",
            priority = "HIGH",
            updatedAt = 1000L
        )

        fileStorage.save(dto)

        val loaded = fileStorage.loadAll()
        assertEquals(1, loaded.size)
        assertEquals("123", loaded[0].uid)
        assertEquals("Title", loaded[0].title)

        val byId = fileStorage.getById("123")
        assertNotNull(byId)
        assertEquals("Title", byId?.title)
    }

    @Test
    fun delete_removesItem() = runTest {
        val dto1 = IssueDraftDto("1", "o", "r", "t1", "b1", "LOW", 1L)
        val dto2 = IssueDraftDto("2", "o", "r", "t2", "b2", "HIGH", 2L)

        fileStorage.saveAll(listOf(dto1, dto2))

        fileStorage.delete("1")

        val loaded = fileStorage.loadAll()
        assertEquals(1, loaded.size)
        assertEquals("2", loaded[0].uid)

        assertNull(fileStorage.getById("1"))
    }

    @Test
    fun loadAll_whenCorruptedFile_returnsEmptyList() = runTest {
        val cacheFile = File(tempFolder.root, "issue_drafts_cache.json")
        cacheFile.writeText("corrupted json {[[")

        val loaded = fileStorage.loadAll()
        assertTrue(loaded.isEmpty())
    }
}
