package com.ergin.ghclient.feature.repository.impl.di

import com.ergin.ghclient.feature.repository.domain.repository.RepoDetailsRepository
import com.ergin.ghclient.feature.repository.impl.data.repository.RepoDetailsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryDataModule {

    @Binds
    abstract fun bindRepoDetailsRepository(impl: RepoDetailsRepositoryImpl): RepoDetailsRepository
}
