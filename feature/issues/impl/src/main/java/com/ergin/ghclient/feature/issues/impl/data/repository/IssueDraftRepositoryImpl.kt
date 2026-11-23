package com.ergin.ghclient.feature.issues.impl.data.repository

import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.domain.repository.IssueDraftRepository
import com.ergin.ghclient.feature.issues.impl.data.local.IssueDraftFileStorage
import com.ergin.ghclient.feature.issues.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.issues.impl.data.mapper.toDto
import com.ergin.ghclient.feature.issues.impl.data.remote.IssueDraftRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueDraftRepositoryImpl @Inject constructor(
    private val fileStorage: IssueDraftFileStorage,
    private val remoteDataSource: IssueDraftRemoteDataSource
) : IssueDraftRepository {

    private val initMutex = Mutex()
    private var isInitialized = false

    private val _draftsFlow = MutableStateFlow<List<IssueDraft>>(emptyList())

    override fun observeDrafts(): Flow<List<IssueDraft>> {
        return _draftsFlow.asStateFlow().onStart {
            ensureInitialized()
        }
    }

    private suspend fun ensureInitialized() {
        if (!isInitialized) {
            initMutex.withLock {
                if (!isInitialized) {
                    val localDtos = fileStorage.loadAll()
                    _draftsFlow.value = localDtos.map { it.toDomain() }
                    isInitialized = true
                }
            }
        }
    }

    override suspend fun getDraft(uid: String): IssueDraft? {
        ensureInitialized()
        val inMemory = _draftsFlow.value.firstOrNull { it.uid == uid }
        if (inMemory != null) return inMemory

        val dto = fileStorage.getById(uid) ?: return null
        return dto.toDomain()
    }

    override suspend fun save(draft: IssueDraft) {
        ensureInitialized()
        val dto = draft.toDto()

        fileStorage.save(dto)

        val currentList = _draftsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.uid == draft.uid }
        if (index >= 0) {
            currentList[index] = draft
        } else {
            currentList.add(draft)
        }
        _draftsFlow.value = currentList

        try {
            remoteDataSource.pushDraft(dto)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (e is IOException) throw e
            throw e
        }
    }

    override suspend fun delete(uid: String) {
        ensureInitialized()

        fileStorage.delete(uid)

        _draftsFlow.value = _draftsFlow.value.filterNot { it.uid == uid }

        try {
            remoteDataSource.deleteDraft(uid)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (e is IOException) throw e
            throw e
        }
    }

    override suspend fun refreshFromBackend() {
        ensureInitialized()

        try {
            val remoteDtos = remoteDataSource.fetchDrafts()
            fileStorage.saveAll(remoteDtos)
            _draftsFlow.value = remoteDtos.map { it.toDomain() }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (e is IOException) throw e
            throw e
        }
    }
}
