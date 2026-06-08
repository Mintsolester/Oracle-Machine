package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oracle_sessions")
data class OracleSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val timestamp: Long,
    val dateString: String, // format "yyyy-MM-dd"
    val responseJson: String, // Serialized ArtifactPackage
    val theme: String = "Ancient",
    val currentLayer: Int = 1, // 1 to 3
    val layerExtensions: String = "" // JSON mapped layers
)

@Entity(tableName = "lore_posts")
data class LorePost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val author: String,
    val dateString: String,
    val questionSnippet: String,
    val messageSnippet: String,
    val userInterpretation: String,
    val timestamp: Long = System.currentTimeMillis()
)
