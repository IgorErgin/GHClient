package com.ergin.ghclient.core.database.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import java.util.concurrent.Executors
import javax.inject.Singleton

private const val DB_LOG_TAG = "DatabaseLayer"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GHClientDatabase {
        val queryExecutor = Executors.newSingleThreadExecutor()
        return Room.databaseBuilder(
            context,
            GHClientDatabase::class.java,
            "ghclient.db"
        )
            .setQueryCallback({ sqlQuery, bindArgs ->
                Log.d(DB_LOG_TAG, "SQL Query: $sqlQuery | Args: $bindArgs")
            }, queryExecutor)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d(DB_LOG_TAG, "GHClientDatabase created: v${db.version}")
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d(DB_LOG_TAG, "GHClientDatabase opened: v${db.version}")
                }
            })
            .fallbackToDestructiveMigration(false)
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
