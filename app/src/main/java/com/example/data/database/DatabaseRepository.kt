package com.example.data.database

import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val oracleSessionDao: OracleSessionDao,
    private val lorePostDao: LorePostDao
) {
    val allSessions: Flow<List<OracleSession>> = oracleSessionDao.getAllSessions()
    val allLorePosts: Flow<List<LorePost>> = lorePostDao.getAllPosts()

    suspend fun getSessionById(id: Int): Flow<OracleSession?> {
        return oracleSessionDao.getSessionById(id)
    }

    suspend fun getSessionByDate(dateString: String): OracleSession? {
        return oracleSessionDao.getSessionByDate(dateString)
    }

    suspend fun insertSession(session: OracleSession): Long {
        return oracleSessionDao.insertSession(session)
    }

    suspend fun updateSession(session: OracleSession) {
        oracleSessionDao.updateSession(session)
    }

    suspend fun insertLorePost(post: LorePost) {
        lorePostDao.insertPost(post)
    }

    suspend fun clearAllSessions() {
        oracleSessionDao.clearAll()
    }

    suspend fun clearAllLore() {
        lorePostDao.clearAll()
    }
}
