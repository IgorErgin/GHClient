package com.ergin.ghclient.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ergin.ghclient.core.database.dao.IssueDao
import com.ergin.ghclient.core.database.dao.RepoDao
import com.ergin.ghclient.core.database.dao.RepoDetailsDao
import com.ergin.ghclient.core.database.dao.UserProfileDao
import com.ergin.ghclient.core.database.entity.IssueEntity
import com.ergin.ghclient.core.database.entity.RepoDetailsEntity
import com.ergin.ghclient.core.database.entity.RepoEntity
import com.ergin.ghclient.core.database.entity.UserProfileEntity

@Database(
    entities = [
        RepoEntity::class,
        RepoDetailsEntity::class,
        UserProfileEntity::class,
        IssueEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GHClientDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun repoDetailsDao(): RepoDetailsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun issueDao(): IssueDao
}
