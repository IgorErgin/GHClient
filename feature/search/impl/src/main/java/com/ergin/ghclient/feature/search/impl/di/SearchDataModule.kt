package com.ergin.ghclient.feature.search.impl.di

import com.ergin.ghclient.feature.search.domain.repository.SearchRepository
import com.ergin.ghclient.feature.search.impl.data.repository.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchDataModule {

    @Binds
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}
