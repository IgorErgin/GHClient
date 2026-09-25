package com.ergin.ghclient.feature.repository.impl.data.repository

import com.ergin.ghclient.core.database.dao.RepoDetailsDao
import com.ergin.ghclient.core.database.entity.RepoDetailsEntity
import com.ergin.ghclient.core.datastore.TokenStorage
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.AccessToken
import com.ergin.ghclient.core.domain.model.OwnerName
import com.ergin.ghclient.core.domain.model.RepoName
import com.ergin.ghclient.core.network.AuthInterceptor
import com.ergin.ghclient.core.network.HeaderInterceptor
import com.ergin.ghclient.feature.repository.impl.data.remote.RepositoryApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
 * Проверка сквозной цепочки с использованием ПРОДАКШН интерцепторов:
 * HeaderInterceptor + AuthInterceptor -> RepositoryApi -> DTO -> Mapper -> Domain
 */
class RepoDetailsRepositoryImplLiveTest {

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

    private val repositoryApi = retrofit.create(RepositoryApi::class.java)

    private val fakeRepoDetailsDao = object : RepoDetailsDao {
        override suspend fun insertRepoDetails(details: RepoDetailsEntity) {}
        override suspend fun getRepoDetails(ownerName: String, repoName: String): RepoDetailsEntity? = null
        override suspend fun updateReadme(ownerName: String, repoName: String, readme: String) {}
    }

    private val repository = RepoDetailsRepositoryImpl(repositoryApi, fakeRepoDetailsDao)

    @Test
    fun testLiveRepoDetailsMappingWithProductionInterceptors() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос деталей octocat/Hello-World...")

        val result = repository.getRepoDetails(OwnerName("octocat"), RepoName("Hello-World"))

        assertTrue(result is Result.Success)
        val details = (result as Result.Success).data

        assertEquals(OwnerName("octocat"), details.ownerName)
        assertEquals("Hello-World", details.name)
        assertNotNull(details.defaultBranch)
        println("✅ УСПЕХ с HeaderInterceptor: RepoId=${details.id.value}, Owner=${details.ownerName.value}")
    }

    @Test
    fun testLiveRepoReadmeBase64DecodingAndMapping() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос README файла octocat/Hello-World...")

        val result = repository.getRepoReadme(OwnerName("octocat"), RepoName("Hello-World"))

        assertTrue(result is Result.Success)
        val readmeText = (result as Result.Success).data

        assertTrue("Текст README не должен быть пустым", readmeText.isNotBlank())
        println("✅ УСПЕХ с HeaderInterceptor: README раскодирован:")
        println("   Первые 50 символов: \"${readmeText.take(50).replace("\n", " ")}...\"")
    }
}
