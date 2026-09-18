package com.ergin.ghclient.feature.profile.domain.repository

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.feature.profile.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getAuthenticatedUser(): Result<UserProfile, DomainError.Network>
    suspend fun getUserActivity(username: String): Result<List<UserActivity>, DomainError.Network>
}
