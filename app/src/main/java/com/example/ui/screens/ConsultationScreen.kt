package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.ChatMessageEntity
import com.example.data.model.SymptomCategoriesCatalog
import com.example.data.remote.AiModelType
import com.example.ui.components.EmergencyAlertBanner
import com.example.ui.components.GeminiControlPanel
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.MedixResponseCard
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.MedixCyanSecondary
import com.example.ui.theme.MedixTealPrimary
import com.example.ui.theme.SafeEmerald
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConsultationScreen(
  messages: List<ChatMessageEntity>,
  isThinking: Boolean,
  selectedLanguage: String,
  selectedModel: AiModelType,
  onModelSelected: (AiModelType) -> Unit,
  searchGroundingEnabled: Boolean,
  onToggleSearchGrounding: (Boolean) -> Unit,
  mapsGroundingEnabled: Boolean,
  onToggleMapsGrounding: (Boolean) -> Unit,
  isRecordingAudio: Boolean,
  isTranscribing: Boolean,
  onStartAudioRecording: () -> Unit,
  onStopAudioRecording: ((String) -> Unit) -> Unit,
  onStartLiveCall: () -> Unit,
  emergencyAlertActive: Boolean,
  onDismissEmergency: () -> Unit,
  onSendMessage: (String) -> Unit,
  onToggleBookmark: (Long, Boolean) -> Unit,
  onNewSession: () -> Unit,
  onCycleLanguage: () -> Unit,
  onSpecialistClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val context = LocalContext.current

  val audioPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      onStartAudioRecording()
    }
  }

  LaunchedEffect(messages.size, isThinking) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // High Priority Emergency Banner if triggered
    EmergencyAlertBanner(
      isVisible = emergencyAlertActive,
      onDismiss = onDismissEmergency,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )

    // Gemini Control Panel: Model selection (Pro, Flash, Lite, Live) & Grounding (Search, Maps)
    GeminiControlPanel(
      selectedModel = selectedModel,
      onModelSelected = onModelSelected,
      searchGroundingEnabled = searchGroundingEnabled,
      onToggleSearchGrounding = onToggleSearchGrounding,
      mapsGroundingEnabled = mapsGroundingEnabled,
      onToggleMapsGrounding = onToggleMapsGrounding,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
    )

    // Live Voice Consultation Quick Banner
    Surface(
      onClick = onStartLiveCall,
      shape = RoundedCornerShape(12.dp),
      color = MedixCyanSecondary.copy(alpha = 0.12f),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 2.dp)
        .testTag("start_live_voice_bar")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.RecordVoiceOver,
          contentDescription = null,
          tint = MedixTealPrimary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Have a Voice Conversation (gemini-3.8-live)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MedixTealPrimary
          )
          Text(
            text = "Speak aloud with real-time AI audio responses",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MedixTealPrimary
        ) {
          Text(
            text = "Start Voice",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Chat scrollable thread
    LazyColumn(
      state = listState,
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .testTag("consultation_message_list")
    ) {
      item {
        MedicalDisclaimerCard(compact = true)
      }

      // Empty State Hero Card
      if (messages.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("empty_state_hero_card")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Image(
                painter = painterResource(id = R.drawable.medix_hero_banner),
                contentDescription = "Medix AI Assistant Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp)
                  .clip(RoundedCornerShape(14.dp))
              )

              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Welcome to Medix AI",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Empathetic medical assistant powered by Gemini 3.5 Flash & 3.1 Pro with real-time Google Search and Maps Grounding. Describe your symptoms below, tap the mic to speak, or start a live voice call.",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
              )

              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "Tap a symptom prompt to start consultation:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )

              Spacer(modifier = Modifier.height(8.dp))
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SymptomCategoriesCatalog.quickSymptomPrompts.take(4).forEach { prompt ->
                  FilterChip(
                    selected = false,
                    onClick = {
                      inputText = prompt
                      onSendMessage(prompt)
                      inputText = ""
                    },
                    label = { Text(prompt, fontSize = 12.sp) },
                    leadingIcon = {
                      Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                      )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("quick_symptom_chip_${prompt.hashCode()}")
                  )
                }
              }
            }
          }
        }
      }

      // Messages in the conversational chat thread
      items(messages, key = { it.id }) { msg ->
        if (msg.sender == "USER") {
          UserMessageBubble(message = msg)
        } else {
          MedixResponseCard(
            message = msg,
            onToggleBookmark = onToggleBookmark,
            onSpecialistClick = onSpecialistClick
          )
        }
      }

      // Thinking indicator
      if (isThinking) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().testTag("medix_thinking_indicator")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(16.dp)
            ) {
              CircularProgressIndicator(
                strokeWidth = 2.5.dp,
                color = MedixTealPrimary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Medix AI (${selectedModel.modelName}) is thinking...",
                  fontWeight = FontWeight.Medium,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (searchGroundingEnabled) "Grounding with Google Search & Maps clinical data..." else "Evaluating clinical causes, specialists & red flags",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
              }
            }
          }
        }
      }

      // Transcribing indicator
      if (isTranscribing) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("transcribing_indicator")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(12.dp)
            ) {
              CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Transcribing audio with gemini-3.5-transcribe...", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
          }
        }
      }
    }

    // Quick symptom tags row above input
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(SymptomCategoriesCatalog.quickSymptomPrompts) { tag ->
        FilterChip(
          selected = false,
          onClick = { onSendMessage(tag) },
          label = { Text(tag, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.testTag("tag_chip_${tag.hashCode()}")
        )
      }
    }

    // Recording Active Banner
    if (isRecordingAudio) {
      Surface(
        color = EmergencyRedContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(EmergencyRed)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Recording audio... Speak your symptoms now",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.weight(1f)
          )
          Button(
            onClick = {
              onStopAudioRecording { transcribed ->
                inputText = transcribed
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("done_recording_button")
          ) {
            Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Done & Transcribe", fontSize = 11.sp, color = Color.White)
          }
        }
      }
    }

    // Input Bar: Language + Text Input + Mic (gemini-3.5-transcribe) + Send
    Surface(
      tonalElevation = 4.dp,
      color = MaterialTheme.colorScheme.surface,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp)
      ) {
        // Language Badge / Switcher
        Surface(
          onClick = onCycleLanguage,
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.secondaryContainer,
          modifier = Modifier.testTag("language_cycle_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Translate,
              contentDescription = "Switch Language",
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = selectedLanguage,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Mic Transcription Button
        IconButton(
          onClick = {
            if (isRecordingAudio) {
              onStopAudioRecording { transcribed ->
                inputText = transcribed
              }
            } else {
              val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
              ) == PackageManager.PERMISSION_GRANTED

              if (hasPermission) {
                onStartAudioRecording()
              } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
              }
            }
          },
          colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isRecordingAudio) EmergencyRed else MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier
            .size(40.dp)
            .testTag("mic_transcribe_button")
        ) {
          Icon(
            imageVector = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = "Speak to Transcribe",
            tint = if (isRecordingAudio) Color.White else MedixTealPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = { Text("Describe symptoms or tap mic...", fontSize = 13.sp) },
          maxLines = 4,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedixTealPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("symptom_input_textfield")
        )

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(
          onClick = {
            if (inputText.isNotBlank() && !isThinking) {
              onSendMessage(inputText)
              inputText = ""
            }
          },
          enabled = inputText.isNotBlank() && !isThinking,
          colors = IconButtonDefaults.iconButtonColors(
            containerColor = MedixTealPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier
            .size(42.dp)
            .testTag("send_message_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message",
            tint = if (inputText.isNotBlank() && !isThinking) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun UserMessageBubble(
  message: ChatMessageEntity,
  modifier: Modifier = Modifier
) {
  val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

  Row(
    horizontalArrangement = Arrangement.End,
    modifier = modifier.fillMaxWidth()
  ) {
    Card(
      shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
      colors = CardDefaults.cardColors(containerColor = MedixTealPrimary),
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .testTag("user_message_bubble_${message.id}")
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = message.rawText,
          fontSize = 14.sp,
          lineHeight = 19.sp,
          color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = timeFormatted,
          fontSize = 10.sp,
          color = Color.White.copy(alpha = 0.7f),
          modifier = Modifier.align(Alignment.End)
        )
      }
    }
  }
}
