package com.ergin.ghclient.core.database.di

import android.content.Context
import androidx.room.Room
import com.ergin.ghclient.core.database.GHClientDatabase
import com.ergin.ghclient.core.database.dao.RepoDao
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepoDao(
        database: GHClientDatabase
    ): RepoDao = database.repoDao()
}
