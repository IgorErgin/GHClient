package com.ergin.ghclient.feature.profile.impl.data.remote

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Интеграционный тест с HeaderInterceptor и AuthInterceptor для ProfileApi
 */
class LiveProfileApiIntegrationTest {

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

    private val profileApi = retrofit.create(ProfileApi::class.java)

    @Test
    fun testLiveGetUserActivity() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос публичных событий пользователя 'octocat'...")
        val events = profileApi.getUserActivity("octocat")

        assertNotNull(events)
        println("✅ УСПЕХ с HeaderInterceptor: Загружено ${events.size} событий пользователя 'octocat'")
    }

    @Test
    fun testLiveGetAuthenticatedUserWithoutTokenReturns401() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос /user без токена (ожидаем 401 Unauthorized)...")
        try {
            profileApi.getAuthenticatedUser()
            assert(false) { "Запрос без токена не должен пройти с успехом" }
        } catch (e: HttpException) {
            assertEquals(401, e.code())
            println("✅ УСПЕХ с HeaderInterceptor и AuthInterceptor: Сервер вернул HTTP 401 Unauthorized!")
        }
    }
}
