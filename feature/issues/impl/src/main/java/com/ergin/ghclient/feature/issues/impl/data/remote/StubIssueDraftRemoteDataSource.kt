package com.ergin.ghclient.feature.issues.impl.data.remote

import android.util.Log
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubIssueDraftRemoteDataSource @Inject constructor() : IssueDraftRemoteDataSource {

    private val remoteDrafts = mutableListOf<IssueDraftDto>()
    private val callCounter = AtomicInteger(0)

    override suspend fun fetchDrafts(): List<IssueDraftDto> {
        Log.d(TAG, "fetchDrafts() called")
        delay(500L)
        checkSimulateFailure("fetchDrafts")
        return remoteDrafts.toList()
    }

    override suspend fun pushDraft(draft: IssueDraftDto) {
        Log.d(TAG, "pushDraft(uid=${draft.uid}, title='${draft.title}') called")
        delay(500L)
        checkSimulateFailure("pushDraft")
        val index = remoteDrafts.indexOfFirst { it.uid == draft.uid }
        if (index >= 0) {
            remoteDrafts[index] = draft
        } else {
            remoteDrafts.add(draft)
        }
    }

    override suspend fun deleteDraft(uid: String) {
        Log.d(TAG, "deleteDraft(uid=$uid) called")
        delay(500L)
        checkSimulateFailure("deleteDraft")
        remoteDrafts.removeAll { it.uid == uid }
    }

    private fun checkSimulateFailure(methodName: String) {
        val count = callCounter.incrementAndGet()
        if (count % 3 == 0) {
            Log.w(TAG, "Simulating network failure on call #$count ($methodName)")
            throw IOException("Симуляция сбоя связи с сервером ГОСТ")
        }
    }

    companion object {
        private const val TAG = "IssueDraftRemote"
    }
}
