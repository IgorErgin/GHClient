package com.ergin.ghclient.feature.search.impl.data.remote

import com.ergin.ghclient.feature.search.impl.data.remote.model.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): SearchResponseDto
}
