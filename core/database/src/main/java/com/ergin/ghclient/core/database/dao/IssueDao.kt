package com.ergin.ghclient.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ergin.ghclient.core.database.entity.IssueEntity

@Dao
interface IssueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssues(issues: List<IssueEntity>)

    @Query("SELECT * FROM issues WHERE repoOwner = :repoOwner AND repoName = :repoName")
    suspend fun getIssues(repoOwner: String, repoName: String): List<IssueEntity>
}
