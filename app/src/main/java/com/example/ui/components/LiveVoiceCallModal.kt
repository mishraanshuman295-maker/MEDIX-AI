package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.MedixCyanSecondary
import com.example.ui.theme.MedixTealPrimary
import com.example.ui.theme.SafeEmerald

@Composable
fun LiveVoiceCallModal(
  isActive: Boolean,
  isListening: Boolean,
  transcribedText: String,
  aiSpeechResponse: String,
  onEndCall: () -> Unit,
  onToggleMic: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (!isActive) return

  val pulseScale = remember { Animatable(1f) }

  LaunchedEffect(isListening) {
    if (isListening) {
      pulseScale.animateTo(
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
          animation = tween(800, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    } else {
      pulseScale.snapTo(1f)
    }
  }

  Surface(
    color = Color.Black.copy(alpha = 0.65f),
    modifier = modifier.fillMaxWidth()
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("live_voice_call_modal")
    ) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(20.dp)
        ) {
          // Top Header
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = SafeEmerald.copy(alpha = 0.15f)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SafeEmerald)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "LIVE SESSION",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = SafeEmerald
                )
              }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
              onClick = onEndCall,
              modifier = Modifier.size(28.dp).testTag("close_live_call_button")
            ) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Medix AI Live Voice Doctor",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Powered by gemini-3.8-live • Continuous voice conversation",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )

          Spacer(modifier = Modifier.height(24.dp))

          // Animated Pulsing Mic Visualizer
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(110.dp)
          ) {
            Box(
              modifier = Modifier
                .size(100.dp)
                .scale(pulseScale.value)
                .clip(CircleShape)
                .background(
                  if (isListening) MedixTealPrimary.copy(alpha = 0.25f)
                  else SafeEmerald.copy(alpha = 0.15f)
                )
            )

            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isListening) MedixTealPrimary else MedixCyanSecondary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.RecordVoiceOver,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = if (isListening) "Listening to you speaking..." else "Medix AI is answering...",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = if (isListening) MedixTealPrimary else SafeEmerald
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Speech Bubble display
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(14.dp)
          ) {
            Column {
              Text(
                text = "Live Conversation:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MedixTealPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (transcribedText.isNotBlank()) "You: \"$transcribedText\""
                else "Speak naturally about how you are feeling, pain duration, or vitals...",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (aiSpeechResponse.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Medix AI: \"$aiSpeechResponse\"",
                  fontSize = 12.sp,
                  lineHeight = 16.sp,
                  fontWeight = FontWeight.Medium,
                  color = MedixTealPrimary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Call Controls: Mute/Unmute + End Call
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = onToggleMic,
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) MaterialTheme.colorScheme.secondaryContainer else MedixTealPrimary
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("toggle_live_mic_button")
            ) {
              Icon(
                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = null,
                tint = if (isListening) MaterialTheme.colorScheme.onSecondaryContainer else Color.White,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isListening) "Pause Mic" else "Start Talking",
                fontSize = 13.sp,
                color = if (isListening) MaterialTheme.colorScheme.onSecondaryContainer else Color.White
              )
            }

            Button(
              onClick = onEndCall,
              colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("end_live_call_button")
            ) {
              Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("End Call", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
