package com.ergin.ghclient.feature.issues.impl.di

import com.ergin.ghclient.feature.issues.domain.repository.IssueRepository
import com.ergin.ghclient.feature.issues.impl.data.repository.IssueRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IssueDataModule {

    @Binds
    abstract fun bindIssueRepository(impl: IssueRepositoryImpl): IssueRepository
}
