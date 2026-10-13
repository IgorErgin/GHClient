package com.ergin.ghclient.feature.upload.impl.data.remote

import com.ergin.ghclient.core.datastore.TokenStorage
import com.ergin.ghclient.core.domain.model.AccessToken
import com.ergin.ghclient.core.network.AuthInterceptor
import com.ergin.ghclient.core.network.HeaderInterceptor
import com.ergin.ghclient.feature.upload.impl.data.remote.model.UploadFileRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Интеграционный тест с продуктовыми HeaderInterceptor и AuthInterceptor для UploadApi
 */
class LiveUploadApiIntegrationTest {

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

    private val uploadApi = retrofit.create(UploadApi::class.java)

    @Test
    fun testLiveUploadWithoutTokenReturns401() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос PUT /contents без токена (ожидаем 401 Unauthorized)...")
        val requestDto = UploadFileRequestDto(
            message = "Test upload",
            content = "Y29udGVudA==",
            branch = "main"
        )
        try {
            uploadApi.uploadFile("octocat", "Hello-World", "test.txt", requestDto)
            assert(false) { "Загрузка файла без токена не должна работать" }
        } catch (e: HttpException) {
            assertEquals(401, e.code())
            println("✅ УСПЕХ с HeaderInterceptor и AuthInterceptor: Загрузка файла анонимно отклонена сервером с HTTP 401!")
        }
    }
}
