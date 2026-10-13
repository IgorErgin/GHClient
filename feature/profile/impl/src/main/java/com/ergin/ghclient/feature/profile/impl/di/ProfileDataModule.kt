package com.ergin.ghclient.feature.profile.impl.di

import com.ergin.ghclient.feature.profile.domain.repository.ProfileRepository
import com.ergin.ghclient.feature.profile.impl.data.repository.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileDataModule {

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
