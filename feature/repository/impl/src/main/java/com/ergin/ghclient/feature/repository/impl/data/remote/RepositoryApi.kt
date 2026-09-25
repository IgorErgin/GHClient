package com.ergin.ghclient.feature.repository.impl.data.remote

import com.ergin.ghclient.feature.repository.impl.data.remote.model.CommitDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.FileNodeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.PullRequestDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.ReadmeDto
import com.ergin.ghclient.feature.repository.impl.data.remote.model.RepoDetailsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface RepositoryApi {

    @GET("repos/{owner}/{repo}")
    suspend fun getRepoDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepoDetailsDto

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getRepoReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): ReadmeDto

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getRepoContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): List<FileNodeDto>

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<CommitDto>

    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<PullRequestDto>
}
