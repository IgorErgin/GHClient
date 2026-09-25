package com.ergin.ghclient.feature.issues.impl.di

import com.ergin.ghclient.feature.issues.impl.data.remote.IssueApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IssueNetworkModule {

    @Provides
    @Singleton
    fun provideIssueApi(retrofit: Retrofit): IssueApi {
        return retrofit.create(IssueApi::class.java)
    }
}
