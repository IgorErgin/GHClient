package com.ergin.ghclient.feature.repository.impl.di

import com.ergin.ghclient.feature.repository.impl.data.remote.RepositoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryNetworkModule {

    @Provides
    @Singleton
    fun provideRepositoryApi(retrofit: Retrofit): RepositoryApi {
        return retrofit.create(RepositoryApi::class.java)
    }
}
