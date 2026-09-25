package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val createdAt: Long = System.currentTimeMillis(),
  val lastMessage: String = "",
  val urgencyLevel: String = "LOW"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: Long,
  val sender: String, // "USER" or "MEDIX"
  val rawText: String,
  val timestamp: Long = System.currentTimeMillis(),
  val urgency: String = "LOW", // "LOW", "MODERATE", "HIGH", "EMERGENCY"
  val sympathyNote: String = "",
  val potentialCauses: String = "",
  val nextSteps: String = "",
  val redFlags: String = "",
  val specialist: String = "",
  val isBookmarked: Boolean = false,
  val modelUsed: String = "gemini-3.5-flash",
  val searchSourcesJson: String = "",
  val nearbyPlacesJson: String = ""
)

@Entity(tableName = "health_profile")
data class HealthProfileEntity(
  @PrimaryKey val id: Int = 1,
  val userName: String = "Patient",
  val ageGroup: String = "Adult (18-64)",
  val allergies: String = "None known",
  val chronicConditions: String = "None",
  val preferredLanguage: String = "English" // English, Hindi, Hinglish
)
