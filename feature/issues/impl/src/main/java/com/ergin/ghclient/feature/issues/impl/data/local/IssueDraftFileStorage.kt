package com.ergin.ghclient.feature.issues.impl.data.local

import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDraftDto

interface IssueDraftFileStorage {
    suspend fun loadAll(): List<IssueDraftDto>
    suspend fun saveAll(items: List<IssueDraftDto>)
    suspend fun getById(uid: String): IssueDraftDto?
    suspend fun save(item: IssueDraftDto)
    suspend fun delete(uid: String)
}
