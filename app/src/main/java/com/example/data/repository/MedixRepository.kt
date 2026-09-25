package com.example.data.repository

import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.HealthProfileEntity
import com.example.data.local.MedixDao
import com.example.data.remote.AiModelType
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class MedixRepository(private val dao: MedixDao) {

  fun getAllSessions(): Flow<List<ChatSessionEntity>> = dao.getAllSessions()

  suspend fun createNewSession(title: String = "New Health Consultation"): Long {
    val session = ChatSessionEntity(
      title = title,
      createdAt = System.currentTimeMillis(),
      lastMessage = "Started consultation",
      urgencyLevel = "LOW"
    )
    return dao.insertSession(session)
  }

  suspend fun deleteSession(sessionId: Long) {
    dao.deleteMessagesForSession(sessionId)
    dao.deleteSession(sessionId)
  }

  fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>> =
    dao.getMessagesForSession(sessionId)

  suspend fun sendUserMessage(
    sessionId: Long,
    userQuery: String,
    language: String,
    modelType: AiModelType = AiModelType.GEMINI_FLASH,
    enableSearch: Boolean = true,
    enableMaps: Boolean = false,
    locationHint: String = ""
  ): ChatMessageEntity {
    // 1. Insert user message
    val userMsg = ChatMessageEntity(
      sessionId = sessionId,
      sender = "USER",
      rawText = userQuery,
      timestamp = System.currentTimeMillis(),
      urgency = if (GeminiApiClient.detectImmediateEmergency(userQuery)) "EMERGENCY" else "LOW",
      modelUsed = modelType.modelName
    )
    dao.insertMessage(userMsg)

    // 2. Fetch recent conversation history
    val existing = dao.getMessagesForSession(sessionId).firstOrNull() ?: emptyList()
    val historyPairs = existing.takeLast(6).map { it.sender to it.rawText }

    // 3. Call Gemini via GeminiApiClient
    val parsedResponse = GeminiApiClient.askMedix(
      userMessage = userQuery,
      conversationHistory = historyPairs,
      userLanguage = language,
      modelType = modelType,
      enableGoogleSearch = enableSearch,
      enableGoogleMaps = enableMaps,
      userLocationHint = locationHint
    )

    // Convert sources & places to JSON string for storage
    val sourcesJsonArray = JSONArray()
    parsedResponse.searchSources.forEach { src ->
      sourcesJsonArray.put(JSONObject().apply {
        put("title", src.title)
        put("uri", src.uri)
      })
    }

    val placesJsonArray = JSONArray()
    parsedResponse.nearbyPlaces.forEach { pl ->
      placesJsonArray.put(JSONObject().apply {
        put("name", pl.name)
        put("type", pl.type)
        put("address", pl.addressOrVicinity)
        put("mapsUri", pl.mapsUri)
      })
    }

    // 4. Save AI Response
    val aiMsg = ChatMessageEntity(
      sessionId = sessionId,
      sender = "MEDIX",
      rawText = parsedResponse.rawText,
      timestamp = System.currentTimeMillis(),
      urgency = parsedResponse.urgencyLevel,
      sympathyNote = parsedResponse.sympathyNote,
      potentialCauses = parsedResponse.potentialCauses,
      nextSteps = parsedResponse.nextSteps,
      redFlags = parsedResponse.redFlags,
      specialist = parsedResponse.recommendedSpecialist,
      isBookmarked = false,
      modelUsed = parsedResponse.usedModel,
      searchSourcesJson = sourcesJsonArray.toString(),
      nearbyPlacesJson = placesJsonArray.toString()
    )
    val id = dao.insertMessage(aiMsg)

    // 5. Update session preview & urgency
    val existingSession = dao.getSessionById(sessionId)
    if (existingSession != null) {
      val preview = if (userQuery.length > 40) userQuery.take(37) + "..." else userQuery
      dao.updateSession(
        existingSession.copy(
          lastMessage = preview,
          urgencyLevel = if (parsedResponse.urgencyLevel == "EMERGENCY") "EMERGENCY" else existingSession.urgencyLevel
        )
      )
    }

    return aiMsg.copy(id = id)
  }

  suspend fun toggleBookmark(messageId: Long, isBookmarked: Boolean) {
    dao.updateBookmark(messageId, isBookmarked)
  }

  fun getBookmarkedMessages(): Flow<List<ChatMessageEntity>> = dao.getBookmarkedMessages()

  fun getProfile(): Flow<HealthProfileEntity?> = dao.getProfile()

  suspend fun saveProfile(profile: HealthProfileEntity) {
    dao.insertOrUpdateProfile(profile)
  }

  suspend fun clearAll() {
    dao.clearAllMessages()
    dao.clearAllSessions()
  }
}
