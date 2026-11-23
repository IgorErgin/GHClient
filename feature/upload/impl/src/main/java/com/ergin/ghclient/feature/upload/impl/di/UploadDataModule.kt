package com.ergin.ghclient.feature.upload.impl.di

import com.ergin.ghclient.feature.upload.domain.repository.UploadRepository
import com.ergin.ghclient.feature.upload.impl.data.repository.UploadRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UploadDataModule {

    @Binds
    @Singleton
    abstract fun bindUploadRepository(impl: UploadRepositoryImpl): UploadRepository
}
