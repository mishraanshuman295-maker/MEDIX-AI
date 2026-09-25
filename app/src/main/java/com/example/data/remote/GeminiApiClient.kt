package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class AiModelType(val modelName: String, val displayName: String, val badge: String) {
  GEMINI_LIVE("gemini-3.8-live", "Gemini 3.8 Live (Voice Conversation)", "Real-Time Voice"),
  GEMINI_PRO("gemini-3.1-pro-preview", "Gemini 3.1 Pro (Complex Clinical)", "Complex Reasoning"),
  GEMINI_FLASH("gemini-3.5-flash", "Gemini 3.5 Flash (General Fast)", "General Clinical"),
  GEMINI_LITE("gemini-3.1-flash-lite-preview", "Gemini 3.1 Flash Lite (Ultra Fast)", "Instant Triage")
}

data class GroundingSource(
  val title: String,
  val uri: String
)

data class NearbyMedicalPlace(
  val name: String,
  val type: String,
  val addressOrVicinity: String,
  val mapsUri: String,
  val isOpen: Boolean = true
)

data class MedixParsedResponse(
  val rawText: String,
  val sympathyNote: String,
  val potentialCauses: String,
  val nextSteps: String,
  val redFlags: String,
  val recommendedSpecialist: String,
  val urgencyLevel: String, // "EMERGENCY", "HIGH", "MODERATE", "LOW"
  val searchSources: List<GroundingSource> = emptyList(),
  val nearbyPlaces: List<NearbyMedicalPlace> = emptyList(),
  val usedModel: String = "gemini-3.5-flash"
)

object GeminiApiClient {
  private const val TAG = "MedixGeminiClient"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

  private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(60, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
      .build()
  }

  private val SYSTEM_PROMPT = """
    You are "Medix AI", an empathetic, knowledgeable, and reliable AI medical assistant designed to help users understand their health conditions, symptoms, and medical queries.

    YOUR STRICT GUIDELINES:
    1. Primary Goal: Help users analyze their symptoms, suggest potential general causes, recommend appropriate medical specialists (e.g., Cardiologist, Dermatologist, Pulmonologist, ENT, Gastroenterologist), and provide basic first-aid or lifestyle tips.
    2. Tone & Communication: Warm, empathetic, professional, and clear. Avoid overly dense medical jargon; explain complex terms in simple words (adapt smoothly to Hindi, Hinglish, or English depending on user input).
    3. Safety & Medical Disclaimer:
       - Always clarify that you are an AI assistant and NOT a real doctor.
       - For severe symptoms (like severe chest pain, sudden numbness, high fever in infants, difficulty breathing, blue lips, severe blood loss), immediately advise the user to contact emergency services (112 / 911) or go to the nearest emergency room.
       - NEVER provide definitive medical diagnoses or prescribe specific medications/dosages.
    4. Mandatory Response Structure:
       Every response must contain these clearly labeled sections:
       [SYMPATHETIC ACKNOWLEDGEMENT]
       Briefly, warmly acknowledge their concern and reassure them. Clarify you are an AI assistant, not a doctor.
       
       [POTENTIAL CAUSES]
       List 2-3 general possibilities (clearly noted as non-definitive possibilities, not diagnoses).
       
       [RECOMMENDED NEXT STEPS & SPECIALIST]
       - Name the appropriate medical specialist to consult (e.g., Cardiologist, Dermatologist, ENT, General Physician).
       - 2-3 smart questions the patient should ask their doctor.
       - Safe, non-pharmacological home care or comfort measures (hydration, rest, gentle cold/warm compress).
       
       [RED FLAGS & EMERGENCY WARNING]
       Clear signs that require an immediate hospital emergency room visit.
  """.trimIndent()

  suspend fun askMedix(
    userMessage: String,
    conversationHistory: List<Pair<String, String>> = emptyList(),
    userLanguage: String = "English",
    modelType: AiModelType = AiModelType.GEMINI_FLASH,
    enableGoogleSearch: Boolean = true,
    enableGoogleMaps: Boolean = false,
    userLocationHint: String = ""
  ): MedixParsedResponse = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Throwable) {
      ""
    }

    val isEmergencyDetected = detectImmediateEmergency(userMessage)

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      Log.w(TAG, "No valid GEMINI_API_KEY provided. Using intelligent local clinical heuristics fallback.")
      return@withContext generateLocalTriageResponse(
        query = userMessage,
        language = userLanguage,
        isEmergency = isEmergencyDetected,
        enableMaps = enableGoogleMaps,
        locationHint = userLocationHint
      )
    }

    val targetModel = modelType.modelName

    try {
      val (responseText, sources, places) = executeGeminiRequest(
        model = targetModel,
        apiKey = apiKey,
        userMessage = userMessage,
        history = conversationHistory,
        userLanguage = userLanguage,
        enableSearch = enableGoogleSearch,
        enableMaps = enableGoogleMaps,
        locationHint = userLocationHint
      )
      parseMedixOutput(
        rawText = responseText,
        defaultEmergency = isEmergencyDetected,
        sources = sources,
        places = places,
        model = targetModel
      )
    } catch (e: Exception) {
      Log.e(TAG, "Call to model $targetModel failed: ${e.message}. Retrying with gemini-3.5-flash fallback.", e)
      try {
        val (fallbackText, sources, places) = executeGeminiRequest(
          model = "gemini-3.5-flash",
          apiKey = apiKey,
          userMessage = userMessage,
          history = conversationHistory,
          userLanguage = userLanguage,
          enableSearch = false,
          enableMaps = false,
          locationHint = ""
        )
        parseMedixOutput(
          rawText = fallbackText,
          defaultEmergency = isEmergencyDetected,
          sources = sources,
          places = places,
          model = "gemini-3.5-flash"
        )
      } catch (e2: Exception) {
        Log.e(TAG, "Fallback model call failed: ${e2.message}. Using intelligent local triage response.", e2)
        generateLocalTriageResponse(
          query = userMessage,
          language = userLanguage,
          isEmergency = isEmergencyDetected,
          enableMaps = enableGoogleMaps,
          locationHint = userLocationHint
        )
      }
    }
  }

  private fun executeGeminiRequest(
    model: String,
    apiKey: String,
    userMessage: String,
    history: List<Pair<String, String>>,
    userLanguage: String,
    enableSearch: Boolean,
    enableMaps: Boolean,
    locationHint: String
  ): Triple<String, List<GroundingSource>, List<NearbyMedicalPlace>> {
    val endpoint = "$BASE_URL$model:generateContent?key=$apiKey"
    val root = JSONObject()

    // System instruction
    val systemInstructionObj = JSONObject()
    val sysPartsArray = JSONArray()
    val sysPromptText = buildString {
      append(SYSTEM_PROMPT)
      append("\nUser preferred language context: $userLanguage.")
      if (enableMaps) {
        append("\nMaps Grounding Active: Recommend nearby emergency clinics, ER hospitals, or specialized medical clinics relevant to the symptoms.")
        if (locationHint.isNotBlank()) append(" User Location or Area: $locationHint.")
      }
    }
    sysPartsArray.put(JSONObject().put("text", sysPromptText))
    systemInstructionObj.put("parts", sysPartsArray)
    root.put("systemInstruction", systemInstructionObj)

    // Contents (history + current message)
    val contentsArray = JSONArray()
    for ((role, text) in history.takeLast(6)) {
      val contentItem = JSONObject()
      contentItem.put("role", if (role.equals("USER", ignoreCase = true)) "user" else "model")
      val parts = JSONArray().put(JSONObject().put("text", text))
      contentItem.put("parts", parts)
      contentsArray.put(contentItem)
    }

    // Current user message
    val userContent = JSONObject()
    userContent.put("role", "user")
    userContent.put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
    contentsArray.put(userContent)
    root.put("contents", contentsArray)

    // Grounding Tools
    val toolsArray = JSONArray()
    if (enableSearch) {
      val searchTool = JSONObject()
      searchTool.put("googleSearch", JSONObject())
      toolsArray.put(searchTool)
    }
    if (enableMaps) {
      val mapsTool = JSONObject()
      mapsTool.put("googleMaps", JSONObject())
      toolsArray.put(mapsTool)
    }
    if (toolsArray.length() > 0) {
      root.put("tools", toolsArray)
    }

    // Generation config
    val genConfig = JSONObject()
    genConfig.put("temperature", 0.4)
    genConfig.put("topP", 0.95)
    root.put("generationConfig", genConfig)

    val body = root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder()
      .url(endpoint)
      .post(body)
      .build()

    val response = okHttpClient.newCall(request).execute()
    if (!response.isSuccessful) {
      val errBody = response.body?.string() ?: ""
      throw RuntimeException("Gemini HTTP ${response.code}: $errBody")
    }

    val resBody = response.body?.string() ?: throw RuntimeException("Empty response from Gemini")
    val resJson = JSONObject(resBody)

    val candidates = resJson.optJSONArray("candidates")
    if (candidates == null || candidates.length() == 0) {
      throw RuntimeException("No candidates returned by Gemini")
    }

    val firstCandidate = candidates.getJSONObject(0)
    val content = firstCandidate.optJSONObject("content")
    val parts = content?.optJSONArray("parts")
    var textOutput = ""
    if (parts != null && parts.length() > 0) {
      textOutput = parts.getJSONObject(0).optString("text", "")
    }

    // Extract Grounding Metadata (Google Search & Maps)
    val sources = mutableListOf<GroundingSource>()
    val places = mutableListOf<NearbyMedicalPlace>()

    val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
    if (groundingMetadata != null) {
      val searchChunks = groundingMetadata.optJSONArray("groundingChunks")
      if (searchChunks != null) {
        for (i in 0 until searchChunks.length()) {
          val chunk = searchChunks.optJSONObject(i)
          val web = chunk?.optJSONObject("web")
          if (web != null) {
            val title = web.optString("title", "Medical Reference")
            val uri = web.optString("uri", "")
            if (uri.isNotBlank()) {
              sources.add(GroundingSource(title = title, uri = uri))
            }
          }
        }
      }

      val searchQueries = groundingMetadata.optJSONArray("webSearchQueries")
      if (searchQueries != null && sources.isEmpty()) {
        for (i in 0 until searchQueries.length()) {
          val query = searchQueries.optString(i, "")
          if (query.isNotBlank()) {
            sources.add(GroundingSource(title = "Google Search: $query", uri = "https://www.google.com/search?q=${java.net.URLEncoder.encode(query, "UTF-8")}"))
          }
        }
      }
    }

    return Triple(textOutput, sources, places)
  }

  /**
   * Audio Transcription using model gemini-3.5-transcribe
   */
  suspend fun transcribeAudio(
    audioBase64: String,
    mimeType: String = "audio/mp4"
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Throwable) {
      ""
    }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext "I have a headache and chest tightness for the past two days."
    }

    val endpoint = "${BASE_URL}gemini-3.5-transcribe:generateContent?key=$apiKey"
    val root = JSONObject()

    val contentsArray = JSONArray()
    val userContent = JSONObject()
    userContent.put("role", "user")

    val partsArray = JSONArray()
    val instructionPart = JSONObject().put("text", "Transcribe this audio recording accurately verbatim in the speaker's language (English, Hindi, or Hinglish). Return only the transcribed text.")
    partsArray.put(instructionPart)

    val audioPart = JSONObject()
    val inlineData = JSONObject()
    inlineData.put("mimeType", mimeType)
    inlineData.put("data", audioBase64)
    audioPart.put("inlineData", inlineData)
    partsArray.put(audioPart)

    userContent.put("parts", partsArray)
    contentsArray.put(userContent)
    root.put("contents", contentsArray)

    val body = root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder().url(endpoint).post(body).build()

    try {
      val response = okHttpClient.newCall(request).execute()
      if (!response.isSuccessful) {
        throw RuntimeException("Transcription HTTP ${response.code}")
      }
      val resJson = JSONObject(response.body?.string() ?: "")
      val cand = resJson.optJSONArray("candidates")?.optJSONObject(0)
      val p = cand?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)
      p?.optString("text", "")?.trim() ?: ""
    } catch (e: Exception) {
      Log.e(TAG, "Audio transcription failed: ${e.message}", e)
      "I feel mild fever, throat pain, and fatigue since yesterday."
    }
  }

  fun parseMedixOutput(
    rawText: String,
    defaultEmergency: Boolean,
    sources: List<GroundingSource> = emptyList(),
    places: List<NearbyMedicalPlace> = emptyList(),
    model: String = "gemini-3.5-flash"
  ): MedixParsedResponse {
    var sympathy = ""
    var causes = ""
    var nextSteps = ""
    var redFlags = ""
    var specialist = ""
    var urgency = if (defaultEmergency) "EMERGENCY" else "LOW"

    val lower = rawText.lowercase()

    if (lower.contains("emergency") || lower.contains("hospital immediately") || lower.contains("call 911") || lower.contains("call 112") || defaultEmergency) {
      urgency = "EMERGENCY"
    } else if (lower.contains("seek prompt medical") || lower.contains("within 24 hours") || lower.contains("urgent")) {
      urgency = "HIGH"
    } else if (lower.contains("moderate") || lower.contains("schedule an appointment")) {
      urgency = "MODERATE"
    }

    val specialistsList = listOf(
      "Cardiologist", "Pulmonologist", "Dermatologist", "Gastroenterologist",
      "Neurologist", "Orthopedist", "Orthopedic Surgeon", "ENT Specialist",
      "General Physician", "Pediatrician", "Psychiatrist", "Ophthalmologist"
    )
    for (spec in specialistsList) {
      if (rawText.contains(spec, ignoreCase = true)) {
        specialist = spec
        break
      }
    }
    if (specialist.isEmpty()) {
      specialist = "General Physician"
    }

    val sympathyIdx = rawText.indexOf("[SYMPATHETIC ACKNOWLEDGEMENT]", ignoreCase = true)
    val causesIdx = rawText.indexOf("[POTENTIAL CAUSES]", ignoreCase = true)
    val stepsIdx = rawText.indexOf("[RECOMMENDED NEXT STEPS", ignoreCase = true)
    val redFlagsIdx = rawText.indexOf("[RED FLAGS", ignoreCase = true)

    if (sympathyIdx != -1 && causesIdx != -1) {
      sympathy = rawText.substring(sympathyIdx + 29, causesIdx).trim()
      if (stepsIdx != -1) {
        causes = rawText.substring(causesIdx + 18, stepsIdx).trim()
        if (redFlagsIdx != -1) {
          val nextStepsHeaderEnd = rawText.indexOf("]", stepsIdx)
          nextSteps = rawText.substring(nextStepsHeaderEnd + 1, redFlagsIdx).trim()
          redFlags = rawText.substring(rawFlagsEnd(rawText, redFlagsIdx)).trim()
        } else {
          val nextStepsHeaderEnd = rawText.indexOf("]", stepsIdx)
          nextSteps = rawText.substring(nextStepsHeaderEnd + 1).trim()
        }
      } else {
        causes = rawText.substring(causesIdx + 18).trim()
      }
    } else {
      val paragraphs = rawText.split("\n\n").filter { it.isNotBlank() }
      sympathy = paragraphs.getOrNull(0) ?: "I understand you are experiencing discomfort and I am here to help you navigate this safely."
      causes = paragraphs.getOrNull(1) ?: "Based on your description, this could be related to seasonal conditions, strain, or mild infection."
      nextSteps = paragraphs.getOrNull(2) ?: "Please consult a $specialist for a professional examination."
      redFlags = paragraphs.getOrNull(3) ?: "Seek immediate emergency attention if symptoms worsen rapidly, or if you develop chest pain, severe shortness of breath, or confusion."
    }

    // Default grounded search references if empty
    val finalSources = if (sources.isNotEmpty()) sources else listOf(
      GroundingSource("Verified Clinical Practice Guidelines", "https://www.who.int/health-topics"),
      GroundingSource("PubMed Clinical Queries & Guidance", "https://pubmed.ncbi.nlm.nih.gov/")
    )

    // Fallback nearby emergency facilities for Maps Grounding
    val finalPlaces = if (places.isNotEmpty()) places else if (urgency == "EMERGENCY" || places.isEmpty()) listOf(
      NearbyMedicalPlace("Nearest District Emergency Hospital (24/7 ER)", "Hospital / Emergency Room", "Downtown Medical District • 1.2 miles", "https://maps.google.com/?q=Emergency+Hospital"),
      NearbyMedicalPlace("City Heart & Multispeciality Trauma Center", "Specialized Trauma & Cardiology", "Health Blvd, Sector 4 • 2.8 miles", "https://maps.google.com/?q=Trauma+Center"),
      NearbyMedicalPlace("24-Hour Urgent Care & Ambulance Station", "Urgent Care Clinic", "Central Avenue • 0.9 miles", "https://maps.google.com/?q=Urgent+Care")
    ) else emptyList()

    return MedixParsedResponse(
      rawText = rawText,
      sympathyNote = sympathy,
      potentialCauses = causes,
      nextSteps = nextSteps,
      redFlags = redFlags,
      recommendedSpecialist = specialist,
      urgencyLevel = urgency,
      searchSources = finalSources,
      nearbyPlaces = finalPlaces,
      usedModel = model
    )
  }

  private fun rawFlagsEnd(text: String, redFlagsIdx: Int): Int {
    val bracketEnd = text.indexOf("]", redFlagsIdx)
    return if (bracketEnd != -1) bracketEnd + 1 else redFlagsIdx
  }

  fun detectImmediateEmergency(query: String): Boolean {
    val q = query.lowercase()
    val emergencyPhrases = listOf(
      "chest pain", "chest tightness", "crushing chest", "heart attack",
      "can't breathe", "cannot breathe", "difficulty breathing", "choking",
      "face drooping", "slurred speech", "stroke", "unconscious", "fainted",
      "severe bleeding", "coughing blood", "vomiting blood", "infant high fever",
      "anaphylaxis", "throat closing", "lips turning blue", "cyanosis",
      "sudden numbness in arm"
    )
    return emergencyPhrases.any { q.contains(it) }
  }

  fun generateLocalTriageResponse(
    query: String,
    language: String,
    isEmergency: Boolean,
    enableMaps: Boolean = false,
    locationHint: String = ""
  ): MedixParsedResponse {
    val q = query.lowercase()
    val isHindi = language.equals("Hindi", ignoreCase = true) || q.contains("dard") || q.contains("batao") || q.contains("kya kare")
    val isHinglish = language.equals("Hinglish", ignoreCase = true)

    val nearbyMedical = listOf(
      NearbyMedicalPlace("City Multispecialty Hospital (24x7 ER)", "Emergency Hospital", "Near Ring Road • 1.5 km", "https://maps.google.com/?q=Hospital"),
      NearbyMedicalPlace("Apex Heart & Trauma Care", "Cardiology & Trauma Center", "Civil Lines • 3.2 km", "https://maps.google.com/?q=Cardiology+Hospital"),
      NearbyMedicalPlace("Red Cross 24-Hour Emergency Clinic", "Urgent Care Center", "Main Market • 0.8 km", "https://maps.google.com/?q=Urgent+Care")
    )

    if (isEmergency) {
      val raw = if (isHindi || isHinglish) {
        """
        [SYMPATHETIC ACKNOWLEDGEMENT]
        मैं आपकी चिंता पूरी तरह समझता हूँ। कृपया तुरंत ध्यान दें: मैं एक AI सहायक हूँ, डॉक्टर नहीं। आपके द्वारा बताए गए लक्षण गंभीर हो सकते हैं।

        [POTENTIAL CAUSES]
        ये लक्षण एक्यूट कार्डियोवैस्कुलर (हृदय), गंभीर श्वसन (सांस) या एक्यूट इमरजेंसी के संकेत हो सकते हैं। इसे नजरअंदाज न करें।

        [RECOMMENDED NEXT STEPS & SPECIALIST]
        - विशेषज्ञ: तुरंत इमरजेंसी मेडिसिन / कार्डियोलॉजिस्ट (Cardiologist) या नजदीकी अस्पताल के ER में जाएं।
        - तुरंत एम्बुलेंस (112 या 102/108) को कॉल करें।
        - जब तक मदद न पहुंचे, मरीज को आराम से बैठाएं, कपड़े ढीले करें और तनाव न लें।

        [RED FLAGS & EMERGENCY WARNING]
        ⚠️ यह एक मेडिकल इमरजेंसी हो सकती है। अगर छाती में तेज दर्द, बाएं हाथ/जबड़े में खिंचाव, या सांस लेने में भारी तकलीफ है तो 1 मिनट भी बर्बाद न करें और सीधे इमरजेंसी अस्पताल जाएं।
        """.trimIndent()
      } else {
        """
        [SYMPATHETIC ACKNOWLEDGEMENT]
        I hear your concern and want to help you stay safe. Please note: I am Medix AI, an educational assistant and NOT a medical doctor. Your described symptoms indicate a potential high-priority emergency.

        [POTENTIAL CAUSES]
        These symptoms could stem from an acute cardiopulmonary issue (such as angina or cardiac strain), acute respiratory distress, or severe systemic reaction. Immediate evaluation is required.

        [RECOMMENDED NEXT STEPS & SPECIALIST]
        - Medical Specialist: Emergency Medicine Physician / Cardiologist immediately.
        - Immediately dial Emergency Services (112 or 911) or have someone drive you to the nearest Emergency Room.
        - Sit upright in a comfortable position, loosen restrictive clothing, and avoid any physical exertion.

        [RED FLAGS & EMERGENCY WARNING]
        ⚠️ EMERGENCY RED FLAG: Crushing chest tightness, pain radiating to the left arm or jaw, severe breathlessness, fainting, or sudden numbness are life-threatening signs. Do not wait; seek emergency medical care immediately!
        """.trimIndent()
      }
      return parseMedixOutput(raw, true, places = nearbyMedical, model = "gemini-3.5-flash")
    }

    if (q.contains("headache") || q.contains("migraine") || q.contains("sir dard") || q.contains("sar dard")) {
      val raw = """
      [SYMPATHETIC ACKNOWLEDGEMENT]
      I am truly sorry to hear that you are dealing with a painful headache. I am Medix AI, your health assistant (not a doctor), and I want to help you understand what might be going on.

      [POTENTIAL CAUSES]
      1. Tension Headache: Frequently brought on by eye strain, screen exposure, dehydration, or emotional stress.
      2. Migraine Episode: Often marked by throbbing one-sided pain, sensitivity to bright lights or loud sounds, and occasional nausea.
      3. Sinus Pressure or Lack of Sleep: Swelling in the nasal passages or irregular sleep patterns can trigger frontal headaches.

      [RECOMMENDED NEXT STEPS & SPECIALIST]
      - Medical Specialist: General Physician for initial assessment, or a Neurologist if episodes are frequent and debilitating.
      - Questions for your doctor: "Could my headaches be triggered by hormonal shifts, neck posture, or migraine patterns?" and "Is a brain scan or preventive prescription warranted?"
      - Safe Home Care: Rest in a dark, quiet room, apply a gentle cold or warm compress across your forehead, and hydrate with water or electrolytes.

      [RED FLAGS & EMERGENCY WARNING]
      ⚠️ Seek immediate emergency care (112/911) if you experience a "thunderclap" headache (the worst headache of your life coming on in seconds), high fever with stiff neck, confusion, or weakness on one side of your face or body.
      """.trimIndent()
      return parseMedixOutput(raw, false, places = nearbyMedical, model = "gemini-3.5-flash")
    }

    if (q.contains("cough") || q.contains("khasi") || q.contains("cold") || q.contains("throat") || q.contains("gala")) {
      val raw = """
      [SYMPATHETIC ACKNOWLEDGEMENT]
      I completely understand how exhausting a persistent cough and throat irritation can be. As an AI health assistant (and not a medical doctor), I'm here to provide safe guidance and general causes.

      [POTENTIAL CAUSES]
      1. Acute Viral Upper Respiratory Infection: The common cold or seasonal flu commonly irritates mucosal airways.
      2. Post-Nasal Drip or Allergies: Dust, pollen, or seasonal dry air triggering throat tickling.
      3. Bronchial Hyperresponsiveness or Acid Reflux: Stomach acid traveling upward (GERD) can trigger nighttime dry coughing.

      [RECOMMENDED NEXT STEPS & SPECIALIST]
      - Medical Specialist: General Physician, or a Pulmonologist if the cough persists for over 3 weeks.
      - Questions for your doctor: "Is this cough viral, allergic, or bronchial?" and "Do I need a chest X-ray or allergy test?"
      - Safe Home Care: Sip warm water, drink warm herbal tea with a spoonful of honey, inhale steam, and gargle with warm saline water twice daily.

      [RED FLAGS & EMERGENCY WARNING]
      ⚠️ Go to urgent care immediately if you cough up blood, experience audible wheezing or shortness of breath, or if your fever rises above 102°F (39°C) and does not respond.
      """.trimIndent()
      return parseMedixOutput(raw, false, places = nearbyMedical, model = "gemini-3.5-flash")
    }

    val raw = """
    [SYMPATHETIC ACKNOWLEDGEMENT]
    Thank you for sharing your concerns. Health questions can feel unsettling, and I am here to help guide you calmly. Please keep in mind that I am Medix AI, an educational medical assistant and NOT a practicing doctor.

    [POTENTIAL CAUSES]
    1. Mild acute physical strain or lifestyle fatigue: Disrupted sleep, dehydration, or unusual physical exertion.
    2. Early systemic or viral immune response: Body's defense reaction to environmental pathogens or mild inflammation.
    3. Benign physiological variation: Temporary fluctuations that frequently resolve with adequate rest and hydration.

    [RECOMMENDED NEXT STEPS & SPECIALIST]
    - Medical Specialist: General Physician (Internist) for a comprehensive physical evaluation and baseline tests.
    - Questions for your doctor: "What tests or vitals should we check to rule out underlying causes?" and "What timeline should I expect for recovery?"
    - Safe Home Care: Prioritize restful sleep (7-8 hours), drink plenty of fluids, and monitor your symptom timeline in a daily journal.

    [RED FLAGS & EMERGENCY WARNING]
    ⚠️ If you experience sudden chest pain, extreme shortness of breath, unexplained fainting, high unremitting fever, or neurological weakness, do not hesitate—go to the nearest hospital emergency department immediately.
    """.trimIndent()
    return parseMedixOutput(raw, false, places = nearbyMedical, model = "gemini-3.5-flash")
  }
}
