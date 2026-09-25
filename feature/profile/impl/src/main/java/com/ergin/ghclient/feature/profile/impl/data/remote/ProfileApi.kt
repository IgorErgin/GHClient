package com.ergin.ghclient.feature.profile.impl.data.remote

import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserActivityDto
import com.ergin.ghclient.feature.profile.impl.data.remote.model.UserProfileDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {

    @GET("user")
    suspend fun getAuthenticatedUser(): UserProfileDto

    @GET("users/{username}/events")
    suspend fun getUserActivity(
        @Path("username") username: String
    ): List<UserActivityDto>
}
