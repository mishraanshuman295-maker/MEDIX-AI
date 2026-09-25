package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedixDao {

  @Query("SELECT * FROM chat_sessions ORDER BY createdAt DESC")
  fun getAllSessions(): Flow<List<ChatSessionEntity>>

  @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
  suspend fun getSessionById(sessionId: Long): ChatSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: ChatSessionEntity): Long

  @Update
  suspend fun updateSession(session: ChatSessionEntity)

  @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
  suspend fun deleteSession(sessionId: Long)

  @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
  suspend fun deleteMessagesForSession(sessionId: Long)

  @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
  fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity): Long

  @Query("UPDATE chat_messages SET isBookmarked = :isBookmarked WHERE id = :messageId")
  suspend fun updateBookmark(messageId: Long, isBookmarked: Boolean)

  @Query("SELECT * FROM chat_messages WHERE isBookmarked = 1 ORDER BY timestamp DESC")
  fun getBookmarkedMessages(): Flow<List<ChatMessageEntity>>

  @Query("SELECT * FROM health_profile WHERE id = 1 LIMIT 1")
  fun getProfile(): Flow<HealthProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: HealthProfileEntity)

  @Query("DELETE FROM chat_sessions")
  suspend fun clearAllSessions()

  @Query("DELETE FROM chat_messages")
  suspend fun clearAllMessages()
}
