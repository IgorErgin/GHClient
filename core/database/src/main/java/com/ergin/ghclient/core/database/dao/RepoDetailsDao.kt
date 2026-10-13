package com.ergin.ghclient.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ergin.ghclient.core.database.entity.RepoDetailsEntity

@Dao
interface RepoDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepoDetails(details: RepoDetailsEntity)

    @Query("SELECT * FROM repo_details WHERE ownerName = :ownerName AND name = :repoName")
    suspend fun getRepoDetails(ownerName: String, repoName: String): RepoDetailsEntity?

    @Query("UPDATE repo_details SET readmeContent = :readme WHERE ownerName = :ownerName AND name = :repoName")
    suspend fun updateReadme(ownerName: String, repoName: String, readme: String)
}
