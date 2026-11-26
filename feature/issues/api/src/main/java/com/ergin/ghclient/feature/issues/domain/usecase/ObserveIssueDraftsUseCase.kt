package com.ergin.ghclient.feature.issues.domain.usecase

import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.domain.repository.IssueDraftRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIssueDraftsUseCase @Inject constructor(
    private val repository: IssueDraftRepository
) {
    operator fun invoke(): Flow<List<IssueDraft>> {
        return repository.observeDrafts()
    }
}
