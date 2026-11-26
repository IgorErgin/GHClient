package com.ergin.ghclient.feature.issues.impl.data.repository

import com.ergin.ghclient.core.database.dao.IssueDraftDao
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.domain.repository.IssueDraftRepository
import com.ergin.ghclient.feature.issues.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.issues.impl.data.mapper.toDto
import com.ergin.ghclient.feature.issues.impl.data.mapper.toEntity
import com.ergin.ghclient.feature.issues.impl.data.remote.IssueDraftRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueDraftRepositoryImpl @Inject constructor(
    private val issueDraftDao: IssueDraftDao,
    private val remoteDataSource: IssueDraftRemoteDataSource
) : IssueDraftRepository {

    override fun observeDrafts(): Flow<List<IssueDraft>> {
        return issueDraftDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDraft(uid: String): IssueDraft? {
        return issueDraftDao.getById(uid)?.toDomain()
    }

    override suspend fun save(draft: IssueDraft) {
        issueDraftDao.upsert(draft.toEntity())
        remoteDataSource.pushDraft(draft.toDto())
    }

    override suspend fun delete(uid: String) {
        issueDraftDao.deleteById(uid)
        remoteDataSource.deleteDraft(uid)
    }

    override suspend fun refreshFromBackend() {
        val remoteDtos = remoteDataSource.fetchDrafts()
        val entities = remoteDtos.map { dto ->
            dto.toDomain().toEntity()
        }
        issueDraftDao.replaceAll(entities)
    }
}
