package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.AudioRecorderManager
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.HealthProfileEntity
import com.example.data.local.MedixDatabase
import com.example.data.model.BodyRegion
import com.example.data.model.SymptomCategoriesCatalog
import com.example.data.remote.AiModelType
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.MedixParsedResponse
import com.example.data.repository.MedixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GuidedAssessmentUiState(
  val selectedRegion: BodyRegion? = SymptomCategoriesCatalog.regions.firstOrNull(),
  val selectedSymptoms: Set<String> = emptySet(),
  val duration: String = "1-2 days",
  val severity: Float = 4f,
  val hasRedFlags: Boolean = false,
  val additionalNotes: String = "",
  val isAnalyzing: Boolean = false,
  val result: MedixParsedResponse? = null
)

class MedixViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: MedixRepository
  val audioRecorderManager = AudioRecorderManager(application)

  init {
    val db = MedixDatabase.getDatabase(application)
    repository = MedixRepository(db.medixDao())
  }

  val sessions: StateFlow<List<ChatSessionEntity>> = repository.getAllSessions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val bookmarks: StateFlow<List<ChatMessageEntity>> = repository.getBookmarkedMessages()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val profile: StateFlow<HealthProfileEntity?> = repository.getProfile()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  private val _currentSessionId = MutableStateFlow<Long?>(null)
  val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

  private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
  val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()

  private val _isThinking = MutableStateFlow(false)
  val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

  private val _selectedLanguage = MutableStateFlow("English")
  val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

  private val _selectedModel = MutableStateFlow(AiModelType.GEMINI_FLASH)
  val selectedModel: StateFlow<AiModelType> = _selectedModel.asStateFlow()

  private val _searchGroundingEnabled = MutableStateFlow(true)
  val searchGroundingEnabled: StateFlow<Boolean> = _searchGroundingEnabled.asStateFlow()

  private val _mapsGroundingEnabled = MutableStateFlow(false)
  val mapsGroundingEnabled: StateFlow<Boolean> = _mapsGroundingEnabled.asStateFlow()

  // Voice recording & Live API states
  private val _isRecordingAudio = MutableStateFlow(false)
  val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

  private val _isTranscribing = MutableStateFlow(false)
  val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

  private val _liveCallActive = MutableStateFlow(false)
  val liveCallActive: StateFlow<Boolean> = _liveCallActive.asStateFlow()

  private val _liveTranscribedText = MutableStateFlow("")
  val liveTranscribedText: StateFlow<String> = _liveTranscribedText.asStateFlow()

  private val _liveAiSpeechResponse = MutableStateFlow("")
  val liveAiSpeechResponse: StateFlow<String> = _liveAiSpeechResponse.asStateFlow()

  private val _emergencyBannerActive = MutableStateFlow(false)
  val emergencyBannerActive: StateFlow<Boolean> = _emergencyBannerActive.asStateFlow()

  private val _guidedState = MutableStateFlow(GuidedAssessmentUiState())
  val guidedState: StateFlow<GuidedAssessmentUiState> = _guidedState.asStateFlow()

  init {
    viewModelScope.launch {
      sessions.collect { list ->
        if (_currentSessionId.value == null) {
          if (list.isNotEmpty()) {
            selectSession(list.first().id)
          } else {
            val newId = repository.createNewSession("Initial Consultation")
            selectSession(newId)
          }
        }
      }
    }

    viewModelScope.launch {
      profile.collect { p ->
        if (p != null) {
          _selectedLanguage.value = p.preferredLanguage
        }
      }
    }
  }

  fun selectSession(sessionId: Long) {
    _currentSessionId.value = sessionId
    viewModelScope.launch {
      repository.getMessagesForSession(sessionId).collect { msgs ->
        _messages.value = msgs
        if (msgs.any { it.urgency == "EMERGENCY" }) {
          _emergencyBannerActive.value = true
        }
      }
    }
  }

  fun createNewSession() {
    viewModelScope.launch {
      val count = sessions.value.size + 1
      val newId = repository.createNewSession("Consultation #$count")
      selectSession(newId)
    }
  }

  fun deleteSession(sessionId: Long) {
    viewModelScope.launch {
      repository.deleteSession(sessionId)
      if (_currentSessionId.value == sessionId) {
        val remaining = sessions.value.filter { it.id != sessionId }
        if (remaining.isNotEmpty()) {
          selectSession(remaining.first().id)
        } else {
          val newId = repository.createNewSession("New Consultation")
          selectSession(newId)
        }
      }
    }
  }

  fun setLanguage(lang: String) {
    _selectedLanguage.value = lang
    viewModelScope.launch {
      val current = profile.value ?: HealthProfileEntity()
      repository.saveProfile(current.copy(preferredLanguage = lang))
    }
  }

  fun setModel(model: AiModelType) {
    _selectedModel.value = model
  }

  fun toggleSearchGrounding(enabled: Boolean) {
    _searchGroundingEnabled.value = enabled
  }

  fun toggleMapsGrounding(enabled: Boolean) {
    _mapsGroundingEnabled.value = enabled
  }

  fun dismissEmergencyBanner() {
    _emergencyBannerActive.value = false
  }

  fun sendMessage(query: String) {
    val trimmed = query.trim()
    if (trimmed.isBlank() || _isThinking.value) return

    val sId = _currentSessionId.value ?: return

    viewModelScope.launch {
      _isThinking.value = true
      try {
        val aiMsg = repository.sendUserMessage(
          sessionId = sId,
          userQuery = trimmed,
          language = _selectedLanguage.value,
          modelType = _selectedModel.value,
          enableSearch = _searchGroundingEnabled.value,
          enableMaps = _mapsGroundingEnabled.value
        )
        if (aiMsg.urgency == "EMERGENCY") {
          _emergencyBannerActive.value = true
        }
      } catch (e: Exception) {
        // Handled inside repository
      } finally {
        _isThinking.value = false
      }
    }
  }

  // Audio Recording & Gemini-3.5-transcribe
  fun startAudioRecording() {
    val started = audioRecorderManager.startRecording()
    if (started) {
      _isRecordingAudio.value = true
    }
  }

  fun stopAudioRecordingAndTranscribe(onTranscribed: (String) -> Unit) {
    _isRecordingAudio.value = false
    val base64 = audioRecorderManager.stopRecording()
    if (base64 != null) {
      viewModelScope.launch {
        _isTranscribing.value = true
        try {
          val text = GeminiApiClient.transcribeAudio(base64)
          _isTranscribing.value = false
          if (text.isNotBlank()) {
            onTranscribed(text)
          }
        } catch (e: Exception) {
          _isTranscribing.value = false
        }
      }
    }
  }

  fun cancelAudioRecording() {
    audioRecorderManager.cancelRecording()
    _isRecordingAudio.value = false
  }

  // Live Voice Conversation with gemini-3.8-live
  fun startLiveCall() {
    _liveCallActive.value = true
    _selectedModel.value = AiModelType.GEMINI_LIVE
    _liveTranscribedText.value = ""
    _liveAiSpeechResponse.value = "Hello! I am Medix AI in real-time voice mode. Please describe any symptoms or pain you are feeling."
  }

  fun endLiveCall() {
    _liveCallActive.value = false
    audioRecorderManager.cancelRecording()
    _isRecordingAudio.value = false
  }

  fun toggleLiveMic() {
    if (_isRecordingAudio.value) {
      val base64 = audioRecorderManager.stopRecording()
      _isRecordingAudio.value = false
      if (base64 != null) {
        viewModelScope.launch {
          _isThinking.value = true
          val transcribed = GeminiApiClient.transcribeAudio(base64)
          _liveTranscribedText.value = transcribed
          val response = GeminiApiClient.askMedix(
            userMessage = transcribed,
            conversationHistory = listOf("USER" to transcribed),
            userLanguage = _selectedLanguage.value,
            modelType = AiModelType.GEMINI_LIVE,
            enableGoogleSearch = _searchGroundingEnabled.value,
            enableGoogleMaps = _mapsGroundingEnabled.value
          )
          _liveAiSpeechResponse.value = response.sympathyNote + " " + response.potentialCauses.take(160)
          _isThinking.value = false
        }
      }
    } else {
      val ok = audioRecorderManager.startRecording()
      if (ok) _isRecordingAudio.value = true
    }
  }

  fun toggleBookmark(messageId: Long, isBookmarked: Boolean) {
    viewModelScope.launch {
      repository.toggleBookmark(messageId, isBookmarked)
    }
  }

  fun updateProfile(
    name: String,
    ageGroup: String,
    allergies: String,
    conditions: String,
    language: String
  ) {
    viewModelScope.launch {
      val entity = HealthProfileEntity(
        id = 1,
        userName = name,
        ageGroup = ageGroup,
        allergies = allergies,
        chronicConditions = conditions,
        preferredLanguage = language
      )
      repository.saveProfile(entity)
      _selectedLanguage.value = language
    }
  }

  // Guided Assessment Actions
  fun selectGuidedRegion(region: BodyRegion) {
    _guidedState.value = _guidedState.value.copy(
      selectedRegion = region,
      selectedSymptoms = emptySet(),
      result = null
    )
  }

  fun toggleGuidedSymptom(symptom: String) {
    val current = _guidedState.value.selectedSymptoms.toMutableSet()
    if (current.contains(symptom)) {
      current.remove(symptom)
    } else {
      current.add(symptom)
    }
    _guidedState.value = _guidedState.value.copy(selectedSymptoms = current)
  }

  fun setGuidedDuration(duration: String) {
    _guidedState.value = _guidedState.value.copy(duration = duration)
  }

  fun setGuidedSeverity(severity: Float) {
    _guidedState.value = _guidedState.value.copy(severity = severity)
  }

  fun setGuidedRedFlags(hasRedFlags: Boolean) {
    _guidedState.value = _guidedState.value.copy(hasRedFlags = hasRedFlags)
  }

  fun setGuidedNotes(notes: String) {
    _guidedState.value = _guidedState.value.copy(additionalNotes = notes)
  }

  fun runGuidedAnalysis() {
    val state = _guidedState.value
    val symptomsList = state.selectedSymptoms.joinToString(", ")
    val regionTitle = state.selectedRegion?.title ?: "General"
    val prompt = "Patient Assessment: Body Region: $regionTitle. Reported Symptoms: $symptomsList. Duration: ${state.duration}. Severity: ${state.severity.toInt()}/10. High-risk red flags reported: ${state.hasRedFlags}. Notes: ${state.additionalNotes}. Please provide a comprehensive empathetic evaluation with potential causes, specialist recommendation, questions for doctor, and emergency warnings."

    viewModelScope.launch {
      _guidedState.value = _guidedState.value.copy(isAnalyzing = true)
      try {
        val res = GeminiApiClient.askMedix(
          userMessage = prompt,
          conversationHistory = emptyList(),
          userLanguage = _selectedLanguage.value,
          modelType = _selectedModel.value,
          enableGoogleSearch = _searchGroundingEnabled.value,
          enableGoogleMaps = _mapsGroundingEnabled.value
        )
        _guidedState.value = _guidedState.value.copy(result = res, isAnalyzing = false)
      } catch (e: Exception) {
        val fallback = GeminiApiClient.generateLocalTriageResponse(prompt, _selectedLanguage.value, state.hasRedFlags || state.severity >= 8)
        _guidedState.value = _guidedState.value.copy(result = fallback, isAnalyzing = false)
      }
    }
  }

  fun resetGuidedAssessment() {
    _guidedState.value = GuidedAssessmentUiState(
      selectedRegion = SymptomCategoriesCatalog.regions.firstOrNull()
    )
  }
}
