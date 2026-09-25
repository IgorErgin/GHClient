package com.ergin.ghclient.feature.issues.impl.data.remote

import com.ergin.ghclient.feature.issues.impl.data.remote.model.CreateIssueRequestDto
import com.ergin.ghclient.feature.issues.impl.data.remote.model.IssueDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IssueApi {

    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<IssueDto>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body body: CreateIssueRequestDto
    ): IssueDto
}
