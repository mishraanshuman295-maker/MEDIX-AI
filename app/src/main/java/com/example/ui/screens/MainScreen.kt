package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LiveVoiceCallModal
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.EmergencyRedOnContainer
import com.example.ui.theme.MedixTealPrimary
import com.example.ui.viewmodel.MedixViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  viewModel: MedixViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) }

  val messages by viewModel.messages.collectAsState()
  val isThinking by viewModel.isThinking.collectAsState()
  val selectedLanguage by viewModel.selectedLanguage.collectAsState()
  val selectedModel by viewModel.selectedModel.collectAsState()
  val searchGroundingEnabled by viewModel.searchGroundingEnabled.collectAsState()
  val mapsGroundingEnabled by viewModel.mapsGroundingEnabled.collectAsState()
  val isRecordingAudio by viewModel.isRecordingAudio.collectAsState()
  val isTranscribing by viewModel.isTranscribing.collectAsState()
  val liveCallActive by viewModel.liveCallActive.collectAsState()
  val liveTranscribedText by viewModel.liveTranscribedText.collectAsState()
  val liveAiSpeechResponse by viewModel.liveAiSpeechResponse.collectAsState()

  val emergencyAlertActive by viewModel.emergencyBannerActive.collectAsState()
  val sessions by viewModel.sessions.collectAsState()
  val bookmarks by viewModel.bookmarks.collectAsState()
  val profile by viewModel.profile.collectAsState()
  val currentSessionId by viewModel.currentSessionId.collectAsState()
  val guidedState by viewModel.guidedState.collectAsState()

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
              painter = painterResource(id = R.drawable.medix_logo),
              contentDescription = "Medix AI Logo",
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Medix AI",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        navigationIcon = {
          // One-touch Emergency SOS button
          Surface(
            onClick = {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
              context.startActivity(intent)
            },
            shape = RoundedCornerShape(20.dp),
            color = EmergencyRedContainer,
            modifier = Modifier
              .padding(start = 12.dp)
              .testTag("top_bar_sos_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Emergency,
                contentDescription = "Emergency Call",
                tint = EmergencyRed,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "SOS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EmergencyRedOnContainer
              )
            }
          }
        },
        actions = {
          // Voice Call Action Icon
          IconButton(
            onClick = { viewModel.startLiveCall() },
            modifier = Modifier.testTag("top_voice_call_button")
          ) {
            Icon(
              imageVector = Icons.Default.RecordVoiceOver,
              contentDescription = "Live Voice Session",
              tint = MedixTealPrimary
            )
          }

          if (selectedTab == 0) {
            IconButton(
              onClick = { viewModel.createNewSession() },
              modifier = Modifier.testTag("new_chat_button")
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Consultation",
                tint = MedixTealPrimary
              )
            }
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("bottom_navigation_bar")
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Consultation") },
          label = { Text("Consult", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MedixTealPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_item_consult")
        )

        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = { Icon(Icons.Default.MedicalInformation, contentDescription = "Symptom Checker") },
          label = { Text("Triage", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MedixTealPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_item_triage")
        )

        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Specialists") },
          label = { Text("Doctors", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MedixTealPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_item_specialists")
        )

        NavigationBarItem(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          icon = { Icon(Icons.Default.LocalHospital, contentDescription = "First-Aid") },
          label = { Text("First-Aid", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = EmergencyRed,
            indicatorColor = EmergencyRedContainer
          ),
          modifier = Modifier.testTag("nav_item_firstaid")
        )

        NavigationBarItem(
          selected = selectedTab == 4,
          onClick = { selectedTab = 4 },
          icon = { Icon(Icons.Default.Person, contentDescription = "History & Profile") },
          label = { Text("Profile", fontSize = 11.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MedixTealPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_item_profile")
        )
      }
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> ConsultationScreen(
          messages = messages,
          isThinking = isThinking,
          selectedLanguage = selectedLanguage,
          selectedModel = selectedModel,
          onModelSelected = { viewModel.setModel(it) },
          searchGroundingEnabled = searchGroundingEnabled,
          onToggleSearchGrounding = { viewModel.toggleSearchGrounding(it) },
          mapsGroundingEnabled = mapsGroundingEnabled,
          onToggleMapsGrounding = { viewModel.toggleMapsGrounding(it) },
          isRecordingAudio = isRecordingAudio,
          isTranscribing = isTranscribing,
          onStartAudioRecording = { viewModel.startAudioRecording() },
          onStopAudioRecording = { callback -> viewModel.stopAudioRecordingAndTranscribe(callback) },
          onStartLiveCall = { viewModel.startLiveCall() },
          emergencyAlertActive = emergencyAlertActive,
          onDismissEmergency = { viewModel.dismissEmergencyBanner() },
          onSendMessage = { viewModel.sendMessage(it) },
          onToggleBookmark = { id, bm -> viewModel.toggleBookmark(id, bm) },
          onNewSession = { viewModel.createNewSession() },
          onCycleLanguage = {
            val next = when (selectedLanguage) {
              "English" -> "Hindi"
              "Hindi" -> "Hinglish"
              else -> "English"
            }
            viewModel.setLanguage(next)
          },
          onSpecialistClick = { specialistName ->
            selectedTab = 2 // navigate to Doctors tab
          }
        )
        1 -> SymptomCheckerScreen(
          state = guidedState,
          onSelectRegion = { viewModel.selectGuidedRegion(it) },
          onToggleSymptom = { viewModel.toggleGuidedSymptom(it) },
          onDurationChange = { viewModel.setGuidedDuration(it) },
          onSeverityChange = { viewModel.setGuidedSeverity(it) },
          onRedFlagsChange = { viewModel.setGuidedRedFlags(it) },
          onNotesChange = { viewModel.setGuidedNotes(it) },
          onRunAnalysis = { viewModel.runGuidedAnalysis() },
          onReset = { viewModel.resetGuidedAssessment() },
          onSpecialistClick = { selectedTab = 2 }
        )
        2 -> SpecialistsScreen(
          onAskAboutSpecialist = { specialistName ->
            selectedTab = 0
            viewModel.sendMessage("I want to consult a $specialistName. What symptoms warrant this and what tests or questions should I prepare?")
          }
        )
        3 -> FirstAidScreen()
        4 -> HistoryProfileScreen(
          sessions = sessions,
          bookmarks = bookmarks,
          profile = profile,
          currentSessionId = currentSessionId,
          onSelectSession = {
            viewModel.selectSession(it)
            selectedTab = 0
          },
          onDeleteSession = { viewModel.deleteSession(it) },
          onUpdateProfile = { n, a, al, c, l -> viewModel.updateProfile(n, a, al, c, l) },
          onToggleBookmark = { id, bm -> viewModel.toggleBookmark(id, bm) },
          onSpecialistClick = { selectedTab = 2 }
        )
      }

      // Real-Time Live Voice Call Modal (gemini-3.8-live)
      LiveVoiceCallModal(
        isActive = liveCallActive,
        isListening = isRecordingAudio,
        transcribedText = liveTranscribedText,
        aiSpeechResponse = liveAiSpeechResponse,
        onEndCall = { viewModel.endLiveCall() },
        onToggleMic = { viewModel.toggleLiveMic() }
      )
    }
  }
}
