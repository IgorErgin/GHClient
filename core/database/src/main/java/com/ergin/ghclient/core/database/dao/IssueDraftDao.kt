package com.ergin.ghclient.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ergin.ghclient.core.database.entity.IssueDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDraftDao {

    @Query("SELECT * FROM issue_drafts ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<IssueDraftEntity>>

    @Query("SELECT * FROM issue_drafts WHERE uid = :uid LIMIT 1")
    suspend fun getById(uid: String): IssueDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(draft: IssueDraftEntity)

    @Update
    suspend fun update(draft: IssueDraftEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drafts: List<IssueDraftEntity>)

    @Query("DELETE FROM issue_drafts")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(drafts: List<IssueDraftEntity>) {
        clearAll()
        insertAll(drafts)
    }

    @Query("DELETE FROM issue_drafts WHERE uid = :uid")
    suspend fun deleteById(uid: String)
}
