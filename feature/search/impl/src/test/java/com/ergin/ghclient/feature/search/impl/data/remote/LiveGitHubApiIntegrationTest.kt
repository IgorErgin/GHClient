package com.ergin.ghclient.feature.search.impl.data.remote

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
 * Интеграционный тест, совершающий РЕАЛЬНЫЙ сетевой запрос к https://api.github.com/
 */
class LiveGitHubApiIntegrationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader("X-GitHub-Api-Version", "2026-03-10")
                .addHeader("User-Agent", "GHClient-Android-App")
                .build()
            chain.proceed(request)
        }
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
        println("📡 Отправляем реальный HTTP-запрос на https://api.github.com/search/repositories?q=kotlin...")

        val response = searchApi.searchRepositories(query = "kotlin", page = 1, perPage = 5)

        assertNotNull("Ответ сервера не должен быть null", response)
        assertTrue("Общее количество найденных репозиториев должно быть > 0", response.totalCount > 0)
        assertTrue("Список элементов не должен быть пустым", response.items.isNotEmpty())

        val firstRepo = response.items.first()
        assertNotNull("Имя репозитория должно быть заполнено", firstRepo.name)
        assertNotNull("Логин владельца должен быть заполнен", firstRepo.owner.login)

        println("✅ УСПЕХ! Получен реальный ответ от сервера GitHub API:")
        println("   - Название репозитория: ${firstRepo.name}")
        println("   - Владелец: ${firstRepo.owner.login}")
        println("   - Звезд: ${firstRepo.stargazersCount}")
        println("   - Язык: ${firstRepo.language}")
    }
}
