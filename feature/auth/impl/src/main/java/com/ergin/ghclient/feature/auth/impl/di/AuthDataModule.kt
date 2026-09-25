package com.ergin.ghclient.feature.auth.impl.di

import com.ergin.ghclient.feature.auth.domain.repository.AuthRepository
import com.ergin.ghclient.feature.auth.impl.data.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
