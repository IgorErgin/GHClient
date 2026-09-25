package com.ergin.ghclient.core.network

import com.ergin.ghclient.core.datastore.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2026-03-10")

        val token = tokenStorage.getTokenSync()
        if (token != null && token.value.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer ${token.value}")
        }

        return chain.proceed(requestBuilder.build())
    }
}
