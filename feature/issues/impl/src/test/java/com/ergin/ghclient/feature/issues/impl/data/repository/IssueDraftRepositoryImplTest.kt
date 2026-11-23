package com.ergin.ghclient.feature.issues.impl.data.repository

import app.cash.turbine.test
import com.ergin.ghclient.feature.issues.domain.model.DraftPriority
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.impl.data.local.IssueDraftFileStorage
import com.ergin.ghclient.feature.issues.impl.data.remote.IssueDraftRemoteDataSource
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class IssueDraftRepositoryImplTest {

    private lateinit var fileStorage: IssueDraftFileStorage
    private lateinit var remoteDataSource: IssueDraftRemoteDataSource
    private lateinit var repository: IssueDraftRepositoryImpl

    @Before
    fun setUp() {
        fileStorage = mockk(relaxed = true)
        remoteDataSource = mockk(relaxed = true)

        coEvery { fileStorage.loadAll() } returns emptyList()

        repository = IssueDraftRepositoryImpl(fileStorage, remoteDataSource)
    }

    @Test
    fun observeDrafts_emitsInitialDataFromFileStorage() = runTest {
        val initialDto = IssueDraftDto("1", "jetbrains", "kotlin", "Title", "Body", "NORMAL", 100L)
        coEvery { fileStorage.loadAll() } returns listOf(initialDto)

        repository.observeDrafts().test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals("1", item[0].uid)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun save_updatesLocalFileAndRemote() = runTest {
        val draft = IssueDraft(uid = "1", owner = "o", repoName = "r", title = "T", body = "B", priority = DraftPriority.CRITICAL)

        repository.save(draft)

        coVerify { fileStorage.save(any()) }
        coVerify { remoteDataSource.pushDraft(any()) }

        val cached = repository.getDraft("1")
        assertEquals("1", cached?.uid)
        assertEquals("T", cached?.title)
    }

    @Test(expected = IOException::class)
    fun save_whenRemoteFailsWithIOException_rethrowsExceptionAndKeepsLocalCache() = runTest {
        coEvery { remoteDataSource.pushDraft(any()) } throws IOException("ГОСТ Error")

        val draft = IssueDraft(uid = "1", owner = "o", repoName = "r", title = "T", body = "B")

        try {
            repository.save(draft)
        } finally {
            coVerify { fileStorage.save(any()) }
            val cached = repository.getDraft("1")
            assertEquals("1", cached?.uid)
        }
    }

    @Test
    fun observeDrafts_emitsUpdatesAfterSaveAndDelete() = runTest {
        val draft = IssueDraft(uid = "1", owner = "o", repoName = "r", title = "T", body = "B")

        repository.observeDrafts().test {
            assertEquals(emptyList<IssueDraft>(), awaitItem())

            repository.save(draft)
            val afterSave = awaitItem()
            assertEquals(1, afterSave.size)
            assertEquals("T", afterSave[0].title)

            repository.delete("1")
            val afterDelete = awaitItem()
            assertEquals(0, afterDelete.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = IOException::class)
    fun refreshFromBackend_whenIOException_preservesCachedDrafts() = runTest {
        val initialDto = IssueDraftDto("1", "jetbrains", "kotlin", "Cached", "Body", "NORMAL", 100L)
        coEvery { fileStorage.loadAll() } returns listOf(initialDto)
        coEvery { remoteDataSource.fetchDrafts() } throws IOException("Network down")

        try {
            repository.refreshFromBackend()
        } finally {
            val cached = repository.getDraft("1")
            assertEquals("Cached", cached?.title)
        }
    }

    @Test(expected = kotlinx.coroutines.CancellationException::class)
    fun refreshFromBackend_whenCancellationException_rethrows() = runTest {
        coEvery { remoteDataSource.fetchDrafts() } throws kotlinx.coroutines.CancellationException("Cancelled")
        repository.refreshFromBackend()
    }
}
