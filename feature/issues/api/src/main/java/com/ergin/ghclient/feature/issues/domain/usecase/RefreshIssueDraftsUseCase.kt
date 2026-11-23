package com.ergin.ghclient.feature.issues.domain.usecase

import com.ergin.ghclient.feature.issues.domain.repository.IssueDraftRepository
import javax.inject.Inject

class RefreshIssueDraftsUseCase @Inject constructor(
    private val repository: IssueDraftRepository
) {
    suspend operator fun invoke() {
        repository.refreshFromBackend()
    }
}
