package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OracleSessionDao {
    @Query("SELECT * FROM oracle_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<OracleSession>>

    @Query("SELECT * FROM oracle_sessions WHERE id = :id")
    fun getSessionById(id: Int): Flow<OracleSession?>

    @Query("SELECT * FROM oracle_sessions WHERE dateString = :dateString LIMIT 1")
    suspend fun getSessionByDate(dateString: String): OracleSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: OracleSession): Long

    @Update
    suspend fun updateSession(session: OracleSession)

    @Query("DELETE FROM oracle_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)

    @Query("DELETE FROM oracle_sessions")
    suspend fun clearAll()
}

@Dao
interface LorePostDao {
    @Query("SELECT * FROM lore_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<LorePost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: LorePost)

    @Query("DELETE FROM lore_posts")
    suspend fun clearAll()
}
