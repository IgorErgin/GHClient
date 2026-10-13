package com.ergin.ghclient.feature.repository.impl.data.remote

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Интеграционный тест с вызовами к https://api.github.com/ для RepositoryApi
 */
class LiveRepositoryApiIntegrationTest {

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

    private val repositoryApi = retrofit.create(RepositoryApi::class.java)

    @Test
    fun testLiveGetRepoDetails() = runBlocking {
        println("📡 [LIVE] Запрос деталей репозитория octocat/Hello-World...")
        val details = repositoryApi.getRepoDetails("octocat", "Hello-World")

        assertNotNull(details)
        assertEquals("Hello-World", details.name)
        assertEquals("octocat", details.owner.login)
        println("✅ УСПЕХ: ${details.name} (звезд: ${details.stargazersCount}, branch: ${details.defaultBranch})")
    }

    @Test
    fun testLiveGetRepoReadme() = runBlocking {
        println("📡 [LIVE] Запрос README файла octocat/Hello-World...")
        val readme = repositoryApi.getRepoReadme("octocat", "Hello-World")

        assertNotNull(readme)
        assertTrue(readme.name.startsWith("README"))
        assertNotNull(readme.content)
        println("✅ УСПЕХ: Найден README (имя: ${readme.name}, encoding: ${readme.encoding})")
    }

    @Test
    fun testLiveGetRepoContents() = runBlocking {
        println("📡 [LIVE] Запрос содержимого директории octocat/Hello-World...")
        val contents = repositoryApi.getRepoContents("octocat", "Hello-World", "")

        assertTrue(contents.isNotEmpty())
        println("✅ УСПЕХ: Найдено ${contents.size} файлов/папок (первый: ${contents.first().name}, тип: ${contents.first().type})")
    }

    @Test
    fun testLiveGetCommits() = runBlocking {
        println("📡 [LIVE] Запрос коммитов octocat/Hello-World...")
        val commits = repositoryApi.getCommits("octocat", "Hello-World")

        assertTrue(commits.isNotEmpty())
        val firstCommit = commits.first()
        println("✅ УСПЕХ: Найдено ${commits.size} коммитов (последний: '${firstCommit.commit.message}')")
    }
}
