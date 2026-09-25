package com.ergin.ghclient.feature.issues.impl.data.remote

import com.ergin.ghclient.core.datastore.TokenStorage
import com.ergin.ghclient.core.domain.model.AccessToken
import com.ergin.ghclient.core.network.AuthInterceptor
import com.ergin.ghclient.core.network.HeaderInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Интеграционный тест с HeaderInterceptor и AuthInterceptor для IssueApi
 */
class LiveIssueApiIntegrationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val fakeTokenStorage = object : TokenStorage {
        override val tokenFlow: Flow<AccessToken?> = flowOf(null)
        override suspend fun saveToken(token: AccessToken) {}
        override suspend fun clearToken() {}
        override fun getTokenSync(): AccessToken? = null
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor(fakeTokenStorage))
        .addInterceptor(AuthInterceptor(fakeTokenStorage))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val issueApi = retrofit.create(IssueApi::class.java)

    @Test
    fun testLiveGetIssues() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос публичных задач (Issues) для octocat/Hello-World...")
        val issues = issueApi.getIssues("octocat", "Hello-World")

        assertNotNull(issues)
        println("✅ УСПЕХ с HeaderInterceptor: Загружен список задач (размер: ${issues.size})")
    }
}
