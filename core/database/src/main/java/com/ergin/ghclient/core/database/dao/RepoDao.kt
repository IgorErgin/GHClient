package com.ergin.ghclient.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ergin.ghclient.core.database.entity.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {

    @Query("SELECT * FROM repos")
    fun getAllCachedRepos(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repos WHERE isFavorite = 1")
    fun getFavoriteRepos(): Flow<List<RepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<RepoEntity>)

    @Query("UPDATE repos SET isFavorite = :isFavorite WHERE id = :repoId")
    suspend fun updateFavorite(repoId: Long, isFavorite: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM repos WHERE id = :repoId AND isFavorite = 1)")
    suspend fun isFavorite(repoId: Long): Boolean
}
