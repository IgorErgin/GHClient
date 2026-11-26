package com.ergin.ghclient.feature.issues.impl.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.domain.usecase.DeleteIssueDraftUseCase
import com.ergin.ghclient.feature.issues.domain.usecase.ObserveIssueDraftsUseCase
import com.ergin.ghclient.feature.issues.domain.usecase.RefreshIssueDraftsUseCase
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsEffect
import com.ergin.ghclient.feature.issues.impl.presentation.contract.IssueDraftsEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class IssueDraftsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var observeUseCase: ObserveIssueDraftsUseCase
    private lateinit var refreshUseCase: RefreshIssueDraftsUseCase
    private lateinit var deleteUseCase: DeleteIssueDraftUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        observeUseCase = mockk()
        refreshUseCase = mockk(relaxed = true)
        deleteUseCase = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(mapOf("owner" to "jetbrains", "repoName" to "kotlin"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_observesDraftsAndFiltersByRepo() = runTest {
        val draft1 = IssueDraft(uid = "1", owner = "jetbrains", repoName = "kotlin", title = "T1", body = "B1")
        val draft2 = IssueDraft(uid = "2", owner = "other", repoName = "repo", title = "T2", body = "B2")

        coEvery { observeUseCase() } returns flowOf(listOf(draft1, draft2))

        val viewModel = IssueDraftsViewModel(observeUseCase, refreshUseCase, deleteUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.drafts.size)
        assertEquals("1", state.drafts[0].uid)
    }

    @Test
    fun refreshClicked_whenIOException_emitsShowSnackbarEffect() = runTest {
        coEvery { observeUseCase() } returns flowOf(emptyList())
        coEvery { refreshUseCase() } throws IOException("Сбой сети")

        val viewModel = IssueDraftsViewModel(observeUseCase, refreshUseCase, deleteUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(IssueDraftsEvent.RefreshClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assert(effect is IssueDraftsEffect.ShowSnackbar)
            assert((effect as IssueDraftsEffect.ShowSnackbar).message.contains("Сбой сети"))
        }
    }
}
