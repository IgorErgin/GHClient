package com.ergin.ghclient.core.database.di

import android.content.Context
import androidx.room.Room
import com.ergin.ghclient.core.database.GHClientDatabase
import com.ergin.ghclient.core.database.dao.IssueDao
import com.ergin.ghclient.core.database.dao.RepoDao
import com.ergin.ghclient.core.database.dao.RepoDetailsDao
import com.ergin.ghclient.core.database.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GHClientDatabase {
        return Room.databaseBuilder(
            context,
            GHClientDatabase::class.java,
            "ghclient.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRepoDao(
        database: GHClientDatabase
    ): RepoDao = database.repoDao()

    @Provides
    @Singleton
    fun provideRepoDetailsDao(
        database: GHClientDatabase
    ): RepoDetailsDao = database.repoDetailsDao()

    @Provides
    @Singleton
    fun provideUserProfileDao(
        database: GHClientDatabase
    ): UserProfileDao = database.userProfileDao()

    @Provides
    @Singleton
    fun provideIssueDao(
        database: GHClientDatabase
    ): IssueDao = database.issueDao()
}
