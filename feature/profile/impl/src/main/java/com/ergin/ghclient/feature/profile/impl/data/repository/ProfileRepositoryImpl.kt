package com.ergin.ghclient.feature.profile.impl.data.repository

import com.ergin.ghclient.core.database.dao.UserProfileDao
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.network.safeApiCall
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.feature.profile.domain.model.UserProfile
import com.ergin.ghclient.feature.profile.domain.repository.ProfileRepository
import com.ergin.ghclient.feature.profile.impl.data.mapper.toDomain
import com.ergin.ghclient.feature.profile.impl.data.mapper.toEntity
import com.ergin.ghclient.feature.profile.impl.data.remote.ProfileApi
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val userProfileDao: UserProfileDao
) : ProfileRepository {

    override suspend fun getAuthenticatedUser(): Result<UserProfile, DomainError.Network> {
        val result = safeApiCall {
            profileApi.getAuthenticatedUser()
        }
        return when (result) {
            is Result.Success -> {
                val profile = result.data.toDomain()
                userProfileDao.insertProfile(profile.toEntity())
                Result.Success(profile)
            }
            is Result.Error -> {
                if (result.error == DomainError.Network.NO_INTERNET) {
                    val cachedProfile = userProfileDao.getLastProfile()
                    if (cachedProfile != null) {
                        return Result.Success(cachedProfile.toDomain())
                    }
                }
                Result.Error(result.error)
            }
        }
    }

    override suspend fun getUserActivity(username: String): Result<List<UserActivity>, DomainError.Network> {
        val result = safeApiCall {
            profileApi.getUserActivity(username)
        }
        return when (result) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Error -> Result.Error(result.error)
        }
    }
}
