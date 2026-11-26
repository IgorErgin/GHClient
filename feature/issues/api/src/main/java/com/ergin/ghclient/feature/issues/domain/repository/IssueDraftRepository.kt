package com.ergin.ghclient.feature.issues.domain.repository

import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import kotlinx.coroutines.flow.Flow

interface IssueDraftRepository {
    fun observeDrafts(): Flow<List<IssueDraft>>
    suspend fun getDraft(uid: String): IssueDraft?
    suspend fun save(draft: IssueDraft)
    suspend fun delete(uid: String)
    suspend fun refreshFromBackend()
}
