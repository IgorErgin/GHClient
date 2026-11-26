package com.ergin.ghclient.feature.auth.impl.data.remote

import com.ergin.ghclient.core.network.GithubAuthConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

@Serializable
data class OAuthTokenResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("scope") val scope: String? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null
)

interface OAuthApi {
    @FormUrlEncoded
    @POST("https://github.com/login/oauth/access_token")
    @Headers(
        "Accept: application/json",
        "User-Agent: GHClient-Android-App"
    )
    suspend fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = GithubAuthConfig.REDIRECT_URI
    ): OAuthTokenResponse
}
