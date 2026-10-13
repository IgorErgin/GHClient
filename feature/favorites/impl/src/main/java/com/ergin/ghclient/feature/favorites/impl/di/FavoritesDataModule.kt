package com.ergin.ghclient.feature.favorites.impl.di

import com.ergin.ghclient.feature.favorites.domain.repository.FavoritesRepository
import com.ergin.ghclient.feature.favorites.impl.data.repository.FavoritesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesDataModule {

    @Binds
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository
}
