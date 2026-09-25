package com.ergin.ghclient.feature.upload.impl.di

import com.ergin.ghclient.feature.upload.impl.data.remote.UploadApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UploadNetworkModule {

    @Provides
    @Singleton
    fun provideUploadApi(retrofit: Retrofit): UploadApi {
        return retrofit.create(UploadApi::class.java)
    }
}
