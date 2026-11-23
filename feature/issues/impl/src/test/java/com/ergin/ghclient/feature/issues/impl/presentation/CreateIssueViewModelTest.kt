package com.ergin.ghclient.feature.issues.impl.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ergin.ghclient.feature.issues.domain.model.DraftPriority
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.domain.usecase.CreateIssueUseCase
import com.ergin.ghclient.feature.issues.domain.usecase.GetIssueDraftUseCase
import com.ergin.ghclient.feature.issues.domain.usecase.SaveIssueDraftUseCase
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueEffect
import com.ergin.ghclient.feature.issues.impl.presentation.contract.CreateIssueEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CreateIssueViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var createIssueUseCase: CreateIssueUseCase
    private lateinit var getIssueDraftUseCase: GetIssueDraftUseCase
    private lateinit var saveIssueDraftUseCase: SaveIssueDraftUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        createIssueUseCase = mockk(relaxed = true)
        getIssueDraftUseCase = mockk(relaxed = true)
        saveIssueDraftUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initWithDraftUid_loadsDraftDataIntoState() = runTest {
        val draft = IssueDraft(
            uid = "draft123",
            owner = "jetbrains",
            repoName = "kotlin",
            title = "Existing Title",
            body = "Existing Body",
            priority = DraftPriority.CRITICAL
        )
        coEvery { getIssueDraftUseCase("draft123") } returns draft

        val handle = SavedStateHandle(mapOf("draftUid" to "draft123", "owner" to "jetbrains", "repoName" to "kotlin"))
        val viewModel = CreateIssueViewModel(createIssueUseCase, getIssueDraftUseCase, saveIssueDraftUseCase, handle)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Existing Title", state.titleInput)
        assertEquals("Existing Body", state.bodyInput)
        assertEquals(DraftPriority.CRITICAL, state.priority)
    }

    @Test
    fun saveDraftClicked_savesDraftAndEmitsSnackbarAndNavigateBack() = runTest {
        val handle = SavedStateHandle(mapOf("owner" to "jetbrains", "repoName" to "kotlin"))
        val viewModel = CreateIssueViewModel(createIssueUseCase, getIssueDraftUseCase, saveIssueDraftUseCase, handle)

        viewModel.onEvent(CreateIssueEvent.TitleChanged("New Draft Title"))
        viewModel.onEvent(CreateIssueEvent.BodyChanged("New Draft Body"))

        viewModel.effect.test {
            viewModel.onEvent(CreateIssueEvent.SaveDraftClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { saveIssueDraftUseCase(any()) }

            val effect1 = awaitItem()
            assert(effect1 is CreateIssueEffect.ShowSnackbar)

            val effect2 = awaitItem()
            assert(effect2 is CreateIssueEffect.NavigateBack)
        }
    }

    @Test
    fun saveDraftClicked_whenIOException_savesLocallyAndEmitsSnackbarAndNavigateBack() = runTest {
        coEvery { saveIssueDraftUseCase(any()) } throws IOException("Сбой сети ГОСТ")

        val handle = SavedStateHandle(mapOf("owner" to "jetbrains", "repoName" to "kotlin"))
        val viewModel = CreateIssueViewModel(createIssueUseCase, getIssueDraftUseCase, saveIssueDraftUseCase, handle)

        viewModel.onEvent(CreateIssueEvent.TitleChanged("Draft Title"))

        viewModel.effect.test {
            viewModel.onEvent(CreateIssueEvent.SaveDraftClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect1 = awaitItem()
            assert(effect1 is CreateIssueEffect.ShowSnackbar)
            assert((effect1 as CreateIssueEffect.ShowSnackbar).message.contains("локальном кэше"))

            val effect2 = awaitItem()
            assert(effect2 is CreateIssueEffect.NavigateBack)
        }
    }
}
