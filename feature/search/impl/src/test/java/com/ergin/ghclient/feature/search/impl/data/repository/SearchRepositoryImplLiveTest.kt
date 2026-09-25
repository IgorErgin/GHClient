  package com.ergin.ghclient.feature.search.impl.data.repository

import com.ergin.ghclient.core.database.dao.RepoDao
import com.ergin.ghclient.core.database.entity.RepoEntity
import com.ergin.ghclient.core.datastore.TokenStorage
import com.ergin.ghclient.core.domain.Result
import com.ergin.ghclient.core.domain.model.AccessToken
import com.ergin.ghclient.core.network.AuthInterceptor
import com.ergin.ghclient.core.network.HeaderInterceptor
import com.ergin.ghclient.feature.search.impl.data.remote.SearchApi
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
 * Проверка сквозной цепочки с использованием ПРОДАКШН интерцепторов:
 * HeaderInterceptor + AuthInterceptor -> Retrofit -> DTO -> Mapper -> Domain
 */
class SearchRepositoryImplLiveTest {

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

    private val fakeRepoDao = object : RepoDao {
        override fun getAllCachedRepos(): Flow<List<RepoEntity>> = flowOf(emptyList())
        override fun getFavoriteRepos(): Flow<List<RepoEntity>> = flowOf(emptyList())
        override suspend fun insertRepos(repos: List<RepoEntity>) {}
        override suspend fun updateFavorite(repoId: Long, isFavorite: Boolean) {}
        override suspend fun isFavorite(repoId: Long): Boolean = false
    }

    private val repository = SearchRepositoryImpl(searchApi, fakeRepoDao)

    @Test
    fun testLiveNetworkToDomainMapping() = runBlocking {
        println("📡 [PRODUCTION INTERCEPTORS] Запрос поиска через HeaderInterceptor...")

        val result = repository.searchRepositories(query = "kotlin", page = 1, perPage = 5)

        assertTrue("Результат должен быть Success", result is Result.Success)
        val repos = (result as Result.Success).data
        assertTrue("Список репозиториев не пустой", repos.isNotEmpty())

        val firstRepo = repos.first()
        println("✅ УСПЕХ с HeaderInterceptor:")
        println("   - RepoID: ${firstRepo.id.value}")
        println("   - Owner: ${firstRepo.ownerName.value}")

        assertNotNull(firstRepo.id.value)
        assertNotNull(firstRepo.ownerName.value)
        assertTrue(firstRepo.stars > 0)
    }
}
