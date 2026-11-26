package com.ergin.ghclient.feature.issues.impl.data.repository

import app.cash.turbine.test
import com.ergin.ghclient.core.database.dao.IssueDraftDao
import com.ergin.ghclient.core.database.entity.IssueDraftEntity
import com.ergin.ghclient.feature.issues.domain.model.DraftPriority
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.impl.data.remote.IssueDraftRemoteDataSource
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

class IssueDraftRepositoryImplTest {

    private lateinit var issueDraftDao: IssueDraftDao
    private lateinit var remoteDataSource: IssueDraftRemoteDataSource
    private lateinit var repository: IssueDraftRepositoryImpl

    @Before
    fun setUp() {
        issueDraftDao = mockk(relaxed = true)
        remoteDataSource = mockk(relaxed = true)

        coEvery { issueDraftDao.observeAll() } returns flowOf(emptyList())

        repository = IssueDraftRepositoryImpl(issueDraftDao, remoteDataSource)
    }

    @Test
    fun observeDrafts_emitsInitialDataFromRoomDao() = runTest {
        val initialEntity = IssueDraftEntity("1", "jetbrains", "kotlin", "Title", "Body", 1, 100L)
        coEvery { issueDraftDao.observeAll() } returns flowOf(listOf(initialEntity))

        repository.observeDrafts().test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals("1", item[0].uid)
            assertEquals(DraftPriority.NORMAL, item[0].priority)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getDraft_returnsMappedDomainDraftWhenFoundAndNullWhenMissing() = runTest {
        val entity = IssueDraftEntity("42", "jetbrains", "kotlin", "Title", "Body", 2, 100L)
        coEvery { issueDraftDao.getById("42") } returns entity
        coEvery { issueDraftDao.getById("missing") } returns null

        val found = repository.getDraft("42")
        assertEquals("42", found?.uid)
        assertEquals(DraftPriority.CRITICAL, found?.priority)

        assertNull(repository.getDraft("missing"))
    }

    @Test
    fun save_updatesRoomDaoAndRemote() = runTest {
        val draft = IssueDraft(uid = "1", owner = "o", repoName = "r", title = "T", body = "B", priority = DraftPriority.CRITICAL)

        repository.save(draft)

        coVerify { issueDraftDao.upsert(any()) }
        coVerify { remoteDataSource.pushDraft(any()) }
    }

    @Test(expected = IOException::class)
    fun save_whenRemoteFailsWithIOException_rethrowsExceptionAndKeepsRoomRecord() = runTest {
        coEvery { remoteDataSource.pushDraft(any()) } throws IOException("ГОСТ Error")

        val draft = IssueDraft(uid = "1", owner = "o", repoName = "r", title = "T", body = "B")

        try {
            repository.save(draft)
        } finally {
            coVerify { issueDraftDao.upsert(any()) }
        }
    }

    @Test
    fun delete_removesFromRoomDaoAndRemote() = runTest {
        repository.delete("1")

        coVerify { issueDraftDao.deleteById("1") }
        coVerify { remoteDataSource.deleteDraft("1") }
    }

    @Test
    fun refreshFromBackend_whenSuccess_replacesAllInRoomDao() = runTest {
        val dto = IssueDraftDto("10", "jetbrains", "kotlin", "Remote", "Body", "LOW", 500L)
        coEvery { remoteDataSource.fetchDrafts() } returns listOf(dto)

        repository.refreshFromBackend()

        coVerify {
            issueDraftDao.replaceAll(
                match { list ->
                    list.size == 1 && list[0].uid == "10" && list[0].priority == 0
                }
            )
        }
    }

    @Test(expected = IOException::class)
    fun refreshFromBackend_whenIOException_doesNotReplaceRoomCache() = runTest {
        coEvery { remoteDataSource.fetchDrafts() } throws IOException("Network down")

        try {
            repository.refreshFromBackend()
        } finally {
            coVerify(exactly = 0) { issueDraftDao.replaceAll(any()) }
        }
    }

    @Test(expected = CancellationException::class)
    fun refreshFromBackend_whenCancellationException_rethrows() = runTest {
        coEvery { remoteDataSource.fetchDrafts() } throws CancellationException("Cancelled")

        repository.refreshFromBackend()
    }
}
