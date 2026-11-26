package com.ergin.ghclient.feature.issues.domain.usecase

import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import com.ergin.ghclient.feature.issues.domain.repository.IssueDraftRepository
import javax.inject.Inject

class SaveIssueDraftUseCase @Inject constructor(
    private val repository: IssueDraftRepository
) {
    suspend operator fun invoke(draft: IssueDraft) {
        repository.save(draft)
    }
}
