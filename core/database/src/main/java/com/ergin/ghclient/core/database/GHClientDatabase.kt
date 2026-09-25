package com.ergin.ghclient.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ergin.ghclient.core.database.dao.RepoDao
import com.ergin.ghclient.core.database.entity.RepoEntity

@Database(
    entities = [RepoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GHClientDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}
