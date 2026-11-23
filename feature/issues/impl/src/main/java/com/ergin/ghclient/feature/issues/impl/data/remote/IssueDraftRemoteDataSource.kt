package com.ergin.ghclient.feature.issues.impl.data.remote

import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto

interface IssueDraftRemoteDataSource {
    suspend fun fetchDrafts(): List<IssueDraftDto>
    suspend fun pushDraft(draft: IssueDraftDto)
    suspend fun deleteDraft(uid: String)
}
