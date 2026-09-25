package com.ergin.ghclient.feature.search.impl.data.remote

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
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Интеграционный тест с использованием продуктовых HeaderInterceptor и AuthInterceptor
 */
class LiveGitHubApiIntegrationTest {

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

    private val searchApi = retrofit.create(SearchApi::class.java)

    @Test
    fun testRealNetworkRequestToGitHubApi() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос на https://api.github.com/search/repositories?q=kotlin...")

        val response = searchApi.searchRepositories(query = "kotlin", page = 1, perPage = 5)

        assertNotNull("Ответ сервера не должен быть null", response)
        assertTrue("Общее количество найденных репозиториев должно быть > 0", response.totalCount > 0)
        assertTrue("Список элементов не должен быть пустым", response.items.isNotEmpty())

        val firstRepo = response.items.first()
        assertNotNull("Имя репозитория должно быть заполнено", firstRepo.name)
        assertNotNull("Логин владельца должен быть заполнен", firstRepo.owner.login)

        println("✅ УСПЕХ с HeaderInterceptor и AuthInterceptor:")
        println("   - Название репозитория: ${firstRepo.name}")
        println("   - Владелец: ${firstRepo.owner.login}")
        println("   - Звезд: ${firstRepo.stargazersCount}")
        println("   - Язык: ${firstRepo.language}")
    }
}
